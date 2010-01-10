package com.sawdust.engine.model.state;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.view.game.ClientCommand;

public abstract class GameCommand
{
    /**
     * @param commandText TODO
     * @throws com.sawdust.engine.view.GameException 
	 * 
	 */
    public abstract boolean doCommand(final Participant p, String commandText) throws GameException;

    public abstract String getCommandText();

    public abstract String getHelpText();

    public ClientCommand toGwt()
    {
        return new ClientCommand(getCommandText(), getHelpText());
    }
}
