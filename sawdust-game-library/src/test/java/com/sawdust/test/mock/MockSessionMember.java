package com.sawdust.test.mock;

import com.sawdust.engine.game.players.Player;
import com.sawdust.engine.service.data.Account;
import com.sawdust.engine.service.data.SessionMember;

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
