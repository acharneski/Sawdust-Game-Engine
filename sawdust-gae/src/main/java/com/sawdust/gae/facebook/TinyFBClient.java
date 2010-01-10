/**
 * Tiny Rest Client for Facebook
 * 
 * @author Carmen Delessio carmendelessio AT gmail DOT com http://www.socialjava.com created March 30, 2009
 **/

package com.sawdust.gae.facebook;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.logging.Logger;

public class TinyFBClient
{
    private static final Logger LOG = Logger.getLogger(TinyFBClient.class.getName());

    String apiKey;
    String callId;
    String facebookRestServer = "http://api.facebook.com/restserver.php";
    String format = "XML";
    Client restClient = Client.create();
    ClientResponse restResponse;
    String secretKey;

    String session;
    TreeMap<String, String> standardParms = new TreeMap<String, String>();
    String version = "1.0";

    // post.addParameter("call_id", );

    public TinyFBClient()
    {
        //standardParms.put("v", version);
        standardParms.put("format", format);
    }

    public TinyFBClient(final String appIdParm, final String appSecretParm)
    {
        this();
        apiKey = appIdParm;
        secretKey = appSecretParm;
        standardParms.put("api_key", apiKey);
        //standardParms.put("secret_key", secretKey);
    }

    public TinyFBClient(final String appIdParm, final String appSecretParm, final String sessionParm)
    {
        this(appIdParm, appSecretParm);
        session = sessionParm;
        standardParms.put("session_key", sessionParm);
    }

    public TinyFBClient(final String appIdParm, final String appSecretParm, final String sessionParm, final String versionParm, final String formatParm)
    {
        this(appIdParm, appSecretParm, sessionParm);
        version = versionParm;
        format = formatParm;
        //standardParms.put("v", version);
        standardParms.put("format", format);

    }

    /*
     * public TinyFBClient( TinyFBClient tiny){ this(tiny.apiKey, tiny.secretKey, tiny.session); this.version=tiny.version;
     * this.format=tiny.format; standardParms.put("v", this.version); standardParms.put("format", this.format); }
     */

    public String call(final String method, final TreeMap<String, String> parms)
    {
        parms.put("method", method);
        return (this.call(parms));
    }

    public String call(final TreeMap<String, String> parms)
    {
        ClientResponse thisResponse;

        thisResponse = this.getResponse(parms);
        return (thisResponse.getEntity(String.class));
    }

    public String generateSignature(String requestString, final String psecretKey)
    {
        final StringBuilder result = new StringBuilder();
        try
        {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            for (final byte b : md.digest((requestString + psecretKey).getBytes()))
            {
                result.append(Integer.toHexString((b & 0xf0) >>> 4));
                result.append(Integer.toHexString(b & 0x0f));
            }
            String signiatureString = result.toString();
            LOG.info(String.format("generateSignature(\n\"%s\",\n\"%s\")=\n\"%s\"", requestString, psecretKey, signiatureString));
            return signiatureString;
        }
        catch (final NoSuchAlgorithmException e)
        {
            return ("Error: no MD5 ");
        }
    }

    public String getApiKey()
    {
        return (apiKey);
    }

    public ClientResponse getResponse(final String method, final TreeMap<String, String> parms)
    {
        parms.put("method", method);
        return (this.getResponse(parms));
    }

    public ClientResponse getResponse(final TreeMap<String, String> parms)
    {
        String currentKey;
        String currentValue;
        String sigParms = ""; // String used for creating signature
        String encodedParm;
        final UriBuilder ub = UriBuilder.fromUri(facebookRestServer);
        final TreeMap<String, String> restParms = new TreeMap<String, String>();
        restParms.putAll(standardParms);
        restParms.putAll(parms);
        restParms.put("call_id", String.valueOf(System.currentTimeMillis()));

        final Collection<String> c = restParms.keySet();
        final Iterator<String> itr = c.iterator();

        while (itr.hasNext())
        {
            currentKey = itr.next();
            currentValue = restParms.get(currentKey);
            encodedParm = currentValue;
            encodedParm = UriComponent.contextualEncode(currentValue, UriComponent.Type.QUERY_PARAM, false);
            ub.queryParam(currentKey, encodedParm);
            sigParms += currentKey + "=" + currentValue;
        }
        final String signature = generateSignature(sigParms, secretKey);

        ub.queryParam("sig", signature);
        WebResource resource;
        URI uri;
        uri = ub.build();
        try
        {
            resource = restClient.resource(uri);
            restResponse = resource.get(ClientResponse.class);
            return (restResponse);
        }
        catch (final MalformedURLException e)
        {
            e.printStackTrace();
            return null;
        }
        catch (final IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public String getSecretKey()
    {
        return (secretKey);
    }

    public void setApiKey(final String papiKey)
    {
        apiKey = papiKey;
        standardParms.put("api_key", papiKey);

    }

    public void setFormat(final String formatParm)
    {
        format = formatParm;
        standardParms.put("format", format);

    }

    public void setRequestParms(final TreeMap<String, String> parms)
    {
        final TreeMap<String, String> requestParms = new TreeMap<String, String>();
        requestParms.putAll(standardParms);
        requestParms.putAll(parms);
    }

    public void setSecretKey(final String psecretKey)
    {
        secretKey = psecretKey;
        standardParms.put("secret_key", psecretKey);

    }

    public void setSession(final String sessionParm)
    {
        session = sessionParm;
        standardParms.put("session_key", session);
    }

    public void setVersion(final String versionParm)
    {
        version = versionParm;
        standardParms.put("v", version);

    }
}
