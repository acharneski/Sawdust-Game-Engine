package com.sawdust.engine.model.state;

import java.util.ArrayList;

import com.sawdust.engine.controller.Util;
import com.sawdust.engine.model.basetypes.GameState;

public class CommandResult<T extends GameState>
{
    public final ArrayList<T> state;

    public CommandResult(T... p)
    {
        super();
        state = new ArrayList<T>(p.length);
        for(int i=0;i<p.length;i++)
        {
            state.add(p[i]);
        }
    }

    public T addState(T game)
    {
        T copy = Util.Copy(game);
        state.add(copy);
        return copy;
    }

    public CommandResult()
    {
        super();
        state = new ArrayList<T>();
    }

    public T getLatestState()
    {
        int lastIndex = state.size()-1;
        return lastIndex<0?null:state.get(lastIndex);
    }
}
