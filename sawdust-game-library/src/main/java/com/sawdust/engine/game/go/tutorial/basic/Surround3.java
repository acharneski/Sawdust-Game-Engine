package com.sawdust.engine.game.go.tutorial.basic;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.sawdust.engine.service.debug.GameException;
import com.sawdust.engine.common.game.GameFrame;
import com.sawdust.engine.common.game.Notification;
import com.sawdust.engine.game.TutorialPhase;
import com.sawdust.engine.game.basetypes.TutorialGameBase;
import com.sawdust.engine.game.go.GoAgent1;
import com.sawdust.engine.game.players.Agent;
import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.game.state.GameCommand;
import com.sawdust.engine.game.state.IndexPosition;
import com.sawdust.engine.game.stop.BoardData;
import com.sawdust.engine.game.stop.StopIsland;
import com.sawdust.engine.game.go.GoAgent1;
import com.sawdust.engine.game.go.GoGame;
import com.sawdust.engine.service.debug.GameLogicException;

public class Surround3 extends Phases
{
    private static final Logger LOG = Logger.getLogger(Surround3.class.getName());

    public Surround3()
    {
    }

    public static final Surround3 INSTANCE = new Surround3();

    private Agent<GoGame> _agent = new GoAgent1("Do Nothing", 1, 30)
    {

        @Override
        public void Move(GoGame game, Participant participant) throws GameException
        {
            LOG.fine("_agent.Move");
            game.finishTurn(participant);
            // super.Move(game,
            // participant);
        }

    };

    @Override
    public void onStartPhase(TutorialGameBase<GoGame> game) throws GameException
    {
        LOG.fine("onStartPhase");
        super.onStartPhase(game);
        game.setAgent(_agent);

        game.getInnerGame().resetBoard();
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
    public TutorialPhase<GoGame> preCommand(TutorialGameBase<GoGame> game, GameCommand m, Participant p) throws GameLogicException
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
    public TutorialPhase<GoGame> postCommand(TutorialGameBase<GoGame> game, GameCommand m, Participant p) throws GameLogicException
    {
        ArrayList<StopIsland> islands = game.getInnerGame().getTokenArray().getIslands();
        for (StopIsland i : islands)
        {
            if (i.getPlayer() == 0)
            {
                LOG.fine("Post-command: Still in Surround3 phase");
                return super.postCommand(game, m, p);
            }
        }
        LOG.fine("Post-command: Finished Surround3 phase");
        game.getInnerGame().resetBoard();
        return Go1.INSTANCE;
    }

    @Override
    public GameFrame filterDisplay(GameFrame gwt)
    {
        Notification notification = new Notification();
        notification.notifyText = "There are two types of points in Go: "+
            "<ol>"+
            "<li>Territory - Empty nodes surrounded by a single color count as territory points</li>"+
            "<li>Prisoners - Captured stones become prisoners, permanently reducing that player's score</li>"+
            "</ol>"+
            "Pay attention to the score as you capture all the black pieces.";
        gwt.setNotification(notification);
        return gwt;
    }

}
