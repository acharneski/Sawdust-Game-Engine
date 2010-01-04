package com.sawdust.engine.game;

import com.sawdust.engine.game.basetypes.GameState;

public abstract class GameModification<T extends GameState>
{
    public abstract void apply(T game);
}
