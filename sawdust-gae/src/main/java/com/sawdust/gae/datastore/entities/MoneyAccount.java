package com.sawdust.gae.datastore.entities;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.sawdust.gae.datastore.DataObj;
import com.sawdust.gae.datastore.DataStore;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class MoneyAccount extends DataObj implements com.sawdust.engine.controller.entities.BankAccount
{
   private static final Logger LOG = Logger.getLogger(MoneyAccount.class.getName());

    public static MoneyAccount Load(final Key key)
    {
        final MoneyAccount myData = DataStore.Get(MoneyAccount.class, key);
        return myData;
    }

    public static MoneyAccount Load(final String accountId, Key key)
    {
        MoneyAccount myData = null;
        final PersistenceManager entityManager = DataStore.create();
        try
        {
            final Query newQuery = entityManager.newQuery(MoneyAccount.class);
            newQuery.setFilter("accountId == param1");
            newQuery.setUnique(true);
            newQuery.declareParameters("String param1");
            myData = (MoneyAccount) newQuery.execute(accountId);
        }
        catch (final Throwable e)
        {
            myData = null;
        }
        if (null == myData)
        {
            myData = new MoneyAccount(accountId, key);
            MoneyTransaction.Transfer(null, myData, 10, "Welcome to Sawdust Games!");
            DataStore.Save();
        }
        return myData;
    }

    @Persistent
    public String accountId;

    @Persistent
    public int currentBalence = 0;

    @Persistent
    private String displayName;

    protected MoneyAccount() {
        super();
    }
    
    public MoneyAccount(final String acctId, final Key key)
    {
        super((KeyFactory.createKey(MoneyAccount.class.getSimpleName(), acctId)));
        accountId = acctId;
        if (this != DataStore.Add(this)) throw new AssertionError();
    }

    public String getDisplayName()
    {
        if ((displayName == null) || displayName.isEmpty()) return accountId;
        return displayName;
    }

    public List<MoneyTransaction> getTransactionsSince(final Date time)
    {
        return MoneyTransaction.getTransactionsSince(accountId, time);
    }

    public List<MoneyTransaction> getTransactionsUntil(final Date time)
    {
        return MoneyTransaction.getTransactionsUntil(accountId, time);
    }

    public void setDisplayName(final String pdisplayName)
    {
        displayName = pdisplayName;
    }

    @Override
    public String getStringId()
    {
        return accountId;
    }

    @Override
    public int getBalance()
    {
        return currentBalence;
    }

    @Override
    public void setBalance(int i)
    {
        LOG.info(String.format("Set %s balence = %d", accountId, i));
//        if(i < Account.MIN_CREDITS)
//        {
//            LOG.fine(String.format("Override balence to %d", Account.MIN_CREDITS));
//            i = Account.MIN_CREDITS;
//        }
        currentBalence = i;
    }
}
