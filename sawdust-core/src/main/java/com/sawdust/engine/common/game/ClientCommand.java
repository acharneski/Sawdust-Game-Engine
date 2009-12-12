package com.sawdust.engine.common.game;

import java.io.Serializable;

public class ClientCommand implements Serializable
{
    /**
	 * 
	 */
    private String command;
    private String helpText;

    @Deprecated
    public ClientCommand()
    {
    }

    /**
     * @param pcommand
     */
    public ClientCommand(final String pcommand, final String help)
    {
        super();
        command = pcommand;
        helpText = help;
    }

    /**
     * @return the command
     */
    public String getCommand()
    {
        return command;
    }

    public String getHelp()
    {
        return "<div class='sdge-game-rule'><strong>" + command + "</strong>" + " - " + helpText + "</div>";
    }
}
