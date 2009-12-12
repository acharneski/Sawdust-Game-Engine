package com.sawdust.engine.game.go.tutorial.basic;

import com.sawdust.engine.common.GameException;
import com.sawdust.engine.common.game.GameState;
import com.sawdust.engine.common.game.Notification;
import com.sawdust.engine.game.TutorialGameBase;
import com.sawdust.engine.game.TutorialPhase;
import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.game.state.GameCommand;
import com.sawdust.engine.game.blackjack.BlackjackGame;
import com.sawdust.engine.game.go.GoGame;
import com.sawdust.engine.service.debug.GameLogicException;

public class Phases implements TutorialPhase<GoGame>
{
   protected Phases()
   {
   }
   
   @Override
   public boolean allowCommand(TutorialGameBase<GoGame> game, GameCommand m)
   {
      return true;
   }
   
   @Override
   public GameState filterDisplay(GameState gwt)
   {
      return gwt;
   }
   
   @Override
   public TutorialPhase<GoGame> preCommand(TutorialGameBase<GoGame> game, GameCommand m, Participant p) throws GameLogicException
   {
      return this;
   }
   
   @Override
   public TutorialPhase<GoGame> postCommand(TutorialGameBase<GoGame> game, GameCommand m, Participant p) throws GameLogicException
   {
      return null;
   }
   
   @Override
   public void onStartPhase(TutorialGameBase<GoGame> game) throws GameException
   {
   }
}
