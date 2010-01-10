package com.sawdust.games.stop.tutorial.basic;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.controller.exceptions.GameLogicException;
import com.sawdust.engine.model.TutorialPhase;
import com.sawdust.engine.model.basetypes.TutorialGameBase;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.state.GameCommand;
import com.sawdust.engine.view.game.GameFrame;
import com.sawdust.engine.view.game.Notification;
import com.sawdust.games.stop.StopGame;

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
