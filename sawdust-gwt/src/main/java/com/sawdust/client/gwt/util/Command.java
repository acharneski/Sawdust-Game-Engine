/**
 * 
 */
package com.sawdust.client.gwt.util;

class Command
{
    public String command;
    public EventListener onComplete;

    public Command(final String pcommand, final EventListener ponComplete)
    {
        super();
        command = pcommand;
        onComplete = ponComplete;
    }
}
