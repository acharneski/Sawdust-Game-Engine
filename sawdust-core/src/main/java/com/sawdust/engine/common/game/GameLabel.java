package com.sawdust.engine.common.game;

import java.io.Serializable;

import com.sawdust.engine.common.geometry.Position;

public class GameLabel implements Serializable
{
    public String command = null;
    public String key = null;
    public Position position = null;
    public String text = null;
    public int width;
}
