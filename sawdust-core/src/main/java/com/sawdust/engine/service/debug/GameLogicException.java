package com.sawdust.engine.service.debug;

import java.util.logging.Level;
import java.util.logging.Logger;


public class GameLogicException extends GameException
{

    private static final Logger LOG = Logger.getLogger(GameLogicException.class.getName());

    private static final Level LEVEL = Level.INFO;

    public GameLogicException()
    {
        super(LEVEL);
    }

    public GameLogicException(String msg)
    {
        super(msg, LEVEL);
    }

    public GameLogicException(String message, Throwable cause)
    {
        super(message, cause, LEVEL);
    }

    public GameLogicException(Throwable cause)
    {
        super(cause, LEVEL);
    }
}
