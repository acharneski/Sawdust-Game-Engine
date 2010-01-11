/**
 * 
 */
package com.sawdust.games.euchre;


import com.sawdust.engine.controller.entities.GameSession;
import com.sawdust.engine.controller.entities.GameSession.SessionStatus;
import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.controller.exceptions.GameLogicException;
import com.sawdust.engine.controller.exceptions.SawdustSystemError;
import com.sawdust.engine.model.basetypes.BaseGame;
import com.sawdust.engine.model.basetypes.GameState;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.players.Player;
import com.sawdust.engine.model.state.CommandResult;
import com.sawdust.engine.model.state.GameCommand;
import com.sawdust.engine.view.cards.Suits;

public enum Command
{
    CallSuit
    {

        public void doCommand(final Participant user, final GameSession game, final String param) throws GameException
        {
            final GameState baseGame = game.getState();
            final EuchreGame euchreGame = (EuchreGame) baseGame;
            if (!user.equals(euchreGame.getCurrentPlayer())) throw new GameLogicException("It is not your turn");
            try
            {
                euchreGame.doCommand(EuchreCommand.Call, Suits.getSuitFromFullString(param));
            }
            catch (com.sawdust.engine.view.GameException e)
            {
                throw new SawdustSystemError(e);
            }
            baseGame.doSaveState();
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
            final GameState baseGame = game.getState();
            final GameSession gameSession = game;
            if (SessionStatus.Playing == gameSession.getStatus()) throw new GameLogicException("A game is in progress");
            gameSession.setStatus(SessionStatus.Closed, baseGame);
            baseGame.doSaveState();
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

        public void doCommand(final Participant user, final GameSession game, final String param) throws GameException
        {
            final GameState baseGame = game.getState();
            final EuchreGame euchreGame = (EuchreGame) baseGame;
            final boolean notCurrentPlayer = (user == null) || !user.equals(euchreGame.getCurrentPlayer());
            if (notCurrentPlayer) throw new GameLogicException("It is not your turn");
            euchreGame.doCommand(EuchreCommand.Pass);
            baseGame.doSaveState();
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

        public void doCommand(final Participant user, final GameSession game, final String param) throws GameException
        {
            final GameSession gameSession = game;
            final GameState baseGame = gameSession.getState();
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
            baseGame.doSaveState();
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

    public abstract void doCommand(final Participant user, final GameSession game, final String param) throws GameException;

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
			public CommandResult doCommand(Participant p, String commandText) throws GameException {
				Command.this.doCommand(p, game.getSession(), "");
				return new CommandResult<GameState>(game);
			}
		};
	}
}
