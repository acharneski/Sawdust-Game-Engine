package com.sawdust.engine.game.stop.tutorial.basic;

import com.sawdust.engine.service.debug.GameException;
import com.sawdust.engine.common.game.GameFrame;
import com.sawdust.engine.common.game.Notification;
import com.sawdust.engine.game.TutorialPhase;
import com.sawdust.engine.game.basetypes.TutorialGameBase;
import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.game.state.GameCommand;
import com.sawdust.engine.game.stop.StopGame;
import com.sawdust.engine.service.debug.GameLogicException;

public class Phases implements TutorialPhase<StopGame>
{
   protected Phases()
   {
   }
   
   @Override
   public boolean allowCommand(TutorialGameBase<StopGame> game, GameCommand m)
   {
      return true;
   }
   
   @Override
   public GameFrame filterDisplay(GameFrame gwt)
   {
      return gwt;
   }
   
   @Override
   public TutorialPhase<StopGame> preCommand(TutorialGameBase<StopGame> game, GameCommand m, Participant p) throws GameLogicException
   {
      return this;
   }
   
   @Override
   public TutorialPhase<StopGame> postCommand(TutorialGameBase<StopGame> game, GameCommand m, Participant p) throws GameLogicException
   {
      return null;
   }
   
   @Override
   public void onStartPhase(TutorialGameBase<StopGame> game) throws GameException
   {
   }
}
