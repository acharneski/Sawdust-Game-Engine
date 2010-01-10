package com.sawdust.engine.model;

import java.io.Serializable;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.controller.exceptions.GameLogicException;
import com.sawdust.engine.model.basetypes.GameState;
import com.sawdust.engine.model.basetypes.TutorialGameBase;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.state.GameCommand;
import com.sawdust.engine.view.game.GameFrame;

public interface TutorialPhase<T extends GameState> extends Serializable
{

    boolean allowCommand(TutorialGameBase<T> game, GameCommand m);

    TutorialPhase<T> preCommand(TutorialGameBase<T> game, GameCommand m, Participant p) throws GameLogicException;

    GameFrame filterDisplay(GameFrame gwt);

    TutorialPhase<T> postCommand(TutorialGameBase<T> game, GameCommand m, Participant p) throws GameLogicException;

   void onStartPhase(TutorialGameBase<T> game) throws GameException;
    
}
