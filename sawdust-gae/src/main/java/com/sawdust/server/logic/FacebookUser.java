package com.sawdust.server.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.sawdust.engine.service.Util;
import com.sawdust.engine.service.debug.SawdustSystemError;
import com.sawdust.server.datastore.DataStore;
import com.sawdust.server.datastore.entities.Account;
import com.sawdust.server.datastore.entities.Account.InterfacePreference;
import com.sawdust.server.facebook.ClientResponse;
import com.sawdust.server.facebook.TinyFBClient;
import org.apache.xpath.jaxp.XPathFactoryImpl;

public class FacebookUser
{
    protected static final FacebookSite FACEBOOK_SECRET[] = new FacebookSite[]
    {
            new FacebookSite("48ac90f59799edc11e332278b0f88488", "http://sawdust-games.appspot.com", "http://apps.facebook.com/sawdust-games", "facebook.null"),
            new FacebookSite("ca6de3f83ee41af6e8e0204991b01400", "http://beta.latest.sawdust-games.appspot.com", "http://apps.facebook.com/sawdust-games-beta",
                    "facebook.beta")
    };

    private static final Logger LOG = Logger.getLogger(FacebookUser.class.getName());

    public static String getCalculatedSignature(final HttpServletRequest request, final String secret)
    {
        if (null == secret) return null;
        final String sigString = getParamsToSign(request);
        if (null == sigString) return null;
        final String md5 = Util.md5(sigString + secret);
        return md5;
    }

    public static String getFacebookId(final HttpServletRequest request)
    {
        final FacebookSite facebookId = verifyFacebookSignature(request);
        if (null == facebookId) 
        {
            LOG.fine("Facebook id not found");
            return null;
        }
        final String userId = GetFbParam(request, "fb_sig_user");
        if (null == userId) 
        {
            LOG.fine("Facebook user id");
            return null;
        }
        final String id = "x" + userId + "@" + facebookId.subVersion;

        final com.sawdust.server.datastore.entities.Account account = com.sawdust.server.datastore.entities.Account.Load(id);
        final String userName = getUserName(request, userId, facebookId.apiSecretKey);
        if ((null != userName) && !userName.equals(account.getName()))
        {
            account.setName(userName);
            account.setLogic(new UserLogic()
            {
                @Override
                public void publishActivity(String message)
                {
                    postUserActivity(request, userId, facebookId.apiSecretKey, message);
                }
            });
            account.setInterfacePreference(InterfacePreference.Facebook);
            LOG.info(String.format("Creating new facebook login: %s (%s)", userName, userId));
            DataStore.Save();
        }
        else
        {
            LOG.fine(String.format("Using existing facebook login: %s (%s)", userName, userId));
        }

        return id;
    }

    public static String GetFbParam(final HttpServletRequest request, final String paramName)
    {
        String parameter = request.getParameter(paramName);
        if (null == parameter)
        {
            parameter = request.getParameter("amp;" + paramName);
        }
        return parameter;
    }

    public static String getParamsForTrace(final HttpServletRequest request)
    {
        final ArrayList<String> sigs = sigParams(request);
        final StringBuilder sigString = new StringBuilder();
        for (final String sig : sigs)
        {
            if (sigString.length() > 0)
            {
                sigString.append("\n");
            }
            sigString.append(sig);
        }
        for (final FacebookSite secret : FACEBOOK_SECRET)
        {
            if (sigString.length() > 0)
            {
                sigString.append("\n");
            }
            final String md5 = getCalculatedSignature(request, secret.apiSecretKey);
            sigString.append(String.format("Sig[%s] = %s", secret.facebookSite, md5));
        }
        return sigString.toString();
    }

    public static String getParamsToSign(final HttpServletRequest request)
    {
        final ArrayList<String> sigs = sigParams(request);
        final StringBuilder sigString = new StringBuilder();
        for (final String sig : sigs)
        {
            sigString.append(sig);
        }
        return sigString.toString();
    }

    public static TinyFBClient getProxy(final HttpServletRequest request, final String facebookId)
    {
        final String sessionKey = GetFbParam(request, "fb_sig_session_key");
        final String apiKey = GetFbParam(request, "fb_sig_api_key");
        if ((null == sessionKey) || (null == apiKey)) return null;
        final TinyFBClient fb = new TinyFBClient(apiKey, facebookId, sessionKey);
        return fb;
    }

    protected static String postUserActivity(HttpServletRequest request, String userId, String apiSecretKey, String message)
    {
        LOG.fine("Posting activity for facebook user: " + userId);
        final TinyFBClient fb = getProxy(request, apiSecretKey);
        if (null == fb) throw new SawdustSystemError("Cannot connect to facebook");
        fb.setFormat("XML");

        final TreeMap<String,String> tm = new TreeMap<String, String>();
        tm.put("uid", userId);
        tm.put("message", message);

        final ClientResponse response = fb.getResponse("stream.publish", tm);
        final Document doc = parseResponse(response);
        final String value = getXPath(doc, "//fb:users_getInfo_response/fb:user/fb:name/text()");
        LOG.info("User Name: " + value);
        return value;
    }

    public static String getUserName(final HttpServletRequest request, final String uid, final String facebookId)
    {
        LOG.fine("Requesting user id from facebook for user: " + uid);
        final TinyFBClient fb = getProxy(request, facebookId);
        if (null == fb) throw new SawdustSystemError("Cannot connect to facebook");
        fb.setFormat("XML");

        final TreeMap<String,String> tm = new TreeMap<String, String>();
        tm.put("uids", uid);
        tm.put("fields", "name");

        final ClientResponse response = fb.getResponse("users.getInfo", tm);
        final Document doc = parseResponse(response);
        final String value = getXPath(doc, "//fb:users_getInfo_response/fb:user/fb:name/text()");
        LOG.info("User Name: " + value);
        return value;
    }

    private static String getXPath(final Document doc, final String path)
    {
        if (null == doc) return null;
        final XPathFactory xpathFactory = new org.apache.xpath.jaxp.XPathFactoryImpl();
        final XPath xpath = xpathFactory.newXPath();
        xpath.setNamespaceContext(new NamespaceContext()
        {

            public String getNamespaceURI(final String prefix)
            {
                if (prefix == null) throw new SawdustSystemError("Null prefix");
                else if ("fb".equals(prefix)) return "http://api.facebook.com/1.0/";
                else if ("xml".equals(prefix)) return XMLConstants.XML_NS_URI;
                return XMLConstants.NULL_NS_URI;
            }

            public String getPrefix(final String namespaceURI)
            {
                throw new UnsupportedOperationException();
            }

            public Iterator<?> getPrefixes(final String namespaceURI)
            {
                throw new UnsupportedOperationException();
            }
        });
        XPathExpression expr;
        try
        {
            expr = xpath.compile(path);
            return (String) expr.evaluate(doc, XPathConstants.STRING);
        }
        catch (final XPathExpressionException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private static Document parseResponse(final ClientResponse response)
    {
        DocumentBuilder builder = null;
        Document doc = null;
        try
        {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            builder = factory.newDocumentBuilder();
            doc = builder.parse(response.getEntityInputStream());
            LOG.finer("Facebook API Response: " + doc.toString());
        }
        catch (final ParserConfigurationException e)
        {
            LOG.warning(Util.getFullString(e));
        }
        catch (final SAXException e)
        {
            LOG.warning(Util.getFullString(e));
        }
        catch (final IOException e)
        {
            LOG.warning(Util.getFullString(e));
        }
        return doc;
    }

    private static ArrayList<String> sigParams(final HttpServletRequest request)
    {
        final Enumeration<?> e = request.getParameterNames();
        final ArrayList<String> sigs = new ArrayList<String>();
        while (e.hasMoreElements())
        {
            final String key = (String) e.nextElement();
            String values[];
            values = request.getParameterValues(key);
            for(String value : values)
            {
                String longPrefix = "amp;fb_sig_";
                final boolean overescapedSign = key.startsWith(longPrefix); // Facebook overescape bug
                String prefix = "fb_sig_";
                final boolean sign = key.startsWith(prefix);
                if (sign || overescapedSign)
                {
                    String fbKey = key.substring(overescapedSign ? longPrefix.length() : prefix.length());
                    LOG.finer(String.format("Facebook Param: %s = %s",fbKey,value));
                    sigs.add(fbKey + "=" + value);
                    break;
                }
            }
        }
        Collections.sort(sigs);
        return sigs;
    }

    public static FacebookSite verifyFacebookSignature(final HttpServletRequest request)
    {
        final String signature = GetFbParam(request, "fb_sig");
        if (null == signature)
        {
        	LOG.fine("Facebook signature field not found");
            return null;
        }
        for (final FacebookSite secret : FACEBOOK_SECRET)
        {
            final String md5 = getCalculatedSignature(request, secret.apiSecretKey);
            if ((null != md5) && md5.equals(signature)) 
            {
                LOG.fine("Facebook verified with signature: " + secret.toString().substring(0, 3));
                return secret;
            }
        }
        LOG.warning("Facebook authentication failed:\n" + getParamsForTrace(request));
        return null;
    }

}
