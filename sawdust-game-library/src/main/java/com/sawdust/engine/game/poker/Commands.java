/**
 * 
 */
package com.sawdust.engine.game.poker;


import java.util.ArrayList;

import com.sawdust.engine.game.basetypes.BaseGame;
import com.sawdust.engine.game.basetypes.GameState;
import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.service.data.GameSession;
import com.sawdust.engine.service.debug.GameException;
import com.sawdust.engine.service.debug.GameLogicException;

public enum Commands
{
    Bet
    {

        public void doCommand(final Participant user, final GameSession game, final String param) throws GameException
        {
            int bet;
            try
            {
                bet = Integer.parseInt(param);
            }
            catch (final NumberFormatException e)
            {
                throw new GameLogicException(String.format("Warning: Unparsable card index: %s", param));
            }
            final GameState baseGame = game.getState();
            final PokerGame pokerGame = (PokerGame) baseGame;
            pokerGame.doBet(user, bet);
            baseGame.saveState();
        }

        public String getCommandText()
        {
            return "Bet";
        }

        public String getHelpText()
        {
            // TODO Auto-generated method stub
            return null;
        }
    },
    Draw_Cards
    {

        public void doCommand(final Participant user, final GameSession game, final String param) throws GameException
        {
            final GameState baseGame = game.getState();
            final PokerGame pokerGame = (PokerGame) baseGame;
            pokerGame.doDraw(user);
            baseGame.saveState();
        }

        public String getCommandText()
        {
            return "Draw";
        }

        public String getHelpText()
        {
            // TODO Auto-generated method stub
            return null;
        }
    },
    Drop_Cards
    {

        public void doCommand(final Participant user, final GameSession game, final String param) throws GameException
        {
            final GameState baseGame = game.getState();
            final PokerGame pokerGame = (PokerGame) baseGame;
            final String cards[] = param.split("\\s+");
            final Participant email = user;
            final ArrayList<Integer> cardIndex = new ArrayList<Integer>();
            for (final String cardStr : cards)
            {
                int cardSlot = -1;
                try
                {
                    cardSlot = Integer.parseInt(cardStr);
                }
                catch (final NumberFormatException e)
                {
                    throw new GameLogicException(String.format("Warning: Unparsable card index: %s", cardStr));
                }
                if ((cardSlot < 0) || (cardSlot >= 5)) throw new GameLogicException(String.format("Warning: Card index %d out of range", cardSlot));
                cardIndex.add(cardSlot);
            }
            pokerGame.dropCards(email, cardIndex);
            baseGame.saveState();
        }

        public String getCommandText()
        {
            return "Discard";
        }

        public String getHelpText()
        {
            // TODO Auto-generated method stub
            return null;
        }

    },
    Fold
    {

        public void doCommand(final Participant user, final GameSession game, final String param) throws GameException
        {
            final GameState baseGame = game.getState();
            final PokerGame pokerGame = (PokerGame) baseGame;
            pokerGame.doFold(user);
            baseGame.saveState();
        }

        public String getCommandText()
        {
            return "Fold";
        }

        public String getHelpText()
        {
            // TODO Auto-generated method stub
            return null;
        }
    };
    
    public abstract void doCommand(final Participant user, final GameSession game, final String param) throws GameException;
    public abstract String getCommandText();
    public abstract String getHelpText();
    
}
