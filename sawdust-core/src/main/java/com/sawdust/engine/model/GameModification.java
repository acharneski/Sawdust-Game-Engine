package com.sawdust.engine.model;

import com.sawdust.engine.model.basetypes.GameState;

public abstract class GameModification<T extends GameState>
{
    public abstract T apply(T game);
}
