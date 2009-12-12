package com.sawdust.server.facebook;

import java.io.InputStream;

public class WebResource
{
    private ClientResponse _response;

    /**
     * @param response
     */
    public WebResource(final ClientResponse response)
    {
        super();
        _response = response;
    }

    public WebResource(final InputStream stream)
    {
        this(new ClientResponse(stream));
    }

    public ClientResponse get(final Class<ClientResponse> class1)
    {
        return _response;
    }

    ClientResponse getResponse()
    {
        return _response;
    }

    void setResponse(final ClientResponse response)
    {
        _response = response;
    }

}
