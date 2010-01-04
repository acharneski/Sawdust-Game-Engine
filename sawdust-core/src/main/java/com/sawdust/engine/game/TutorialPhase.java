package com.sawdust.engine.game;

import java.io.Serializable;

import com.sawdust.engine.service.debug.GameException;
import com.sawdust.engine.common.game.GameFrame;
import com.sawdust.engine.game.basetypes.GameState;
import com.sawdust.engine.game.basetypes.TutorialGameBase;
import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.game.state.GameCommand;
import com.sawdust.engine.service.debug.GameLogicException;

public interface TutorialPhase<T extends GameState> extends Serializable
{

    boolean allowCommand(TutorialGameBase<T> game, GameCommand m);

    TutorialPhase<T> preCommand(TutorialGameBase<T> game, GameCommand m, Participant p) throws GameLogicException;

    GameFrame filterDisplay(GameFrame gwt);

    TutorialPhase<T> postCommand(TutorialGameBase<T> game, GameCommand m, Participant p) throws GameLogicException;

   void onStartPhase(TutorialGameBase<T> game) throws GameException;
    
}
