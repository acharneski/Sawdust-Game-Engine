package com.sawdust.games.stop.tutorial.basic;

import com.sawdust.engine.controller.exceptions.GameLogicException;
import com.sawdust.engine.model.TutorialPhase;
import com.sawdust.engine.model.basetypes.TutorialGameBase;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.state.GameCommand;
import com.sawdust.engine.view.game.GameFrame;
import com.sawdust.engine.view.game.Notification;
import com.sawdust.games.stop.StopGame;

public class Welcome1 extends Phases
{
   protected Welcome1() {}
   
   public static final Welcome1 INSTANCE = new Welcome1();
   
   @Override
   public TutorialPhase<StopGame> doOnPostCommand(TutorialGameBase<StopGame> game, GameCommand m, Participant p) throws GameLogicException
   {
      if (m.getCommandText().startsWith("Move"))
      {
         return Surround1.INSTANCE;
      }
      else
      {
         return this;
      }
   }
   
   @Override
   public GameFrame getFilteredDisplay(GameFrame gwt)
   {
      Notification notification = new Notification();
      notification.notifyText = "Welcome to Go! To start, please drag your white 'stone' on the right and place it on the board.";
      gwt.setNotification(notification);
      return gwt;
   }
   
}
