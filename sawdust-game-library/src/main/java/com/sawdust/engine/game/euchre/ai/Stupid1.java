package com.sawdust.engine.game.euchre.ai;

import com.sawdust.engine.common.cards.Suits;
import com.sawdust.engine.game.euchre.EuchreCommand;
import com.sawdust.engine.game.euchre.EuchreGame;
import com.sawdust.engine.game.euchre.TeamStatus;
import com.sawdust.engine.game.players.Agent;
import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.game.players.PlayerManager;
import com.sawdust.engine.game.state.IndexCard;
import com.sawdust.engine.game.state.IndexPosition;
import com.sawdust.engine.service.debug.GameException;

public class Stupid1 extends Agent<EuchreGame>
{
    public Stupid1(final String s)
    {
        super(s);
    }

    @Override
    public void Move(final EuchreGame game, final Participant player) throws GameException
    {
        if (game.getCurrentPhase().equals(EuchreGame.INITIAL_MAKING))
        {
            game.doCommand(EuchreCommand.Call);
        }
        else if (game.getCurrentPhase().equals(EuchreGame.OPEN_MAKING))
        {
            game.doCommand(EuchreCommand.Pass);
        }
        else if (game.getCurrentPhase().equals(EuchreGame.PLAYING))
        {

            final PlayerManager playerManager = game.getPlayerManager();
            final int currentPlayerIndex = playerManager.getCurrentPlayerIndex();
            final Suits leadingSuit = game.getLeadingSuit();
            int originalScore = 0;
            for (final TeamStatus i : game.getTeamStatuses().values())
            {
                originalScore += i.currentHandCount;
            }

            final boolean canLeadSuit = game.playerCanLead(currentPlayerIndex);
            for (int cardIndexToPlay = 0; cardIndexToPlay < EuchreGame.NUMBER_OF_CARDS; cardIndexToPlay++)
            {
                final IndexCard cardToPlay = (IndexCard) game.getToken(new IndexPosition(playerManager.getCurrentPlayerIndex(), cardIndexToPlay));
                if (null == cardToPlay)
                {
                    continue;
                }
                final boolean isLeadingSuit = (game.getEffectiveSuit(cardToPlay.getCard()) == leadingSuit);
                if (isLeadingSuit || !canLeadSuit)
                {
                    game.doCommand(EuchreCommand.Play, player, cardIndexToPlay);
                    break;
                }
            }
            int newScore = 0;
            for (final TeamStatus i : game.getTeamStatuses().values())
            {
                newScore += i.currentHandCount;
            }
            final boolean isSamePlayer = playerManager.getCurrentPlayer().equals(player);
            if (isSamePlayer && (newScore > originalScore))
            {
                // Sometimes we end up with the turn again, if we win the
                // round...
                Move(game, player);
            }
        }
        else if (game.getCurrentPhase().equals(EuchreGame.DEALING))
        {
            // Do nothing
        }
        else if (game.getCurrentPhase().equals(EuchreGame.COMPLETE))
        {
            // Do nothing
        }
        else if (game.getCurrentPhase().equals(EuchreGame.FORMING))
        {
            // Do nothing
        }
        else
        {
            // game.print("Failed force move");
        }
    }
}
