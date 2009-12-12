package com.sawdust.test.mock;

import com.sawdust.engine.common.Bank;
import com.sawdust.engine.service.data.Account;
import com.sawdust.engine.service.data.GameSession;
import com.sawdust.engine.service.debug.GameException;

public class MockAccount implements Account
{
    int bank = 10;
    String email;
    String name;

    /**
     * @param pemail
     */
    public MockAccount(String pemail)
    {
        super();
        this.email = pemail;
        this.name = pemail;
    }

    public int getBalance()
    {
        return bank;
    }

    public String getName()
    {
        return name;
    }

    public String getUserId()
    {
        return email;
    }

    public void removeSession(GameSession gameSession)
    {
        // TODO Auto-generated method stub

    }

    public void setName(String displayName)
    {
        this.name = displayName;
    }

    public void withdraw(int amount, Bank from, String description) throws com.sawdust.engine.common.GameException
    {
        this.bank -= amount;
        if (null != from) from.withdraw(-amount, null, description);
    }

    public boolean isAdmin()
    {
        return false;
    }

    public void setAdmin(boolean isAdmin)
    {
    }

}
