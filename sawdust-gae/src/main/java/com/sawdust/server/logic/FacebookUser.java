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
import com.sawdust.engine.service.debug.RequestLocalLog;
import com.sawdust.engine.service.debug.SawdustSystemError;
import com.sawdust.server.datastore.DataStore;
import com.sawdust.server.datastore.entities.Account;
import com.sawdust.server.datastore.entities.Account.InterfacePreference;
import com.sawdust.server.facebook.ClientResponse;
import com.sawdust.server.facebook.TinyFBClient;
import org.apache.xpath.jaxp.XPathFactoryImpl;

public class FacebookUser
{
    public static class Site
    {
        public final String apiSecretKey;
        public final String facebookSite;
        public final String jspGatewaySite;
        public final String subVersion;

        public Site(final String papiSecretKey, final String pjspGatewaySite, final String pfacebookSite, final String v)
        {
            super();
            apiSecretKey = papiSecretKey;
            jspGatewaySite = pjspGatewaySite;
            facebookSite = pfacebookSite;
            subVersion = v;
        }
    }

    protected static final FacebookUser.Site FACEBOOK_SECRET[] = new FacebookUser.Site[]
    {
            new Site("48ac90f59799edc11e332278b0f88488", "http://sawdust-games.appspot.com", "http://apps.facebook.com/sawdust-games", "facebook.null"),
            new Site("ca6de3f83ee41af6e8e0204991b01400", "http://beta.latest.sawdust-games.appspot.com", "http://apps.facebook.com/sawdust-games-beta",
                    "facebook.beta")
    };;

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
        final Site facebookId = verifyFacebookSignature(request);
        if (null == facebookId) return null;
        final String userId = GetFbParam(request, "fb_sig_user");
        if (null == userId) return null;
        final String id = "x" + userId + "@" + facebookId.subVersion;

        final com.sawdust.server.datastore.entities.Account account = Account.Load(id);
        final String userName = getUserName(request, userId, facebookId.apiSecretKey);
        if ((null != userName) && !userName.equals(account.getName()))
        {
            account.setName(userName);
            account.setInterfacePreference(InterfacePreference.Facebook);
            DataStore.Save();
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
        for (final Site secret : FACEBOOK_SECRET)
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

    public static String getUserName(final HttpServletRequest request, final String uid, final String facebookId)
    {
        final TinyFBClient fb = getProxy(request, facebookId);
        if (null == fb) throw new SawdustSystemError("Cannot connect to facebook");
        fb.setFormat("XML");

        final TreeMap<String,String> tm = new TreeMap<String, String>();
        tm.put("uids", uid);
        tm.put("fields", "name");

        final ClientResponse response = fb.getResponse("users.getInfo", tm);
        final Document doc = parseResponse(response);
        final String value = getXPath(doc, "//fb:users_getInfo_response/fb:user/fb:name/text()");
        LOG.warning("User Name: " + value);
        return value;
    }

    private static String getXPath(final Document doc, final String path)
    {
        if (null == doc) return null;
        // XPathFactory xpathFactory = new javax.xml.xpath.XPathFactory();
        final XPathFactory xpathFactory = new org.apache.xpath.jaxp.XPathFactoryImpl();
        // XPathFactory xpathFactory = XPathFactory.newInstance();
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

            factory.setNamespaceAware(true); // never forget this!
            builder = factory.newDocumentBuilder();
            doc = builder.parse(response.getEntityInputStream());
        }
        catch (final ParserConfigurationException e)
        {
            e.printStackTrace();
        }
        catch (final SAXException e)
        {
            e.printStackTrace();
        }
        catch (final IOException e)
        {
            e.printStackTrace();
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
            final boolean overescapedSign = key.startsWith("amp;fb_sig_"); // Facebook overescape bug
            final boolean sign = key.startsWith("fb_sig_");
            if (sign || overescapedSign)
            {
                if (1 != values.length)
                {
                    continue;
                }
                else
                {
                    sigs.add(key.substring(overescapedSign ? 11 : 7) + "=" + values[0]);
                }
            }
        }
        Collections.sort(sigs);
        return sigs;
    }

    public static Site verifyFacebookSignature(final HttpServletRequest request)
    {
        final String signature = GetFbParam(request, "fb_sig");
        if (null == signature)
        {
            RequestLocalLog.Instance.note("Facebook signature field not found");
            return null;
        }
        for (final Site secret : FACEBOOK_SECRET)
        {
            final String md5 = getCalculatedSignature(request, secret.apiSecretKey);
            if ((null != md5) && md5.equals(signature)) return secret;
        }
        RequestLocalLog.Instance.println("Facebook authentication failed:\n" + getParamsForTrace(request));
        return null;
    }

}
