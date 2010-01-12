package com.sawdust.games.euchre.ai;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.players.Agent;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.players.PlayerManager;
import com.sawdust.engine.model.state.CommandResult;
import com.sawdust.engine.model.state.GameCommand;
import com.sawdust.engine.model.state.IndexCard;
import com.sawdust.engine.model.state.IndexPosition;
import com.sawdust.engine.view.cards.Suits;
import com.sawdust.games.euchre.EuchreCommand;
import com.sawdust.games.euchre.EuchreGame;
import com.sawdust.games.euchre.TeamStatus;
import com.sawdust.engine.model.players.MoveFactory;

public class Stupid1 extends MoveFactory<EuchreGame>
{
    public static Agent<EuchreGame> getAgent(final String s)
    {
        return new Agent<EuchreGame>(s,new Stupid1());
    }

    @Override
    public GameCommand<EuchreGame> getMove(final EuchreGame game, final Participant player) throws GameException
    {
        return new GameCommand<EuchreGame>()
        {
            
            @Override
            public CommandResult<EuchreGame> doCommand(Participant p, String parameters) throws GameException
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

                    final boolean canLeadSuit = game.getPlayerCanLead(currentPlayerIndex);
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
                        getMove(game, player);
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
                return new CommandResult<EuchreGame>(game);
            }
        };
    }
}
