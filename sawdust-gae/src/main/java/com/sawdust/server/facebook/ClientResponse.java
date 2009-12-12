package com.sawdust.server.facebook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.sawdust.engine.service.debug.SawdustSystemError;

public class ClientResponse
{
    private InputStream _stream;

    /**
     * @param stream
     */
    public ClientResponse(final InputStream stream)
    {
        super();
        setStream(stream);
    }

    public String getEntity(final Class<String> class1)
    {
        // TODO Auto-generated method stub
        final InputStreamReader inputStreamReader = new InputStreamReader(_stream);
        final BufferedReader b = new BufferedReader(inputStreamReader);
        final StringBuilder sb = new StringBuilder();
        int maxLoop = 1000;
        while (true)
        {
            String s;
            try
            {
                s = b.readLine();
            }
            catch (final IOException e)
            {
                break;
            }
            if (null == s)
            {
                break;
            }
            if (0 > maxLoop--) throw new SawdustSystemError();
            sb.append(s);
        }
        return sb.toString();
    }

    public InputStream getEntityInputStream()
    {
        return _stream;
    }

    InputStream getStream()
    {
        return _stream;
    }

    void setStream(final InputStream stream)
    {
        _stream = stream;
    }

}
