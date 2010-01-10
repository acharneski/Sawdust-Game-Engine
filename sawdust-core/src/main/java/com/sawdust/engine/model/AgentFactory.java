package com.sawdust.engine.model;

import com.sawdust.engine.model.players.Agent;

public abstract class AgentFactory<T extends Agent<?>>
{
    public abstract T getAgent(String string);

    public abstract String getName();
}
