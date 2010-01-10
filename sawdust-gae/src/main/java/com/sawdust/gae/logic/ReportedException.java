package com.sawdust.gae.logic;

import java.util.logging.Level;

import com.sawdust.engine.controller.exceptions.GameException;

public class ReportedException extends GameException
{
    public String reportId = "";
    private static final Level LEVEL = Level.INFO;

    public ReportedException()
    {
        super(LEVEL);
    }

    public ReportedException(String msg)
    {
        super(msg, LEVEL);
    }

    public ReportedException(String message, Throwable cause)
    {
        super(message, cause, LEVEL);
    }

    public ReportedException(Throwable cause)
    {
        super(cause, LEVEL);
    }
}
