package com.sawdust.games.stop.tutorial.basic;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.controller.exceptions.GameLogicException;
import com.sawdust.engine.model.TutorialPhase;
import com.sawdust.engine.model.basetypes.TutorialGameBase;
import com.sawdust.engine.model.players.Agent;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.state.GameCommand;
import com.sawdust.engine.model.state.IndexPosition;
import com.sawdust.engine.view.game.GameFrame;
import com.sawdust.engine.view.game.Notification;
import com.sawdust.games.go.GoAgent1;
import com.sawdust.games.stop.BoardData;
import com.sawdust.games.stop.StopAgent1;
import com.sawdust.games.stop.StopGame;
import com.sawdust.games.stop.StopIsland;

public class Surround3 extends Phases
{
   private static final Logger LOG = Logger.getLogger(Surround3.class.getName());
   
   public Surround3()
   {
   }
   
   public static final Surround3 INSTANCE = new Surround3();
   
   private Agent<StopGame>       _agent   = new StopAgent1<StopGame>("Do Nothing", 1, 30)
                                          {
                                             
                                             @Override
                                             public void Move(StopGame game, Participant participant) throws GameException
                                             {
                                                LOG.fine("_agent.Move");
                                                game.doFinishTurn(participant);
                                                // super.Move(game,
                                                // participant);
                                             }
                                             
                                          };
   
   @Override
   public void onStartPhase(TutorialGameBase<StopGame> game) throws GameException
   {
      LOG.fine("onStartPhase");
      super.onStartPhase(game);
      game.setAgent(_agent);
      
      game.getInnerGame().doResetBoard();
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
   public TutorialPhase<StopGame> preCommand(TutorialGameBase<StopGame> game, GameCommand m, Participant p) throws GameLogicException
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
   public TutorialPhase<StopGame> postCommand(TutorialGameBase<StopGame> game, GameCommand m, Participant p) throws GameLogicException
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
      game.getInnerGame().doResetBoard();
      return Stop1.INSTANCE;
   }
   
   @Override
   public GameFrame filterDisplay(GameFrame gwt)
   {
      Notification notification = new Notification();
      notification.notifyText = "The border counts as being surrounded, and interior spaces can be used to help secure islands. Capture this island.";
      gwt.setNotification(notification);
      return gwt;
   }
   
}
