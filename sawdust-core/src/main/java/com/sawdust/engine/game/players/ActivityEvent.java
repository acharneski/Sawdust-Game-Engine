package com.sawdust.engine.game.players;

import java.io.Serializable;

public class ActivityEvent implements Serializable
{
    public ActivityEvent(String type, String event)
    {
        super();
        this.type = type;
        this.event = event;
    }
    protected ActivityEvent()
    {
        super();
    }
    public String event;
    public String type;
}
