package com.sawdust.engine.view;

import java.io.Serializable;

public interface IGameType extends Serializable
{
    public abstract String getDescription();

    public abstract String getName();

    public abstract String getID();
}
