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

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.sawdust.engine.controller.Util;
import com.sawdust.engine.controller.entities.BankAccount;
import com.sawdust.engine.model.players.ActivityEvent;
import com.sawdust.gae.datastore.DataObj;
import com.sawdust.gae.datastore.DataStore;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class ActivityEventRecord extends DataObj
{

    private static final int MAX_RECORDS = 10;

    public static List<ActivityEventRecord> getTransactionsSince(final String accountId, final Date time)
    {
        final PersistenceManager entityManager = DataStore.create();
        final List<ActivityEventRecord> finalResult = new ArrayList<ActivityEventRecord>();
        final Query newQuery = entityManager.newQuery(ActivityEventRecord.class);
        newQuery.setFilter("playerId == param1 && time > _time");
        newQuery.declareParameters("String param1, java.util.Date _time");
        newQuery.setOrdering("time asc");
        newQuery.setRange(0, MAX_RECORDS);
        final List<ActivityEventRecord> queryResult = (List<ActivityEventRecord>) newQuery.execute(accountId, time);
        for (final ActivityEventRecord transaction : queryResult)
        {
            finalResult.add(transaction);
        }
        return finalResult;
    }

    public static List<ActivityEventRecord> getTransactionsUntil(final String accountId, final Date time)
    {
        final PersistenceManager entityManager = DataStore.create();
        final List<ActivityEventRecord> finalResult = new ArrayList<ActivityEventRecord>();
        final Query newQuery = entityManager.newQuery(ActivityEventRecord.class);
        newQuery.setFilter("playerId == param1 && time < _time");
        newQuery.declareParameters("String param1, java.util.Date _time");
        newQuery.setOrdering("time desc");
        newQuery.setRange(0, MAX_RECORDS);
        final List<ActivityEventRecord> queryResult = (List<ActivityEventRecord>) newQuery.execute(accountId, time);
        for (final ActivityEventRecord transaction : queryResult)
        {
            finalResult.add(transaction);
        }
        return finalResult;
    }

    public static ActivityEventRecord Load(final Key key)
    {
        final ActivityEventRecord myData = DataStore.Get(ActivityEventRecord.class, key);
        return myData;
    }

    @Persistent
    public String eventType;

    @Persistent
    public String playerId;

    @Persistent
    public Date time = new Date();

    @Persistent
    private com.google.appengine.api.datastore.Blob state = null;

    /**
	 * 
	 */
    private ActivityEventRecord()
    {
        super();
    }

    protected ActivityEventRecord(final BankAccount player, final ActivityEvent event)
    {
        super((KeyFactory.createKey(ActivityEventRecord.class.getSimpleName(), 
                (null==player?null:player.getStringId()) + 
                DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date()) +
                Integer.toString((int) Math.round(Math.random() * 100)))));
        eventType = event.type;
        playerId = player.getStringId();
        setData(event);
        if (this != DataStore.Add(this)) throw new AssertionError();
    }

    public void setData(ActivityEvent event)
    {
        this.state = new Blob(Util.toBytes(event));
    }

    public ActivityEvent getData()
    {
        if(null == this.state) return null;
        return (ActivityEvent) Util.fromBytes(this.state.getBytes());
    }
}
