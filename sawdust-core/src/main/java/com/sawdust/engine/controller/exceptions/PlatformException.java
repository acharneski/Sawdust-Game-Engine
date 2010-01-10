package com.sawdust.engine.controller.exceptions;

import java.util.logging.Level;
import java.util.logging.Logger;


public class PlatformException extends GameException
{
    private static final Logger LOG = Logger.getLogger(PlatformException.class.getName());
    private static final Level LEVEL = Level.WARNING;

    public PlatformException()
    {
        super(LEVEL);
    }

    public PlatformException(String msg)
    {
        super(msg, LEVEL);
    }

    public PlatformException(String message, Throwable cause)
    {
        super(message, cause, LEVEL);
    }

    public PlatformException(Throwable cause)
    {
        super(cause, LEVEL);
    }
}
