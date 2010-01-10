package com.sawdust.games.stop.tutorial.basic;

import java.util.ArrayList;

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

public class Surround2 extends Phases
{
   
   public Surround2() {}
   
   public static final Surround2 INSTANCE = new Surround2();

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
      
      game.getInnerGame().resetBoard();
      game.getInnerGame().setBoardData(3,4,0);
      game.getInnerGame().setBoardData(3,3,0);
      game.getInnerGame().setBoardData(2,4,0);

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
      return Surround3.INSTANCE;
   }

   @Override
   public GameFrame filterDisplay(GameFrame gwt)
   {
      Notification notification = new Notification();
      notification.notifyText = "Good job. When stones of the same color are adjacent, "
         + "they form islands. The entire island must be surrounded in order to capture the stones. "
         + "Capture the black island.";
      gwt.setNotification(notification);
      return gwt;
   }
   
}
