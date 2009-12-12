package com.sawdust.engine.service.debug;

import java.io.PrintStream;
import java.io.StringWriter;
import java.util.logging.Logger;

import com.sawdust.engine.service.Util;

//import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;


public class RequestLocalLog
{
   private static final Logger LOG = Logger.getLogger(RequestLocalLog.class.getName());

    public static final RequestLocalLog Instance = new RequestLocalLog();

    private static final StringBuffer sb = new StringBuffer();

    private RequestLocalLog()
    {
    }

    public void clear()
    {
        if (sb.length() > 0)
        {
            sb.delete(0, sb.length());
        }
        //print(new Exception("Reset at " + DateFormat.getDateTimeInstance().format(new Date())));
    }

    public String getRequestLog()
    {
        return sb.toString();
    }

    public void note(final String str)
    {
        sb.append(str + "\n");
    }

    public void print(final Exception e)
    {
        sb.append(Util.getFullString(e));
    }

    public void print(final String str)
    {
        sb.append(str + "\n");
        LOG.info(str);
    }

    public void println(final String str)
    {
        print(str);
    }
}
