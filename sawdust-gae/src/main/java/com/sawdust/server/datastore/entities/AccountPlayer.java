/**
 * 
 */
package com.sawdust.server.datastore.entities;

import com.sawdust.engine.game.players.ActivityEvent;
import com.sawdust.engine.game.players.Player;

public final class AccountPlayer extends Player
{
    protected AccountPlayer() {}

    public AccountPlayer(final Account account2)
    {
        super(account2.getUserId(), account2.isAdmin());
    }

    @Override
    public com.sawdust.engine.service.data.Account loadAccount()
    {
        return Account.Load(getUserId());
    }

    @Override
    public void logActivity(ActivityEvent event)
    {
        ((Account) loadAccount()).logActivity(event);
    }
}
