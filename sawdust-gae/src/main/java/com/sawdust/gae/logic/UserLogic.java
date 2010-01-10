package com.sawdust.gae.logic;

import java.io.Serializable;

import com.sawdust.engine.view.game.Message;

public abstract class UserLogic implements Serializable
{
    public abstract void publishActivity(Message message);
}
