/**
 * 
 */
package com.sawdust.client.gwt.util;

import java.util.HashMap;

public class Command
{
    public String command;
    public boolean supress = false;
    public EventListener onComplete;
    public final HashMap<String,Boolean> filterStatus = new HashMap<String, Boolean>();

    public Command(final String pcommand, final EventListener ponComplete)
    {
        super();
        command = pcommand;
        onComplete = ponComplete;
    }
}
