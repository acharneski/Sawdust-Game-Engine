package com.sawdust.engine.controller.exceptions;

import java.util.logging.Level;
import java.util.logging.Logger;


public class DataException extends GameException
{

    private static final Logger LOG = Logger.getLogger(DataException.class.getName());

    private static final Level LEVEL = Level.WARNING;

    public DataException()
    {
        super(LEVEL);
    }

    public DataException(String msg)
    {
        super(msg, LEVEL);
    }

    public DataException(String message, Throwable cause)
    {
        super(message, cause, LEVEL);
    }

    public DataException(Throwable cause)
    {
        super(cause, LEVEL);
    }
}
