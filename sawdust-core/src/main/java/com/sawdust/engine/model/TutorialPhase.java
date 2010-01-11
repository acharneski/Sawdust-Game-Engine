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

    TutorialPhase<T> doOnPostCommand(TutorialGameBase<T> game, GameCommand m, Participant p) throws GameLogicException;

    TutorialPhase<T> doOnPreCommand(TutorialGameBase<T> game, GameCommand m, Participant p) throws GameLogicException;

    void doOnStartPhase(TutorialGameBase<T> game) throws GameException;

    boolean getAllowCommand(TutorialGameBase<T> game, GameCommand m);

   GameFrame getFilteredDisplay(GameFrame gwt);
    
}
