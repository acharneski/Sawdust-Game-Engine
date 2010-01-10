package com.sawdust.games.poker.ai;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.controller.exceptions.GameLogicException;
import com.sawdust.engine.model.players.Agent;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.games.poker.PokerGame;
import com.sawdust.games.poker.PokerGame.GamePhase;
import com.sawdust.games.poker.PokerGame.PlayerState;

public class Stupid1 extends Agent<PokerGame>
{
    public Stupid1(final String s)
    {
        super(s);
    }

    @Override
    public void Move(final PokerGame game, final Participant player) throws GameException
    {
        if (game.getCurrentPhase() == GamePhase.Bidding)
        {
            // game.print("Forcing %s's move", game.displayName(currentPlayer));
            game.doBet(player, game.getCurrentBet());
        }
        else if (game.getCurrentPhase() == GamePhase.Drawing)
        {
            // game.print("Forcing %s's move", game.displayName(currentPlayer));
            final PlayerState playerState = game.getPlayerState(player);
            if (playerState == PlayerState.Ready)
            {
                game.doDraw(player);
            }
            else throw new GameLogicException("Agent Panic: State " + playerState + " and game drawing");
        }
        else
        {
            // game.print("Failed force move");
        }
    }
}
