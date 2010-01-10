package com.sawdust.gae.datastore.entities;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.sawdust.engine.controller.entities.BankAccount;
import com.sawdust.gae.datastore.DataObj;
import com.sawdust.gae.datastore.DataStore;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class MoneyTransaction extends DataObj
{

    private static final int MAX_RECORDS = 10;

    public static List<MoneyTransaction> getTransactionsSince(final String accountId, final Date time)
    {
        final PersistenceManager entityManager = DataStore.create();
        final List<MoneyTransaction> finalResult = new ArrayList<MoneyTransaction>();
        // Do something for when this gets too big
        int maxRecords = MAX_RECORDS;
        {
            final Query newQuery = entityManager.newQuery(MoneyTransaction.class);
            newQuery.setFilter("senderId == param1 && time > _time");
            newQuery.declareParameters("String param1, java.util.Date _time");
            newQuery.setOrdering("time asc");
            final List<MoneyTransaction> queryResult = (List<MoneyTransaction>) newQuery.execute(accountId, time);
            for (final MoneyTransaction transaction : queryResult)
            {
                finalResult.add(transaction);
                if (--maxRecords < 0)
                {
                    break;
                }
            }
        }
        maxRecords = MAX_RECORDS;
        {
            final Query newQuery = entityManager.newQuery(MoneyTransaction.class);
            newQuery.setFilter("recipientId == param1 && time > _time");
            newQuery.declareParameters("String param1, java.util.Date _time");
            newQuery.setOrdering("time asc");
            final List<MoneyTransaction> queryResult = (List<MoneyTransaction>) newQuery.execute(accountId, time);
            for (final MoneyTransaction transaction : queryResult)
            {
                finalResult.add(transaction);
                if (--maxRecords < 0)
                {
                    break;
                }
            }
        }
        Collections.sort(finalResult, new Comparator<MoneyTransaction>()
        {
            public int compare(final MoneyTransaction o1, final MoneyTransaction o2)
            {
                return o1.time.compareTo(o2.time);
            }
        });
        return finalResult.subList(0, (finalResult.size() > MAX_RECORDS) ? MAX_RECORDS : finalResult.size());
    }

    public static List<MoneyTransaction> getTransactionsUntil(final String accountId, final Date time)
    {
        final PersistenceManager entityManager = DataStore.create();
        final List<MoneyTransaction> finalResult = new ArrayList<MoneyTransaction>();
        // Do something for when this gets too big
        int maxRecords = MAX_RECORDS;
        {
            final Query newQuery = entityManager.newQuery(MoneyTransaction.class);
            newQuery.setFilter("senderId == param1 && time <= _time");
            newQuery.declareParameters("String param1, java.util.Date _time");
            newQuery.setOrdering("time desc");
            final List<MoneyTransaction> queryResult = (List<MoneyTransaction>) newQuery.execute(accountId, time);
            for (final MoneyTransaction transaction : queryResult)
            {
                finalResult.add(transaction);
                if (--maxRecords < 0)
                {
                    break;
                }
            }
        }
        maxRecords = MAX_RECORDS;
        {
            final Query newQuery = entityManager.newQuery(MoneyTransaction.class);
            newQuery.setFilter("recipientId == param1 && time <= _time");
            newQuery.declareParameters("String param1, java.util.Date _time");
            newQuery.setOrdering("time desc");
            final List<MoneyTransaction> queryResult = (List<MoneyTransaction>) newQuery.execute(accountId, time);
            for (final MoneyTransaction transaction : queryResult)
            {
                finalResult.add(transaction);
                if (--maxRecords < 0)
                {
                    break;
                }
            }
        }
        Collections.sort(finalResult, new Comparator<MoneyTransaction>()
        {
            public int compare(final MoneyTransaction o1, final MoneyTransaction o2)
            {
                return o2.time.compareTo(o1.time);
            }
        });
        return finalResult.subList(0, (finalResult.size() > MAX_RECORDS) ? MAX_RECORDS : finalResult.size());
    }

    public static MoneyTransaction Load(final Key key)
    {
        final MoneyTransaction myData = DataStore.Get(MoneyTransaction.class, key);
        return myData;
    }

    public static MoneyTransaction Transfer(final BankAccount sender, final com.sawdust.engine.controller.entities.BankAccount recipient, final int amount, final String transactionDescription)
    {
        return new MoneyTransaction(sender, recipient, amount, transactionDescription);
    }

    @Persistent
    public String description;

    @Persistent
    public String recipientId;
    
    @Persistent
    public int recipientStartBalance = Integer.MIN_VALUE;

    @Persistent
    public int senderStartBalance = Integer.MIN_VALUE;
    
    @Persistent
    public int recipientEndBalance = Integer.MIN_VALUE;
    
    @Persistent
    public int senderEndBalance = Integer.MIN_VALUE;

    @Persistent
    public String senderId;

    @Persistent
    public Date time = new Date();

    @Persistent
    public int transactionAmount = 0;

    /**
	 * 
	 */
    private MoneyTransaction()
    {
        super();
    }

    private MoneyTransaction(final BankAccount sender, final BankAccount recipient, final int amount, final String transactionDescription)
    {
        super((KeyFactory.createKey(MoneyTransaction.class.getSimpleName(), 
                (null==sender?null:sender.getStringId()) + 
                (null==recipient?null:recipient.getStringId()) + 
                amount + 
                DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date()) +
                Integer.toString((int) Math.round(Math.random() * 100)))));
        if (null != sender)
        {
            senderId = sender.getStringId();
            senderStartBalance = sender.getBalance();
            int currentBalence = sender.getBalance();
            sender.setBalance(currentBalence - amount);
            senderEndBalance = sender.getBalance();
        }
        if (null != recipient)
        {
            recipientId = recipient.getStringId();
            recipientStartBalance = recipient.getBalance();
            int currentBalence = recipient.getBalance();
            recipient.setBalance(currentBalence + amount);
            recipientEndBalance = recipient.getBalance();
        }
        description = transactionDescription;
        transactionAmount = amount;
        if (this != DataStore.Add(this)) throw new AssertionError();
    }
}
