package com.sawdust.games.poker.ai;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.controller.exceptions.GameLogicException;
import com.sawdust.engine.model.players.Agent;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.state.CommandResult;
import com.sawdust.engine.model.state.GameCommand;
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
    public GameCommand<PokerGame> getMove(final PokerGame game, final Participant player) throws GameException
    {
        return new GameCommand<PokerGame>()
        {
            @Override
            public CommandResult<PokerGame> doCommand(Participant p, String parameters) throws GameException
            {
                if (game.getCurrentPhase() == GamePhase.Bidding)
                {
                    game.doBet(player, game.getCurrentBet());
                }
                else if (game.getCurrentPhase() == GamePhase.Drawing)
                {
                    final PlayerState playerState = game.getPlayerState(player);
                    if (playerState == PlayerState.Ready)
                    {
                        game.doDraw(player);
                    }
                    else throw new GameLogicException("Agent Panic: State " + playerState + " and game drawing");
                }
                return new CommandResult<PokerGame>(game);
            }
        };
    }
}
