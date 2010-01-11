package com.sawdust.engine.model.state;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.basetypes.GameState;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.view.game.ClientCommand;

public abstract class GameCommand<T extends GameState>
{
    public abstract CommandResult<T> doCommand(final Participant p, String parameters) throws GameException;

    public abstract String getCommandText();

    public abstract String getHelpText();

    public ClientCommand toGwt()
    {
        return new ClientCommand(getCommandText(), getHelpText());
    }
}
