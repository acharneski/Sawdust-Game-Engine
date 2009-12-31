/**
 * 
 */
package com.sawdust.engine.game.euchre;

import java.util.ArrayList;
import java.util.Collection;

import com.sawdust.engine.common.cards.Suits;
import com.sawdust.engine.common.game.Message.MessageType;
import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.game.players.Player;
import com.sawdust.engine.game.state.GameCommand;
import com.sawdust.engine.game.state.GameLabel;
import com.sawdust.engine.game.state.IndexCard;
import com.sawdust.engine.game.state.IndexPosition;
import com.sawdust.engine.service.data.GameSession;
import com.sawdust.engine.service.debug.GameException;
import com.sawdust.engine.service.debug.GameLogicException;

final class OpenMakingPhase extends GamePhase
{
    public static final GamePhase INSTANCE = new OpenMakingPhase();

    public void callSuit(final EuchreGame game, final Suits suit) throws GameException
    {
        final IndexCard playCard = (IndexCard) game.getToken(new IndexPosition(EuchreLayout.POS_IN_PLAY, 0));
        if (Suits.Null == suit) throw new GameLogicException("You must name a trump suit");
        if (playCard.getCard().getSuit() == suit) throw new GameLogicException("You cannot call the same suit as the turn-up");
        // TODO: Consolidate with InitialMaking
        game.remove(playCard);
        game._trumpSuit = suit;
        game._maker = game.getPlayerManager().getCurrentPlayer();
        game.addMessage(MessageType.Compact, "<strong>Trump suit is called %s</strong>", game._trumpSuit.fullString());
        game.addMessage("");
        game.setCurrentPhase(EuchreGame.PLAYING);
        game.getPlayerManager().setCurrentPlayer(0);
        game._roundStartPlayer = game.getPlayerManager().gotoNextPlayer();
        game.addMessage(MessageType.Compact, "It is now %s's turn: ", game.displayName(game._roundStartPlayer));
        game._winningCard = null;
    }

    @Override
    public void doCommand(final EuchreGame game, final EuchreCommand cmd, final Object... params) throws GameException
    {
        switch (cmd)
        {
        case Call:
            callSuit(game, (Suits) params[0]);
            break;
        case Pass:
            passSuit(game);
            break;
        default:
            throw new GameLogicException(String.format("Unknown command %s while in state %s", cmd, this));
        }
    }

    @Override
    public String getId()
    {
        return "Open Making";
    }

    @Override
    public ArrayList<GameCommand> getMoves(final Participant access, final EuchreGame game) throws GameException
    {
        final ArrayList<GameCommand> returnValue = new ArrayList<GameCommand>();
        returnValue.add(com.sawdust.engine.game.euchre.Command.PassSuit.getGameCommand(game));
        for (final Suits suit : Suits.values())
        {
            if (suit == Suits.Null)
            {
                continue;
            }
            returnValue.add(new GameCommand() {
				
				@Override
				public String getHelpText() {
					return null;
				}
				
				@Override
				public String getCommandText() {
                    return "Call " + suit.fullString();
				}
				
				@Override
				public boolean doCommand(Participant p, String commandText) throws GameException {
                    com.sawdust.engine.game.euchre.Command.CallSuit.doCommand((Player) p, game.getSession(), suit.fullString());
					return true;
				}
			});
        }
        return returnValue;
    }

    protected void passSuit(final EuchreGame game) throws GameException
    {
        final Participant currentPlayer = game.getPlayerManager().getCurrentPlayer();
        if (!game.getCurrentPhase().equals(EuchreGame.INITIAL_MAKING) && !game.getCurrentPhase().equals(EuchreGame.OPEN_MAKING)) throw new GameLogicException(
                "Invalid command... the game is not making");
        final Participant nextPlayer = game.getPlayerManager().gotoNextPlayer();
        game.addMessage(MessageType.Compact, "%s passed.", game.displayName(currentPlayer));
        game.addMessage("");
        game.addMessage(MessageType.Compact, "It is now %s's turn: ", game.displayName(nextPlayer));

        if ((0 == game.getPlayerManager().findPlayer(currentPlayer)) && EuchreGame.INITIAL_MAKING.equals(game.getCurrentPhase()))
        {
            game.addMessage("<strong>Any suit can now be named trump</strong>");
            game.setCurrentPhase(EuchreGame.OPEN_MAKING);
        }
    }

    @Override
    public Collection<GameLabel> setupLabels(final EuchreGame game, final Player access)
    {
        final ArrayList<GameLabel> returnValue = new ArrayList<GameLabel>();
        returnValue.addAll(game.getPlayerLabels());

        if (game.getPlayerManager().getCurrentPlayer().equals(access))
        {
            int index = 0;
            GameLabel label = new GameLabel("GeneralCommand " + index, new IndexPosition(EuchreLayout.POS_GENERAL_CMDS, index), "Pass");
            label.setCommand("Pass");
            returnValue.add(label);
            index++;

            label = new GameLabel("GeneralCommand " + index, new IndexPosition(EuchreLayout.POS_GENERAL_CMDS, index), "Call:");
            returnValue.add(label);
            index++;

            for (final Suits suit : Suits.values())
            {
                if (Suits.Null == suit)
                {
                    continue;
                }
                final String cmd = "Call " + suit.fullString();
                label = new GameLabel("GeneralCommand " + index, new IndexPosition(EuchreLayout.POS_GENERAL_CMDS, index), suit.fullString());
                returnValue.add(label);
                label.setCommand(cmd);
                index += 5;
                if (index > 10)
                {
                    index -= 9;
                }
            }
        }
        return returnValue;
    }
}
