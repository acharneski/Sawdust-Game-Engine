package com.sawdust.engine.model.players;

import java.io.Serializable;

import com.sawdust.engine.controller.entities.Account;
import com.sawdust.engine.view.game.ActivityEvent;

public abstract class Player extends Participant implements Serializable
{
    protected boolean _isAdmin;

    protected Player()
    {
    }

    protected Player(final String userId, final boolean isAdmin)
    {
        super(userId);
        _isAdmin = isAdmin;
    }

    public String getUserId()
    {
        return getId();
    }

    public boolean isAdmin()
    {
        return _isAdmin;
    }

    public abstract void doLogActivity(ActivityEvent event);

    public abstract Account getAccount();

}
