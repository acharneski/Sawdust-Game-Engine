/**
 * 
 */
package com.sawdust.games.euchre;

import java.util.ArrayList;
import java.util.Collection;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.controller.exceptions.GameLogicException;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.players.Player;
import com.sawdust.engine.model.state.GameCommand;
import com.sawdust.engine.model.state.GameLabel;

final class FormingPhase extends GamePhase
{
    public static final GamePhase INSTANCE = new FormingPhase();

    @Override
    public void doCommand(final EuchreGame game, final EuchreCommand cmd, final Object... params) throws GameException
    {
        switch (cmd)
        {
        case Deal:
            game.setCurrentPhase(EuchreGame.DEALING);
            game.doCommand(cmd, params);
            break;
        default:
            throw new GameLogicException(String.format("Unknown command %s while in state %s", cmd, this));
        }
    }

    @Override
    public String getId()
    {
        return "Forming";
    }

    @Override
    public ArrayList<GameCommand> getMoves(final Participant access, final EuchreGame game) throws GameException
    {
        final ArrayList<GameCommand> returnValue = new ArrayList<GameCommand>();
        return returnValue;
    }

    @Override
    public Collection<GameLabel> setupLabels(final EuchreGame game, final Player access)
    {
        return game.getPlayerLabels();
    }
}
