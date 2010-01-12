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
import com.sawdust.engine.model.state.IndexPosition;
import com.sawdust.engine.view.game.GameFrame;
import com.sawdust.engine.view.game.Notification;
import com.sawdust.games.go.GoAgent1;
import com.sawdust.games.go.GoGame;
import com.sawdust.games.stop.BoardData;
import com.sawdust.games.stop.StopIsland;

public class Surround1 extends Phases
{

    public Surround1()
    {
    }

    public static final Surround1 INSTANCE = new Surround1();

    private static final Logger LOG = Logger.getLogger(Surround1.class.getName());
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
        super.doOnStartPhase(game);
        game.setAgent(_agent);

        game.getInnerGame().doResetBoard();
        setGameLayout(game.getInnerGame(), new char[][]
        {
        // --------1----2----3----4----5----6----7----8----9
                { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' }, // 1
                { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' }, // 2
                { ' ', ' ', ' ', ' ', 'w', ' ', ' ', ' ', ' ' }, // 3
                { ' ', ' ', ' ', 'w', 'b', ' ', ' ', ' ', ' ' }, // 4
                { ' ', ' ', ' ', ' ', 'w', ' ', ' ', ' ', ' ' }, // 5
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
            return null;
        }
        else
        {
            return this;
        }
    }

    @Override
    public TutorialPhase<GoGame> doOnPostCommand(TutorialGameBase<GoGame> game, GameCommand m, Participant p) throws GameLogicException
    {
        ArrayList<StopIsland> islands = game.getInnerGame().getTokenArray().getIslands();
        for (StopIsland i : islands)
        {
            if (i.getPlayer() == 0) return super.doOnPostCommand(game, m, p);
        }
        game.getInnerGame().doResetBoard();
        return Surround2.INSTANCE;
    }

    @Override
    public GameFrame getFilteredDisplay(GameFrame gwt)
    {
        Notification notification = new Notification();
        notification.notifyText = "When stones surround others of the opposite color, "
                + "the surrounded stones are captured. Capture this black stone by " + "placing your stones to surround it.";
        gwt.setNotification(notification);
        return gwt;
    }

}
