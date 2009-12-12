package com.sawdust.engine.game.state;

import com.sawdust.engine.common.game.ClientCommand;
import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.service.debug.GameException;

public abstract class GameCommand
{
    /**
     * @throws com.sawdust.engine.common.GameException 
	 * 
	 */
    public abstract boolean doCommand(final Participant p) throws GameException, com.sawdust.engine.common.GameException;

    public abstract String getCommandText();

    public abstract String getHelpText();

    public ClientCommand toGwt()
    {
        return new ClientCommand(getCommandText(), getHelpText());
    }
}
