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
import com.sawdust.engine.model.state.IndexPosition;

final class CompletePhase extends GamePhase
{
    public static final GamePhase INSTANCE = new CompletePhase();

    @Override
    public void doCommand(final EuchreGame game, final EuchreCommand cmd, final Object... params) throws GameException
    {
        switch (cmd)
        {
        case Deal:
            game.setCurrentPhase(EuchreGame.DEALING);
            game.doCommand(EuchreCommand.Deal, params);
            break;
        default:
            throw new GameLogicException(String.format("Unknown command %s while in state %s", cmd, this));
        }
    }

    @Override
    public String getId()
    {
        return "Complete";
    }

    @Override
    public ArrayList<GameCommand<?>> getMoves(final Participant access, final EuchreGame game) throws GameException
    {
        final ArrayList<GameCommand<?>> returnValue = new ArrayList<GameCommand<?>>();
        returnValue.add(com.sawdust.games.euchre.Command.Close.getGameCommand(game));
        return returnValue;
    }

    @Override
    public Collection<GameLabel> setupLabels(final EuchreGame game, final Player access)
    {
        final Collection<GameLabel> returnValue = new ArrayList<GameLabel>();
        returnValue.addAll(game.getPlayerLabels());

        int index = 0;
        final GameLabel label = new GameLabel("GeneralCommand " + index, new IndexPosition(EuchreLayout.POS_GENERAL_CMDS, index), "Deal Again");
        label.setCommand("Deal");
        returnValue.add(label);
        index++;
        return returnValue;
    }
}
