package com.sawdust.games.stop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Logger;

import javax.management.RuntimeErrorException;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.controller.exceptions.GameLogicException;
import com.sawdust.engine.controller.exceptions.SawdustSystemError;
import com.sawdust.engine.model.players.Agent;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.state.CommandResult;
import com.sawdust.engine.model.state.GameCommand;
import com.sawdust.engine.model.state.IndexPosition;
import com.sawdust.games.stop.StopGame.GamePhase;

public class Stupid1 extends Agent<StopGame>
{
    private static final Logger LOG = Logger.getLogger(Stupid1.class.getName());

    public Stupid1(final String s)
    {
        super(s);
    }

    @Override
    public GameCommand<StopGame> getMove(final StopGame game, final Participant player) throws GameException
    {
        if (game.getCurrentPhase() == GamePhase.Playing)
        {
            final int findPlayer = game.getPlayerManager().getPlayerIndex(player);
            final int otherPlayer = (0 == findPlayer) ? 1 : 0;

            final TokenArray tokenArray = game.getTokenArray();
            final ArrayList<StopIsland> islands = tokenArray.getIslands();
            Collections.sort(islands, new Comparator<Island>()
            {
                public int compare(final Island o1, final Island o2)
                {
                    return sortIslandToMoveIn(o1, o2);
                }

            });
            final Island bestIsland = islands.get(0);
            final ArrayList<ArrayPosition> allPositions = bestIsland.getAllPositions();
            Collections.sort(allPositions, new Comparator<ArrayPosition>()
            {
                public int compare(final ArrayPosition o1, final ArrayPosition o2)
                {
                    return sortPositionToMoveIn(otherPlayer, tokenArray, o1, o2);
                }
            });
            for (int i = 0; i < allPositions.size(); i++)
            {
                final ArrayPosition bestPosition = allPositions.get(0);
                return new GameCommand<StopGame>()
                {
                    @Override
                    public CommandResult<StopGame> doCommand(Participant p, String parameters) throws GameException
                    {
                        StopGame newGame = game.doMove(new IndexPosition(bestPosition.row, bestPosition.col), player);
                        return new CommandResult<StopGame>(newGame);
                    }
                };
            }
        }
        else
        {
            LOG.warning("Cannot move in phase: " + game.getCurrentPhase());
        }
        throw new SawdustSystemError("No move?");
    }

    private int sortPositionToMoveIn(final int otherPlayer, final TokenArray tokenArray, final ArrayPosition o1, final ArrayPosition o2)
    {
        final boolean near1 = o1.isNear(tokenArray, otherPlayer);
        final boolean near2 = o2.isNear(tokenArray, otherPlayer);
        if (near1 && near2) return ((Math.random() < 0.5) ? -1 : 1);
        if (near1) return -1;
        if (near2) return 1;
        return 0;
    }

    private int sortIslandToMoveIn(final Island o1, final Island o2)
    {
        if ((o1.getPlayer() == -1) && (o2.getPlayer() == -1))
        {
            if ((o1.getAllPositions().size() > 1) && (o2.getAllPositions().size() > 1))
                return -((Integer) o1.getAllPositions().size()).compareTo(o2.getAllPositions().size());
            else
            {
                if (o1.getAllPositions().size() > 1) return -1;
                if (o2.getAllPositions().size() > 1) return 1;
            }
        }
        else
        {
            if (o1.getPlayer() == -1) return -1;
            if (o2.getPlayer() == -1) return 1;
        }
        return 0;
    }
}
