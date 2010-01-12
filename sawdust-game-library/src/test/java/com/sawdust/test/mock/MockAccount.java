package com.sawdust.test.mock;

import java.io.Serializable;

import com.sawdust.engine.controller.PromotionConfig;
import com.sawdust.engine.controller.entities.Account;
import com.sawdust.engine.controller.entities.GameSession;
import com.sawdust.engine.controller.entities.Promotion;
import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.Bank;
import com.sawdust.engine.view.game.ActivityEvent;

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

    public void doRemoveSession(GameSession gameSession)
    {
        // TODO Auto-generated method stub

    }

    public void setName(String displayName)
    {
        this.name = displayName;
    }

    public void doWithdraw(int amount, Bank from, String description) throws GameException
    {
        this.bank -= amount;
        if (null != from) from.doWithdraw(-amount, null, description);
    }

    public boolean isAdmin()
    {
        return false;
    }

    public void setAdmin(boolean isAdmin)
    {
    }

    @Override
    public <T extends Serializable> T getResource(Class<T> c)
    {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public <T extends Serializable> void setResource(Class<T> c, T markovChain)
    {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public Promotion doAwardPromotion(PromotionConfig p) throws GameException
    {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void doLogActivity(ActivityEvent event)
    {
    }

}
