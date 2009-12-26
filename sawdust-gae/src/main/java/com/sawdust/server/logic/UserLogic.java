package com.sawdust.server.logic;

import java.io.Serializable;

public abstract class UserLogic implements Serializable
{
    public abstract void publishActivity(String message);
}
