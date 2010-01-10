package com.sawdust.test.mock;

import com.sawdust.engine.controller.entities.Account;
import com.sawdust.engine.controller.entities.SessionMember;
import com.sawdust.engine.model.players.Player;

public class MockSessionMember implements SessionMember
{
    private Player _account;

    public MockSessionMember(Player account)
    {
        _account = account;
    }

    public Account getAccount()
    {
        return _account.loadAccount();
    }

    public Player getPlayer()
    {
        return _account;
    }

}
