package com.sawdust.engine.game;

public abstract class GameModification<T extends Game>
{
    public abstract void apply(T game);
}
