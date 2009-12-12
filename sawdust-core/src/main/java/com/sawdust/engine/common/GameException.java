package com.sawdust.engine.common;

public class GameException extends Exception
{

    public GameException()
    {
        super();
    }

    public GameException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public GameException(String message)
    {
        super(message);
    }

    public GameException(Throwable cause)
    {
        super(cause);
    }
    
}
