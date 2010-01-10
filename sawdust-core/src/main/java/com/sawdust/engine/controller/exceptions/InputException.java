package com.sawdust.engine.controller.exceptions;

import java.util.logging.Level;
import java.util.logging.Logger;


public class InputException extends GameException
{

    private static final Logger LOG = Logger.getLogger(InputException.class.getName());
    private static final Level LEVEL = Level.WARNING;

    public InputException()
    {
        super(LEVEL);
    }

    public InputException(String msg)
    {
        super(msg, LEVEL);
    }

    public InputException(String message, Throwable cause)
    {
        super(message, cause, LEVEL);
    }

    public InputException(Throwable cause)
    {
        super(cause, LEVEL);
    }
}
