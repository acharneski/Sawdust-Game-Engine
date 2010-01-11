package com.sawdust.engine.model.players;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.basetypes.GameState;
import com.sawdust.engine.model.state.GameCommand;

public abstract class MoveFactory<G extends GameState> 
{
    public abstract GameCommand<G> getMove(G game, Participant participant) throws GameException;
}
