package com.sawdust.games.euchre.ai;

import java.util.HashMap;
import java.util.logging.Logger;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.players.Agent;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.players.PlayerManager;
import com.sawdust.engine.model.state.IndexCard;
import com.sawdust.engine.model.state.IndexPosition;
import com.sawdust.engine.view.cards.Suits;
import com.sawdust.games.euchre.EuchreCommand;
import com.sawdust.games.euchre.EuchreGame;
import com.sawdust.games.euchre.EuchreLayout;
import com.sawdust.games.euchre.GamePhase;
import com.sawdust.games.euchre.TeamStatus;

public class Normal1 extends Agent<EuchreGame>
{
    private static final Logger LOG = Logger.getLogger(Normal1.class.getName());

    private static final int SUIT_COUNT_THRESHOLD = 2;

    public Normal1(final String s)
    {
        super(s);
    }

    private void doMaking(final EuchreGame game, final boolean isInitMaking) throws GameException
    {
        final HashMap<Suits, Integer> suitMap = new HashMap<Suits, Integer>();
        final PlayerManager playerManager = game.getPlayerManager();
        final Participant player = playerManager.getCurrentPlayer();
        final Suits highestSuit = getMostCommonSuit(game, suitMap);
        final int trumpCount = suitMap.get(highestSuit);
        if (isInitMaking)
        {
            final IndexCard playCard = (IndexCard) game.getToken(new IndexPosition(EuchreLayout.POS_IN_PLAY, 0));
            final Suits suit = playCard.getCard().getSuit();
            if (highestSuit.equals(suit) && (trumpCount > SUIT_COUNT_THRESHOLD))
            {
                game.doCommand(EuchreCommand.Call);
            }
            else
            {
                game.doCommand(EuchreCommand.Pass);
            }
        }
        else
        {
            if (trumpCount > (SUIT_COUNT_THRESHOLD-1))
            {
                game.doCommand(EuchreCommand.Call, highestSuit);
            }
            else
            {
                game.doCommand(EuchreCommand.Pass);
            }
        }
        final boolean isSamePlayer = playerManager.getCurrentPlayer().equals(player);
        if (isSamePlayer)
        {
            game.saveState();
            Move(game, player);
        }
    }

    private void doPlayCards(final EuchreGame game, final Participant player) throws GameException
    {
        // Record state
        final PlayerManager playerManager = game.getPlayerManager();
        final int currentPlayerIndex = playerManager.getCurrentPlayerIndex();
        final Suits leadingSuit = game.getLeadingSuit();
        int originalScore = 0;
        for (final TeamStatus i : game.getTeamStatuses().values())
        {
            originalScore += i.currentHandCount;
        }

        // Aggregate data
        final boolean canLeadSuit = game.playerCanLead(currentPlayerIndex);
        IndexCard lowCard = null;
        IndexCard highCard = null;
        for (int cardIndexToPlay = 0; cardIndexToPlay < EuchreGame.NUMBER_OF_CARDS; cardIndexToPlay++)
        {
            final IndexCard cardToPlay = (IndexCard) game.getToken(new IndexPosition(currentPlayerIndex, cardIndexToPlay));
            if (null == cardToPlay)
            {
                continue;
            }
            final boolean isLeadingSuit = (game.getEffectiveSuit(cardToPlay.getCard()) == leadingSuit);
            if (!isLeadingSuit && canLeadSuit)
            {
                continue;
            }
            if ((null == lowCard) || (-1 == game.getCardSortOrder(lowCard).compareTo(game.getCardSortOrder(cardToPlay))))
            {
                lowCard = cardToPlay;
            }
            if ((null == highCard) || (1 == game.getCardSortOrder(highCard).compareTo(game.getCardSortOrder(cardToPlay))))
            {
                highCard = cardToPlay;
            }
        }

        // Make choice
        final boolean havePhysicalChoice = !lowCard.equals(highCard);
        final boolean isTeamWinning = game.getCurrentWinningTeam() == game.getTeamNumber(player);
        final boolean lowWin = game.isWinningCard(lowCard);
        final boolean highWin = game.isWinningCard(highCard);
        final boolean canWin = lowWin || highWin;
        final boolean canLose = lowWin && highWin;
        if (havePhysicalChoice && !isTeamWinning && canWin && canLose)
        {
            game.doCommand(EuchreCommand.Play, player, highCard.getPosition().getCardIndex());
        }
        else
        {
            game.doCommand(EuchreCommand.Play, player, lowCard.getPosition().getCardIndex());
        }

        // State cleanup
        int newScore = 0;
        for (final TeamStatus i : game.getTeamStatuses().values())
        {
            newScore += i.currentHandCount;
        }
        final boolean isSamePlayer = playerManager.getCurrentPlayer().equals(player);
        if (isSamePlayer && (newScore > originalScore))
        {
            game.saveState();
            Move(game, player);
        }
    }

    private Suits getMostCommonSuit(final EuchreGame game, final HashMap<Suits, Integer> suitMap)
    {
        final PlayerManager playerManager = game.getPlayerManager();
        Suits highestSuit = Suits.Null;
        for (int cardIndexToPlay = 0; cardIndexToPlay < EuchreGame.NUMBER_OF_CARDS; cardIndexToPlay++)
        {
            final IndexCard cardToPlay = (IndexCard) game.getToken(new IndexPosition(playerManager.getCurrentPlayerIndex(), cardIndexToPlay));
            if (null == cardToPlay)
            {
                continue;
            }
            int v = 0;
            final Suits suit = cardToPlay.getCard().getSuit();
            if (suitMap.containsKey(suit))
            {
                v = suitMap.get(suit);
            }
            suitMap.put(suit, ++v);
            int a = 0;
            if (suitMap.containsKey(highestSuit))
            {
                a = suitMap.get(highestSuit);
            }
            if (v > a)
            {
                highestSuit = suit;
            }
        }
        return highestSuit;
    }

    @Override
    public void Move(final EuchreGame game, final Participant player) throws GameException
    {
        final GamePhase currentPhase = game.getCurrentPhase();
        final boolean isInitMaking = currentPhase.equals(EuchreGame.INITIAL_MAKING);
        final boolean isOpenMaking = currentPhase.equals(EuchreGame.OPEN_MAKING);
        if (isInitMaking || isOpenMaking)
        {
            doMaking(game, isInitMaking);
        }
        else if (currentPhase.equals(EuchreGame.PLAYING))
        {
            doPlayCards(game, player);
        }
        // else if (game.getCurrentPhase().equals(EuchreGame.DEALING))
        // {
        // // Do nothing
        // }
        // else if (game.getCurrentPhase().equals(EuchreGame.COMPLETE))
        // {
        // // Do nothing
        // }
        // else if (game.getCurrentPhase().equals(EuchreGame.FORMING))
        // {
        // // Do nothing
        // }
        else
        {
            LOG.warning("Failed force move");
        }
    }
}
