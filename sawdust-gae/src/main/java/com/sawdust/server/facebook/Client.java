package com.sawdust.server.facebook;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;

public class Client
{

    public static Client create()
    {
        return new Client();
    }

    public WebResource resource(final URI uri) throws MalformedURLException, IOException
    {
        final InputStream stream = uri.toURL().openStream();
        return new WebResource(stream);

    }

}
