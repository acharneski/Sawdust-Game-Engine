package com.sawdust.engine.game;

import java.io.Serializable;

import com.sawdust.engine.common.GameException;
import com.sawdust.engine.common.game.GameState;
import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.game.state.GameCommand;
import com.sawdust.engine.service.debug.GameLogicException;

public interface TutorialPhase<T extends Game> extends Serializable
{

    boolean allowCommand(TutorialGameBase<T> game, GameCommand m);

    TutorialPhase<T> preCommand(TutorialGameBase<T> game, GameCommand m, Participant p) throws GameLogicException;

    GameState filterDisplay(GameState gwt);

    TutorialPhase<T> postCommand(TutorialGameBase<T> game, GameCommand m, Participant p) throws GameLogicException;

   void onStartPhase(TutorialGameBase<T> game) throws GameException;
    
}
