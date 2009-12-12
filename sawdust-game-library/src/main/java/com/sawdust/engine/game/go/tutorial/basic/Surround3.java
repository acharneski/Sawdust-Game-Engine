package com.sawdust.engine.game.go.tutorial.basic;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.sawdust.engine.common.GameException;
import com.sawdust.engine.common.game.GameState;
import com.sawdust.engine.common.game.Notification;
import com.sawdust.engine.game.TutorialGameBase;
import com.sawdust.engine.game.TutorialPhase;
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
   
   private Agent<GoGame>       _agent   = new GoAgent1("Do Nothing", 1, 30)
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
      game.getInnerGame().setBoardData(0, 0, 0);
      game.getInnerGame().setBoardData(1, 0, 0);
      game.getInnerGame().setBoardData(2, 0, 0);
      
      game.getInnerGame().setBoardData(0, 1, 0);
      game.getInnerGame().setBoardData(2, 1, 0);
      
      game.getInnerGame().setBoardData(0, 2, 0);
      game.getInnerGame().setBoardData(1, 2, 0);
      game.getInnerGame().setBoardData(2, 2, 0);
      
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
   public GameState filterDisplay(GameState gwt)
   {
      Notification notification = new Notification();
      notification.notifyText = "The border counts as being surrounded, and interior spaces can be used to help secure islands. Capture this island.";
      gwt.setNotification(notification);
      return gwt;
   }
   
}
