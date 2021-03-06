package com.sawdust.engine.controller.exceptions;

import java.io.StringWriter;
import java.util.logging.Logger;

import com.sawdust.engine.controller.Util;

public class SawdustSystemError extends RuntimeException
{
    private static final Logger LOG = Logger.getLogger(SawdustSystemError.class.getName());

    /**
	 * 
	 */
    public SawdustSystemError()
    {
        super();
        initialize(null);
    }

    /**
     * @param message
     */
    public SawdustSystemError(final String message)
    {
        super(message);
        initialize(null);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public SawdustSystemError(final String message, final Throwable cause)
    {
        super(message, cause);
        initialize(cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public SawdustSystemError(final Throwable cause)
    {
        super(cause);
        initialize(cause);
        // TODO Auto-generated constructor stub
    }

    private void initialize(final Throwable cause)
    {
        if (null != cause)
        {
            final StringWriter stringWriter = new StringWriter();
            stringWriter.append("Exception: " + Util.getFullString(cause));
            LOG.warning(stringWriter.toString());
        }
        else
        {
            final StringWriter stringWriter = new StringWriter();
            stringWriter.append("Exception: " + Util.getFullString(this));
            LOG.warning(stringWriter.toString());
        }
    }

}
