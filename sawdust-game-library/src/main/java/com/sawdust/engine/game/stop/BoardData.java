package com.sawdust.engine.game.stop;

import java.io.Serializable;

public class BoardData implements Serializable
{
    public BoardData(int v)
    {
        value = v;
    }

    public int value = -1;
}
