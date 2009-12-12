package com.sawdust.server.facebook;

import java.net.URI;
import java.net.URISyntaxException;

import com.sawdust.engine.service.debug.SawdustSystemError;

public class UriBuilder
{
    public static UriBuilder fromUri(final String facebookRestServer)
    {
        return new UriBuilder(facebookRestServer);
    }

    StringBuilder sb = new StringBuilder();

    public UriBuilder(final String facebookRestServer)
    {
        sb.append(facebookRestServer);
    }

    public URI build()
    {
        try
        {
            return new URI(sb.toString());
        }
        catch (final URISyntaxException e)
        {
            // e.printStackTrace();
            throw new SawdustSystemError(e);
            // return null;
        }
    }

    public void queryParam(final String key, final String value)
    {
        sb.append((0 > sb.indexOf("?")) ? "?" : "&");
        sb.append(String.format("%s=%s", key, value));
    }

}
