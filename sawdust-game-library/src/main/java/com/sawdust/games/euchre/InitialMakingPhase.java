/**
 * 
 */
package com.sawdust.games.euchre;

import java.util.ArrayList;
import java.util.Collection;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.controller.exceptions.GameLogicException;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.players.Player;
import com.sawdust.engine.model.state.GameCommand;
import com.sawdust.engine.model.state.GameLabel;
import com.sawdust.engine.model.state.IndexCard;
import com.sawdust.engine.model.state.IndexPosition;
import com.sawdust.engine.view.cards.Suits;
import com.sawdust.engine.view.game.Message.MessageType;

final class InitialMakingPhase extends GamePhase
{
    public static final GamePhase INSTANCE = new InitialMakingPhase();

    public void callSuit(final EuchreGame game) throws GameException
    {
        /*
         * R-E-0031: The remaining four cards are called the kitty, but are sometimes referred to as R-E-0032: the kit, the widow, the
         * blind, the dead hand, the grave, or buried and are R-E-0033: placed face down in front of the dealer toward the center on the
         * table. The top R-E-0034: card of the kitty, sometimes referred to as the deck head, or the "up card" is R-E-0035: then turned
         * face up, and bidding begins. The dealer asks each of the other R-E-0036: players in turn if they would like the suit of the top
         * card to be trump, which R-E-0037: they indicate saying "pick it up" and the top card becomes part of the dealer's R-E-0038: hand,
         * who then discards to return his hand to five cards. If no one "orders up" *
         */

        /*
         * R-E-0084: In euchre, naming trump is sometimes referred to as "making," "calling," or R-E-0085: "declaring trump". When naming a
         * suit, a player asserts that his or her R-E-0086: partnership intends to win the majority of tricks in the hand (3 of 5 with a
         * R-E-0087: 24-card deck, 4 of 7 with 32 cards). A single point is scored when the bid
         */

        /*
         * R-E-0110: Once the cards are dealt and the top card in the kitty is turned over, the R-E-0111: upturned card's suit is offered as
         * trump to the players in clockwise order R-E-0112: beginning with the player to the left of the dealer. If a player wishes the
         */
        final IndexCard playCard = (IndexCard) game.getToken(new IndexPosition(EuchreLayout.POS_IN_PLAY, 0));
        final Suits suit = playCard.getCard().getSuit();
        game.doRemoveToken(playCard);
        game.getDeck().discard(playCard.getCard());
        game._trumpSuit = suit;
        game._maker = game.getPlayerManager().getCurrentPlayer();
        game.doAddMessage(MessageType.Compact, "<strong>Trump suit is called %s.</strong>", game._trumpSuit.fullString());
        game.doAddMessage("");
        game.setCurrentPhase(EuchreGame.PLAYING);
        game._roundStartPlayer = game.getPlayerManager().gotoNextPlayer();
        /*
         * R-E-0142: The player to the dealer's left begins play by leading a card. (In some R-E-0143: variations, if any player is going
         * alone, the player to that person's left will R-E-0144: lead.)
         */
        game.doAddMessage(MessageType.Compact, "It is now %s's turn: ", game.getDisplayName(game._roundStartPlayer));
        game._winningCard = null;
    }

    @Override
    public void doCommand(final EuchreGame game, final EuchreCommand cmd, final Object... params) throws GameException
    {
        switch (cmd)
        {
        case Call:
            callSuit(game);
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
        return "Initial Making";
    }

    @Override
    public ArrayList<GameCommand> getMoves(final Participant access, final EuchreGame game) throws GameException
    {
        final ArrayList<GameCommand> returnValue = new ArrayList<GameCommand>();
        returnValue.add(com.sawdust.games.euchre.Command.PassSuit.getGameCommand(game));
        returnValue.add(com.sawdust.games.euchre.Command.CallSuit.getGameCommand(game));
        return returnValue;
    }

    protected void passSuit(final EuchreGame game) throws GameException
    {
        final Participant currentPlayer = game.getPlayerManager().getCurrentPlayer();
        final Participant nextPlayer = game.getPlayerManager().gotoNextPlayer();
        game.doAddMessage(MessageType.Compact, "Passed.");
        game.doAddMessage("");
        game.doAddMessage(MessageType.Compact, "It is now %s's turn: ", game.getDisplayName(nextPlayer));
        if ((0 == game.getPlayerManager().findPlayer(currentPlayer)) && EuchreGame.INITIAL_MAKING.equals(game.getCurrentPhase()))
        {
            game.doAddMessage("Any suit can now be named trump");
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
            returnValue.add(label);
            label.setCommand("Pass");
            index++;

            label = new GameLabel("GeneralCommand " + index, new IndexPosition(EuchreLayout.POS_GENERAL_CMDS, index), "Call");
            returnValue.add(label);
            label.setCommand("Call");
            index++;
        }

        return returnValue;
    }
}
