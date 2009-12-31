package com.sawdust.engine.game.stop.tutorial.basic;

import java.util.ArrayList;

import com.sawdust.engine.service.debug.GameException;
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
import com.sawdust.engine.game.stop.StopAgent1;
import com.sawdust.engine.game.stop.StopGame;
import com.sawdust.engine.service.debug.GameLogicException;

public class Surround1 extends Phases
{
   
   public Surround1() {}
   
   public static final Surround1 INSTANCE = new Surround1();
   private Agent<StopGame> _agent = new StopAgent1<StopGame>("Do Nothing", 1, 30) {

      @Override
      public void Move(StopGame game, Participant participant) throws GameException
      {
         game.finishTurn(participant);
         //super.Move(game, participant);
      }
      
   };

   @Override
   public void onStartPhase(TutorialGameBase<StopGame> game) throws GameException
   {
      super.onStartPhase(game);
      game.setAgent(_agent );
      
      for (int i = 0; i < StopGame.NUM_ROWS; i++)
      {
         for (int j = 0; j < StopGame.NUM_ROWS; j++)
         {
            BoardData boardData = game.getInnerGame().getBoardData(i, j);
            if (null != boardData && 1 == boardData.value)
            {
               game.getInnerGame().resetBoard();
               game.getInnerGame().setBoardData(i,j,0);
            }
         }
      }
   }

   @Override
   public TutorialPhase<StopGame> preCommand(TutorialGameBase<StopGame> game, GameCommand m, Participant p) throws GameLogicException
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
   public TutorialPhase<StopGame> postCommand(TutorialGameBase<StopGame> game, GameCommand m, Participant p) throws GameLogicException
   {
      ArrayList<StopIsland> islands = game.getInnerGame().getTokenArray().getIslands();
      for(StopIsland i : islands)
      {
         if(i.getPlayer() == 0) return super.postCommand(game, m, p); 
      }
      game.getInnerGame().resetBoard();
      return Surround2.INSTANCE;
   }

   @Override
   public GameState filterDisplay(GameState gwt)
   {
      Notification notification = new Notification();
      notification.notifyText = "When stones surround others of the opposite color, " + 
         "the surrounded stones are captured. Capture this black stone by " + 
         "placing your stones to surround it.";
      gwt.setNotification(notification);
      return gwt;
   }
   
}
