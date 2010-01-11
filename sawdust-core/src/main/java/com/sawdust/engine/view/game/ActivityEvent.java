package com.sawdust.engine.view.game;

import java.io.Serializable;


public class ActivityEvent implements Serializable
{
    public ActivityEvent(String type, Message event)
    {
        super();
        this.type = type;
        this.event = event;
    }
    public ActivityEvent(String type, String event)
    {
        super();
        this.type = type;
        this.event = new Message(event);
    }
    protected ActivityEvent()
    {
        super();
    }

    public Message event;
    public String type;
}
