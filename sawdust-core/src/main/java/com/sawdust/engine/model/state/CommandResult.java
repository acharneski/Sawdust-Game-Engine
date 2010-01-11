package com.sawdust.engine.model.state;

import java.util.ArrayList;

import com.sawdust.engine.model.basetypes.GameState;

public class CommandResult<T extends GameState>
{
    public final T state[];

    public CommandResult(T... p)
    {
        super();
        ArrayList<T> arrayList = new ArrayList<T>(p.length);
        for(int i=0;i<p.length;i++)
        {
            arrayList.add(p[i]);
        }
        state = arrayList.toArray(p);
    }
}
