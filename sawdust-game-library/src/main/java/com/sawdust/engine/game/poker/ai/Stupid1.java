package com.sawdust.engine.game.poker.ai;

import com.sawdust.engine.game.players.Agent;
import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.game.poker.PokerGame;
import com.sawdust.engine.game.poker.PokerGame.GamePhase;
import com.sawdust.engine.game.poker.PokerGame.PlayerState;
import com.sawdust.engine.service.debug.GameException;
import com.sawdust.engine.service.debug.GameLogicException;

public class Stupid1 extends Agent<PokerGame>
{
    public Stupid1(final String s)
    {
        super(s);
    }

    @Override
    public void Move(final PokerGame game, final Participant player) throws com.sawdust.engine.common.GameException
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
