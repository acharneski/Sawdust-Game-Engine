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
import com.sawdust.engine.view.game.Message.MessageType;

final class DealingPhase extends GamePhase
{
    public static final GamePhase INSTANCE = new DealingPhase();

    protected void deal(final EuchreGame game)
    {
        /*
         * R-E-0014: To determine the first deal, many players use a first Jack deals or first black R-E-0015: Jack deals rule. Using the
         * euchre deck, one player will distribute the cards, R-E-0016: one at a time, face up in front of each player. The player dealt the
         * first R-E-0017: (black) jack becomes the dealer for the first hand. In subsequent hands, the R-E-0018: deal is rotated clockwise.
         * Out of courtesy, the dealer should offer a cut to R-E-0019: the player on his right after shuffling and immediately before
         * dealing. In this version, "Player 2" will deal first, and the winner of each round thereafter deals R-E-0020: R-E-0021: Each
         * player is dealt five cards (or seven if using the 32-card deck) in R-E-0022: clockwise order, usually in groups of two or three
         * cards each. The dealer may R-E-0023: alternate, first giving two cards to the player to his left, three cards to his R-E-0024:
         * partner, two cards to the player on his right and three cards to himself. The R-E-0025: dealer then repeats, this time giving
         * three cards to the player on his left, two R-E-0026: cards to his partner and so on, to give each player the requisite five
         * cards. R-E-0027: Some dealers prefer to deal going down or up such as 4-3-2-1 then 1-2-3-4, R-E-0028: although it doesn't matter
         * what order the cards are dealt in as long as each R-E-0029: person gets 5 cards. Each person gets 5 random cards. We don't
         * simulate anything as complex as card deal order.
         */
        game._roundNumber = 0;
        game.getTeamStatus(0).currentHandCount = 0;
        game.getTeamStatus(1).currentHandCount = 0;
        game.doClearTokens();
        game._winningCard = null;
        game.getDeck().setReshuffleEnabled(true);
        game.setCurrentPhase(EuchreGame.DEALING);

        game.getPlayerManager().setCurrentPlayer(0);

        final IndexPosition playSlot = new IndexPosition(EuchreLayout.POS_IN_PLAY, 0);
        final IndexCard playCard = game.doDealNewCard(playSlot);
        playCard.setPublic();

        for (int player = 0; player < EuchreGame.NUMBER_OF_PLAYERS; player++)
        {
            final Participant thisPlayer = game.getPlayerManager().getPlayerName(player);
            game.doAddMessage(MessageType.Compact, "%s's hand: ", game.getDisplayName(thisPlayer)).setTo(thisPlayer.getId());
            for (int cardSlot = 0; cardSlot < EuchreGame.NUMBER_OF_CARDS; cardSlot++)
            {
                final IndexPosition pos = new IndexPosition(player, cardSlot);
                final IndexCard t = game.doDealNewCard(pos);
                t.setOwner(thisPlayer);
                t.setPrivate("VR");
                t.getMoveCommands().put(playSlot, "Play " + cardSlot);
                t.setMovable(true);
                game.doAddMessage(MessageType.Compact, "(%s) ", t.getCard()).setTo(thisPlayer.getId());
            }
            game.doAddMessage("");
        }
        game._maker = null;
        game._roundStartPlayer = null;
        game.setCurrentPhase(EuchreGame.INITIAL_MAKING);

        game.doAddMessage("The dealt trump suit candidate is %s: ", playCard.getCard().getSuit().fullString());
        game.doAddMessage(MessageType.Compact, "It is %s's turn to call or pass: ", game.getDisplayName(game.getPlayerManager().gotoNextPlayer()));
    }

    @Override
    public void doCommand(final EuchreGame game, final EuchreCommand cmd, final Object... params) throws GameException
    {
        switch (cmd)
        {
        case Deal:
            deal(game);
            break;
        default:
            throw new GameLogicException(String.format("Unknown command %s while in state %s", cmd, this));
        }
    }

    @Override
    public String getId()
    {
        return "Dealing";
    }

    @Override
    public ArrayList<GameCommand<?>> getMoves(final Participant access, final EuchreGame game) throws GameException
    {
        final ArrayList<GameCommand<?>> returnValue = new ArrayList<GameCommand<?>>();
        return returnValue;
    }

    @Override
    public Collection<GameLabel> setupLabels(final EuchreGame game, final Player access)
    {
        return game.getPlayerLabels();
    }
}
