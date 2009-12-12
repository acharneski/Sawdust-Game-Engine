package com.sawdust.engine.game.poker.ai;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.sawdust.engine.game.players.Agent;
import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.game.poker.PokerGame;
import com.sawdust.engine.game.poker.PokerHand;
import com.sawdust.engine.game.poker.PokerHandPattern;
import com.sawdust.engine.game.poker.PokerGame.GamePhase;
import com.sawdust.engine.game.poker.PokerGame.PlayerState;
import com.sawdust.engine.game.state.IndexCard;
import com.sawdust.engine.game.state.Token;
import com.sawdust.engine.service.debug.GameException;
import com.sawdust.engine.service.debug.GameLogicException;

public class Regular1 extends Agent<PokerGame>
{
    private static final Logger LOG = Logger.getLogger(Regular1.class.getName());

    private final double _aggression = Math.random() * 0.6;
    private final double _courage = 1 + Math.random() * 5;
    private final double _richness = 2 + Math.random() * 5;

    public Regular1(final String s)
    {
        super(s);
    }

    @Override
    public void Move(final PokerGame game, final Participant player) throws com.sawdust.engine.common.GameException
    {
        final int playerIdx = game.getPlayerManager().findPlayer(player);

        final ArrayList<IndexCard> playerCards = new ArrayList<IndexCard>();
        for (final Token card : game.getCurveCards(playerIdx))
        {
            playerCards.add((IndexCard) card);
        }
        final PokerHand playerHand = PokerHandPattern.FindHighest(playerCards);

        if (game.getCurrentPhase() == GamePhase.Bidding)
        {
            if (game.getPlayerState(player) == PlayerState.Bidding)
            {
                int odds = playerHand.getOdds();
                if (odds < 1)
                {
                    odds = 1;
                }
                final double targetBet = 1 + Math.log(odds) * _richness;
                final double neededDelta = targetBet - game.getCurrentBet();
                final double fear = game.getCurrentBet() / (targetBet);
                LOG.info(String.format("Hand = %s; Target = %f; Delta = %f; Fear = %f;", playerHand.getName(), targetBet, neededDelta, fear));
                final long amt = Math.round(Math.log(neededDelta) * (1 + (Math.random() * _aggression)));
                if (amt > 0)
                {
                    game.doBet(player, (int) (game.getCurrentBet() + amt));
                }
                else if (fear > _courage)
                {
                    game.doFold(player);
                }
                else
                {
                    game.doBet(player, (game.getCurrentBet()));
                }
            }
        }
        else if (game.getCurrentPhase() == GamePhase.Drawing)
        {
            final PlayerState playerState = game.getPlayerState(player);
            final ArrayList<Integer> cardIndex = new ArrayList<Integer>();
            for (final Token card : game.getCurveCards(playerIdx))
            {
                if (!playerHand.getCards().contains(((IndexCard) card).getCard()))
                {
                    cardIndex.add(card.getPosition().getCardIndex());
                }
            }
            game.dropCards(player, cardIndex);
            if (playerState == PlayerState.Ready)
            {
                game.doDraw(player);
                if (game.getPlayerManager().isCurrentPlayer(player))
                {
                    Move(game, player);
                }
            }
            else throw new GameLogicException("Agent Panic: State " + playerState + " and game drawing");
        }
        else
        {
            System.out.println("Failed force move");
            // game.print("Failed force move");
        }
    }
}
