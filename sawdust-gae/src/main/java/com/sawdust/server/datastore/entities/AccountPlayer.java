/**
 * 
 */
package com.sawdust.server.datastore.entities;

import java.util.logging.Logger;

import com.sawdust.engine.game.players.ActivityEvent;
import com.sawdust.engine.game.players.Player;

public final class AccountPlayer extends Player
{
    private static final Logger LOG = Logger.getLogger(Object.class.getName());

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
        LOG.info(String.format("Event: %s: %s",getUserId(),event.toString()));
        ((Account) loadAccount()).logActivity(event);
    }
}
