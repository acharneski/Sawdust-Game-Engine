package com.sawdust.engine.game.players;

import com.sawdust.engine.game.Game;
import com.sawdust.engine.service.debug.GameException;

public abstract class Agent<G extends Game> extends Participant
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
