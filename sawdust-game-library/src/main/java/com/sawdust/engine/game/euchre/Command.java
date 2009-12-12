/**
 * 
 */
package com.sawdust.engine.game.euchre;


import com.sawdust.engine.common.cards.Suits;
import com.sawdust.engine.game.BaseGame;
import com.sawdust.engine.game.Game;
import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.game.players.Player;
import com.sawdust.engine.game.state.GameCommand;
import com.sawdust.engine.service.data.GameSession;
import com.sawdust.engine.service.data.GameSession.SessionStatus;
import com.sawdust.engine.service.debug.GameException;
import com.sawdust.engine.service.debug.GameLogicException;

public enum Command
{
    CallSuit
    {

        public void doCommand(final Participant user, final GameSession game, final String param) throws com.sawdust.engine.common.GameException
        {
            final Game baseGame = game.getLatestState();
            final EuchreGame euchreGame = (EuchreGame) baseGame;
            if (!user.equals(euchreGame.getCurrentPlayer())) throw new GameLogicException("It is not your turn");
            euchreGame.doCommand(EuchreCommand.Call, Suits.getSuitFromFullString(param));
            baseGame.saveState();
        }

        public String getCommandText()
        {
            return "Call";
        }

        public String getHelpText()
        {
            // TODO Auto-generated method stub
            return null;
        }

    },
    Close
    {

        public void doCommand(final Participant user, final GameSession game, final String param) throws GameException
        {
            final Game baseGame = game.getLatestState();
            final GameSession gameSession = game;
            if (SessionStatus.Playing == gameSession.getSessionStatus()) throw new GameLogicException("A game is in progress");
            gameSession.setSessionStatus(SessionStatus.Closed, baseGame);
            baseGame.saveState();
        }

        public String getCommandText()
        {
            return "Quit";
        }

        public String getHelpText()
        {
            // TODO Auto-generated method stub
            return null;
        }

    },
    PassSuit
    {

        public void doCommand(final Participant user, final GameSession game, final String param) throws com.sawdust.engine.common.GameException
        {
            final Game baseGame = game.getLatestState();
            final EuchreGame euchreGame = (EuchreGame) baseGame;
            final boolean notCurrentPlayer = (user == null) || !user.equals(euchreGame.getCurrentPlayer());
            if (notCurrentPlayer) throw new GameLogicException("It is not your turn");
            euchreGame.doCommand(EuchreCommand.Pass);
            baseGame.saveState();
        }

        public String getCommandText()
        {
            return "Pass";
        }

        public String getHelpText()
        {
            // TODO Auto-generated method stub
            return null;
        }
    },
    PlayCards
    {

        public void doCommand(final Participant user, final GameSession game, final String param) throws com.sawdust.engine.common.GameException
        {
            final GameSession gameSession = game;
            final Game baseGame = gameSession.getLatestState();
            final EuchreGame euchreGame = (EuchreGame) baseGame;
            if (!euchreGame.getCurrentPhase().equals(EuchreGame.PLAYING)) throw new GameLogicException("You cannot play a card right now!");
            if (!euchreGame.getCurrentPlayer().equals(user)) throw new GameLogicException("It is not your turn!");

            final String cards[] = param.split("\\s+");
            if (cards.length != 1) throw new GameLogicException("Invalid param count (expected 1): " + param);
            int cardIndexToPlay = -1;
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
                cardIndexToPlay = cardSlot;
            }
            if (-1 == cardIndexToPlay) throw new GameLogicException("?");

            if (!user.equals(euchreGame.getCurrentPlayer())) throw new GameLogicException("It is not your turn");
            euchreGame.doCommand(EuchreCommand.Play, user, cardIndexToPlay);
            baseGame.saveState();
        }

        public String getCommandText()
        {
            return "Play";
        }

        public String getHelpText()
        {
            // TODO Auto-generated method stub
            return null;
        }
    };

    public abstract void doCommand(final Participant user, final GameSession game, final String param) throws GameException, com.sawdust.engine.common.GameException;

    public abstract String getCommandText();

    public abstract String getHelpText();

    public GameCommand getGameCommand(final BaseGame game) {
		return new GameCommand() {
			
			@Override
			public String getHelpText() {
				return Command.this.getHelpText();
			}
			
			@Override
			public String getCommandText() {
				return Command.this.getCommandText();
			}
			
			@Override
			public boolean doCommand(Participant p) throws GameException, com.sawdust.engine.common.GameException {
				Command.this.doCommand(p, game.getSession(), "");
				return true;
			}
		};
	}
}
