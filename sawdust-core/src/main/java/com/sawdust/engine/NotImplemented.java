package com.sawdust.engine;

public class NotImplemented extends RuntimeException
{

    public NotImplemented()
    {
        super("Not Implemented");
    }

    public NotImplemented(Throwable cause)
    {
        super("Not Implemented",cause);
    }

}
