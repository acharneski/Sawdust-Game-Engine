package com.sawdust.games.go.tutorial.basic;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.controller.exceptions.GameLogicException;
import com.sawdust.engine.model.TutorialPhase;
import com.sawdust.engine.model.basetypes.TutorialGameBase;
import com.sawdust.engine.model.players.Agent;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.state.CommandResult;
import com.sawdust.engine.model.state.GameCommand;
import com.sawdust.engine.view.game.GameFrame;
import com.sawdust.engine.view.game.Notification;
import com.sawdust.games.go.GoAgent1;
import com.sawdust.games.go.GoGame;
import com.sawdust.games.stop.StopIsland;

public class Surround3 extends Phases
{
    private static final Logger LOG = Logger.getLogger(Surround3.class.getName());

    public Surround3()
    {
    }

    public static final Surround3 INSTANCE = new Surround3();

    private Agent<GoGame> _agent = new Agent<GoGame>("Do Nothing", new GoAgent1(1, 30)
    {
        @Override
        public GameCommand<GoGame> getMove(GoGame game, Participant participant) throws GameException
        {
            LOG.fine("_agent.Move");
            return super.getMove(game, participant);
        }
    });

    @Override
    public void doOnStartPhase(TutorialGameBase<GoGame> game) throws GameException
    {
        LOG.fine("onStartPhase");
        super.doOnStartPhase(game);
        game.setAgent(_agent);

        game.getInnerGame().doResetBoard();
        setGameLayout(game.getInnerGame(), new char[][]
        {
        // --------1----2----3----4----5----6----7----8----9
                { ' ', 'b', ' ', 'b', 'w', ' ', ' ', ' ', ' ' }, // 1
                { 'b', ' ', 'b', 'w', ' ', ' ', ' ', ' ', ' ' }, // 2
                { ' ', 'b', 'w', ' ', ' ', ' ', ' ', ' ', ' ' }, // 3
                { 'b', 'w', ' ', ' ', ' ', ' ', ' ', ' ', ' ' }, // 4
                { 'w', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' }, // 5
                { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' }, // 6
                { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' }, // 7
                { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' }, // 8
                { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' } // 9
                });
    }

    @Override
    public TutorialPhase<GoGame> doOnPreCommand(TutorialGameBase<GoGame> game, GameCommand m, Participant p) throws GameLogicException
    {
        if (m.getCommandText().startsWith("Move"))
        {
            LOG.fine("Pre-command: Move command");
            return null;
        }
        else
        {
            LOG.fine("Pre-command: Non-move command");
            return this;
        }
    }

    @Override
    public TutorialPhase<GoGame> doOnPostCommand(TutorialGameBase<GoGame> game, GameCommand m, Participant p) throws GameLogicException
    {
        ArrayList<StopIsland> islands = game.getInnerGame().getTokenArray().getIslands();
        for (StopIsland i : islands)
        {
            if (i.getPlayer() == 0)
            {
                LOG.fine("Post-command: Still in Surround3 phase");
                return super.doOnPostCommand(game, m, p);
            }
        }
        LOG.fine("Post-command: Finished Surround3 phase");
        game.getInnerGame().doResetBoard();
        return Go1.INSTANCE;
    }

    @Override
    public GameFrame getFilteredDisplay(GameFrame gwt)
    {
        Notification notification = new Notification();
        notification.notifyText = "There are two types of points in Go: " + "<ol>"
                + "<li>Territory - Empty nodes surrounded by a single color count as territory points</li>"
                + "<li>Prisoners - Captured stones become prisoners, permanently reducing that player's score</li>" + "</ol>"
                + "Pay attention to the score as you capture all the black pieces.";
        gwt.setNotification(notification);
        return gwt;
    }

}
