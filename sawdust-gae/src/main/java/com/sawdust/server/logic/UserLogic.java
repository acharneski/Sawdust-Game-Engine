package com.sawdust.server.logic;

import java.io.Serializable;

import com.sawdust.engine.common.game.Message;

public abstract class UserLogic implements Serializable
{
    public abstract void publishActivity(Message message);
}
