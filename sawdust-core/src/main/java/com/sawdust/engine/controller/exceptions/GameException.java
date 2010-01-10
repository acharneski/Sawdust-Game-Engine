/**
 * 
 */
package com.sawdust.engine.controller.exceptions;

import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sawdust.engine.controller.Util;

/**
 * @author Administrator
 */
public class GameException extends com.sawdust.engine.view.GameException
{
    //private static final Level LEVEL = Level.WARNING;
    private static final Logger LOG = Logger.getLogger(GameException.class.getName());

    /**
     * @param LEVEL 
	 * 
	 */
    public GameException(Level LEVEL)
    {
        super();
        initialize(null, LEVEL);
        // TODO Auto-generated constructor stub
    }

    public GameException(final String msg, Level LEVEL)
    {
        super(msg);
        initialize(null, LEVEL);
    }

    /**
     * @param message
     * @param cause
     * @param LEVEL 
     */
    public GameException(final String message, final Throwable cause, Level LEVEL)
    {
        super(message, cause);
        initialize(cause, LEVEL);
    }

    /**
     * @param cause
     * @param LEVEL 
     */
    public GameException(final Throwable cause, Level LEVEL)
    {
        super(cause);
        initialize(cause, LEVEL);
    }

    protected void initialize(final Throwable cause, Level level)
    {
        if (null != cause)
        {
            final StringWriter stringWriter = new StringWriter();
            stringWriter.append("Exception: " + Util.getFullString(cause));
            LOG.log(level,stringWriter.toString());
        }
        else
        {
            final StringWriter stringWriter = new StringWriter();
            stringWriter.append("Exception: " + Util.getFullString(this));
            LOG.log(level,stringWriter.toString());
        }

    }

}
