package com.sawdust.engine.game.players;

import java.io.Serializable;

import com.sawdust.engine.service.data.Account;

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

    public abstract void logActivity(ActivityEvent event);

    public abstract Account loadAccount();

}
