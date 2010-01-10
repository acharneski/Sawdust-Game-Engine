package com.sawdust.engine.model.players;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.basetypes.GameState;

public abstract class Agent<G extends GameState> extends Participant
{
    protected Agent()
    {
    }

    public Agent(final String s)
    {
        super(s);
    }

    public abstract void Move(G game, Participant participant) throws GameException;
}
