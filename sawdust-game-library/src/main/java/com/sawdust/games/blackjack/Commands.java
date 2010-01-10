/**
 * 
 */
package com.sawdust.games.blackjack;


import java.util.ArrayList;

import com.sawdust.engine.controller.entities.GameSession;
import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.state.IndexCard;
import com.sawdust.engine.model.state.IndexPosition;
import com.sawdust.engine.model.state.Token;
import com.sawdust.engine.view.cards.Ranks;
import com.sawdust.games.blackjack.BlackjackGame.GamePhases;

public enum Commands
{
    Stay
    {
        public void doCommand(final Participant user, final BlackjackGame baseGame, final String param) throws GameException
        {
            baseGame.doStay();
            baseGame.saveState();
        }

        public String getCommandText()
        {
            return "Stay";
        }

        public String getHelpText()
        {
            return "Stop dealing Participant cards and finish the dealer's hand.";
        }
    },
    DoubleDown
    {
        public void doCommand(final Participant user, final BlackjackGame baseGame, final String param) throws GameException
        {
            baseGame.getSession().doUnitWager();
            baseGame.doHit(0);
            if (GamePhases.Playing == baseGame.getCurrentPhase()) baseGame.doStay();
            baseGame.saveState();
        }

        public String getCommandText()
        {
            return "Double Down";
        }

        public String getHelpText()
        {
            return "Double your initial bet, in exchange for receiving only one more card from the dealer.";
        }

        @Override
        public boolean canDo(final Participant user, final BlackjackGame game)
        {
            ArrayList<Token> cards = game.getCurveCards(BlackjackGame.HAND_PLAYER);
            if(2 != cards.size()) return false;
            if(0 != game.getCurveCards(BlackjackGame.HAND_DEALER+1).size()) return false;
            return true;
        }
    },
    SplitPair
    {
        public void doCommand(final Participant user, final BlackjackGame baseGame, final String param) throws GameException
        {
            ArrayList<Token> cards = baseGame.getCurveCards(BlackjackGame.HAND_PLAYER);
            int curve = BlackjackGame.HAND_DEALER+1;
            cards.get(0).setPosition(new IndexPosition(curve, 1));
            baseGame.playerHandStatus.put(curve, BlackjackGame.PlayerHandStatus.Playing);

            baseGame.dealNewCard(new IndexPosition(BlackjackGame.HAND_PLAYER, 0)).setOwner(user).setPrivate("VR");
            baseGame.dealNewCard(new IndexPosition(curve, 0)).setOwner(user).setPrivate("VR");
            baseGame.saveState();
        }

        public String getCommandText()
        {
            return "Split Pair";
        }

        public String getHelpText()
        {
            return "Use each card to start a pair of paralell new 2-card hands, doubling your bet in the process.";
        }

        @Override
        public boolean canDo(final Participant user, final BlackjackGame game)
        {
            ArrayList<Token> cards = game.getCurveCards(BlackjackGame.HAND_PLAYER);
            if(2 != cards.size()) return false;
            Ranks rank0 = ((IndexCard) cards.get(0)).getCard().getRank();
            Ranks rank1 = ((IndexCard) cards.get(1)).getCard().getRank();
            if(rank0.equals(rank1)) return true;
            return false;
        }
    };
    
    public abstract void doCommand(final Participant user, final BlackjackGame blackjackGame, final String param) throws GameException;
    
    public abstract String getCommandText();
    
    public abstract String getHelpText();

    public boolean canDo(final Participant user, final BlackjackGame game)
    {
        return true;
    }
}
