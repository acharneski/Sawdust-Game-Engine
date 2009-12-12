package com.sawdust.engine.game.stop.tutorial.basic;

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
import com.sawdust.engine.game.stop.StopIsland;
import com.sawdust.engine.game.stop.StopAgent1;
import com.sawdust.engine.game.stop.StopGame;
import com.sawdust.engine.service.debug.GameLogicException;

public class Stop1 extends Phases
{
   private static final Logger LOG = Logger.getLogger(Stop1.class.getName());
   
   public Stop1()
   {
   }
   
   public static final Stop1 INSTANCE = new Stop1();
   
   private Agent<StopGame>       _agent   = new StopAgent1<StopGame>("Do Nothing", 1, 30)
                                        {
                                           
                                           @Override
                                           public void Move(StopGame game, Participant participant) throws GameException
                                           {
                                              LOG.fine("_agent.Move");
                                              // game.finishTurn(participant);
                                              super.Move(game, participant);
                                           }
                                           
                                        };
   
   @Override
   public void onStartPhase(TutorialGameBase<StopGame> game) throws GameException
   {
      LOG.fine("onStartPhase");
      super.onStartPhase(game);
      game.setAgent(_agent);
      game.getInnerGame().resetBoard();
      game.getInnerGame().getPlayerManager().setCurrentPlayer(1);
      
      game.getInnerGame().setBoardData(0, 2, 1);
      game.getInnerGame().setBoardData(1, 2, 1);
      game.getInnerGame().setBoardData(2, 2, 1);
      game.getInnerGame().setBoardData(3, 2, 1);
      // game.setBoardData(4,4,0);
      game.getInnerGame().setBoardData(5, 2, 1);
      game.getInnerGame().setBoardData(6, 2, 1);
      game.getInnerGame().setBoardData(7, 2, 1);
      game.getInnerGame().setBoardData(8, 2, 1);
      
      game.getInnerGame().setBoardData(4, 3, 1);
      game.getInnerGame().setBoardData(4, 4, 1);
      game.getInnerGame().setBoardData(4, 5, 1);
      game.getInnerGame().setBoardData(4, 6, 1);
      game.getInnerGame().setBoardData(4, 7, 1);
      game.getInnerGame().setBoardData(4, 8, 1);
      
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
         LOG.fine("Pre-command: Non-Move command");
         return this;
      }
   }
   
   @Override
   public TutorialPhase<StopGame> postCommand(TutorialGameBase<StopGame> game, GameCommand m, Participant p) throws GameLogicException
   {
      if (game.getInnerGame().getCurrentPhase() == StopGame.GamePhase.Playing)
      {
         LOG.fine("Post-command: Still in GoBang1 phase");
         return super.postCommand(game, m, p);
      }
      else
      {
         LOG.fine("Post-command: Finished GoBang1 phase");
         game.getInnerGame().resetBoard();
         return Welcome1.INSTANCE;
      }
   }
   
   @Override
   public GameState filterDisplay(GameState gwt)
   {
      Notification notification = new Notification();
      notification.notifyText = "The game of Go! differs from the game of Go in a few ways. "
            + "First, islands that touch two opposite sides cannot be captured. "
            + "Once one player controls most of the board, they win the game. "
            + "Board 'control' is determined by surrounding areas with islands that spans opposite sides. " + "Win this game: ";
      notification.commands.put("Reset", "Reset");
      gwt.setNotification(notification);
      return gwt;
   }
   
}
