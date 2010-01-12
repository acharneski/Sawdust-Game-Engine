package com.sawdust.games.go.tutorial.basic;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.controller.exceptions.GameLogicException;
import com.sawdust.engine.model.TutorialPhase;
import com.sawdust.engine.model.basetypes.TutorialGameBase;
import com.sawdust.engine.model.players.Agent;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.state.GameCommand;
import com.sawdust.engine.view.game.GameFrame;
import com.sawdust.engine.view.game.Notification;
import com.sawdust.games.go.GoAgent1;
import com.sawdust.games.go.GoGame;
import com.sawdust.games.stop.StopIsland;

public class Go1 extends Phases
{
    private static final Logger LOG = Logger.getLogger(Go1.class.getName());

    public Go1()
    {
    }

    public static final Phases INSTANCE = new Go1();

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
        GoGame innerGame = game.getInnerGame();
        innerGame.doResetBoard();
        innerGame.getPlayerManager().setCurrentPlayer(1);

        setGameLayout(innerGame, new char[][]
        {
        // --------1----2----3----4----5----6----7----8----9
                { ' ', 'w', 'w', 'w', 'w', 'w', 'w', ' ', ' ' }, // 1
                { ' ', 'w', 'b', 'b', ' ', ' ', 'w', ' ', ' ' }, // 2
                { 'w', 'w', 'b', 'b', ' ', ' ', 'w', ' ', ' ' }, // 3
                { ' ', 'w', 'b', 'b', ' ', ' ', 'w', ' ', ' ' }, // 4
                { 'w', 'w', 'b', 'b', ' ', ' ', 'w', ' ', ' ' }, // 5
                { ' ', 'w', 'b', 'b', ' ', ' ', 'w', ' ', ' ' }, // 6
                { 'w', 'w', 'b', 'b', ' ', ' ', 'w', 'w', 'w' }, // 7
                { ' ', 'w', 'b', 'b', ' ', ' ', 'w', ' ', ' ' }, // 8
                { ' ', 'w', 'b', 'b', ' ', ' ', 'w', ' ', ' ' } // 9
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
            LOG.fine("Pre-command: Non-Move command");
            return this;
        }
    }

    @Override
    public TutorialPhase<GoGame> doOnPostCommand(TutorialGameBase<GoGame> game, GameCommand m, Participant p) throws GameLogicException
    {
        if (game.getInnerGame().getCurrentPhase() == GoGame.GamePhase.Playing)
        {
            LOG.fine("Post-command: Still in Go1 phase");
            return super.doOnPostCommand(game, m, p);
        }
        else
        {
            LOG.fine("Post-command: Finished Go1 phase");
            game.getInnerGame().doResetBoard();
            return Welcome1.INSTANCE;
        }
    }

    @Override
    public GameFrame getFilteredDisplay(GameFrame gwt)
    {
        Notification notification = new Notification();
        notification.notifyText = "In order to win, the basic strategy of Go is to "+
            "<ol>"+
            "<li><b>Surround Territory</b> - You want to surround empty spaces, filling them in sparsely to gain maximum score</li>"+
            "<li><b>Defend Territory</b> - Form continuous islands that cannot be surrounded</li>"+
            "<li><b>Capture Prisoners</b> - Invade the opponent to reduce his territory and capture prisoners</li>"+
            "</ol>"+
            "The game is finished when both player pass, and the winner is then determined by the highest total score (territory minus prisoners). Win this game:";
        notification.commands.put("Reset", "Reset");
        gwt.setNotification(notification);
        return gwt;
    }

}
