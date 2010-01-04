package com.sawdust.engine.game.stop.tutorial.basic;

import com.sawdust.engine.common.game.GameFrame;
import com.sawdust.engine.common.game.Notification;
import com.sawdust.engine.game.TutorialPhase;
import com.sawdust.engine.game.basetypes.TutorialGameBase;
import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.game.state.GameCommand;
import com.sawdust.engine.game.stop.StopGame;
import com.sawdust.engine.service.debug.GameLogicException;

public class Welcome1 extends Phases
{
   protected Welcome1() {}
   
   public static final Welcome1 INSTANCE = new Welcome1();
   
   @Override
   public TutorialPhase<StopGame> postCommand(TutorialGameBase<StopGame> game, GameCommand m, Participant p) throws GameLogicException
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
   public GameFrame filterDisplay(GameFrame gwt)
   {
      Notification notification = new Notification();
      notification.notifyText = "Welcome to Go! To start, please drag your white 'stone' on the right and place it on the board.";
      gwt.setNotification(notification);
      return gwt;
   }
   
}
