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
import com.sawdust.engine.game.stop.StopIsland;
import com.sawdust.engine.game.go.GoAgent1;
import com.sawdust.engine.game.go.GoGame;
import com.sawdust.engine.service.debug.GameLogicException;

public class Go1 extends Phases
{
   private static final Logger LOG = Logger.getLogger(Go1.class.getName());
   
   public Go1()
   {
   }
   
   public static final Go1 INSTANCE = new Go1();
   
   private Agent<GoGame>   _agent   = new GoAgent1("Do Nothing", 1, 30)
                                    {
                                       
                                       @Override
                                       public void Move(GoGame game, Participant participant) throws GameException
                                       {
                                          LOG.fine("_agent.Move");
                                          // game.finishTurn(participant);
                                          super.Move(game, participant);
                                       }
                                       
                                    };
   
   @Override
   public void onStartPhase(TutorialGameBase<GoGame> game) throws GameException
   {
      LOG.fine("onStartPhase");
      super.onStartPhase(game);
      game.setAgent(_agent);
      game.getInnerGame().resetBoard();
      game.getInnerGame().getPlayerManager().setCurrentPlayer(1);
      
      char[][] data = { 
            // 1   2   3   4   5   6   7   8   9
            { ' ','w','b','b',' ',' ',' ',' ',' ' }, // 1 
            { ' ','w','b','b',' ',' ',' ',' ',' ' }, // 2 
            { 'w',' ','b','b',' ',' ',' ',' ',' ' }, // 3 
            { ' ','w','b','b',' ',' ',' ',' ',' ' }, // 4 
            { 'w','w','b','b',' ',' ',' ',' ',' ' }, // 5 
            { ' ','w','b','b',' ',' ',' ',' ',' ' }, // 6 
            { 'w','w','b','b',' ',' ',' ',' ',' ' }, // 7 
            { ' ','w','b','b',' ',' ',' ',' ',' ' }, // 8 
            { ' ','w','b','b',' ',' ',' ',' ',' ' }  // 9 
         };
      for(int i=0;i<data.length;i++)
      {
         for(int j=0;j<data[i].length;j++)
         {
            if(data[i][j] == 'w')
            {
               game.getInnerGame().setBoardData(i, j, 1);
            }
            else if(data[i][j] == 'b')
            {
               game.getInnerGame().setBoardData(i, j, 0);
            }
         }
      }
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
         LOG.fine("Pre-command: Non-Move command");
         return this;
      }
   }
   
   @Override
   public TutorialPhase<GoGame> postCommand(TutorialGameBase<GoGame> game, GameCommand m, Participant p) throws GameLogicException
   {
      if (game.getInnerGame().getCurrentPhase() == GoGame.GamePhase.Playing)
      {
         LOG.fine("Post-command: Still in Go1 phase");
         return super.postCommand(game, m, p);
      }
      else
      {
         LOG.fine("Post-command: Finished Go1 phase");
         game.getInnerGame().resetBoard();
         return Welcome1.INSTANCE;
      }
   }
   
   @Override
   public GameState filterDisplay(GameState gwt)
   {
      Notification notification = new Notification();
      notification.notifyText = "The goals in Go are to: 1) Surround territory 2) Capture pieces 3) Defend against capture<br/>Defeat this opponent!";
      notification.commands.put("Reset", "Reset");
      gwt.setNotification(notification);
      return gwt;
   }
   
}
