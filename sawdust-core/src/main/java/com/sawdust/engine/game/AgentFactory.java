package com.sawdust.engine.game;

import com.sawdust.engine.game.players.Agent;

public abstract class AgentFactory<T extends Agent<?>>
{
    public abstract T getAgent(String string);

    public abstract String getName();
}
