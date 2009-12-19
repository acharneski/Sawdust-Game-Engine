package com.sawdust.server.datastore.entities;

import java.text.DateFormat;
import java.util.Date;

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
import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.common.config.LeagueConfig;
import com.sawdust.engine.service.Util;
import com.sawdust.engine.service.debug.GameException;
import com.sawdust.server.datastore.DataObj;
import com.sawdust.server.datastore.DataStore;
import com.sawdust.server.datastore.DataObj;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class GameLeague extends DataObj
{
    public static GameLeague load(final Key key)
    {
        try
        {
            final GameLeague returnValue = DataStore.Get(GameLeague.class, key);
            return returnValue;
        }
        catch (final Exception e)
        {
            e.printStackTrace(System.out);
            return null;
        }
    }

    public static GameLeague load(final String pName)
    {
        GameLeague myData = null;
        final PersistenceManager entityManager = DataStore.create();
        try
        {
            final Query newQuery = entityManager.newQuery(GameLeague.class);
            newQuery.setFilter("name == pName");
            newQuery.setUnique(true);
            newQuery.declareParameters("String pName");
            myData = (GameLeague) newQuery.execute(pName);
        }
        catch (final Exception e)
        {
            myData = null;
        }
        if (null == myData)
        {
            entityManager.close();
        }
        return myData;
    }

    @Persistent
    private com.google.appengine.api.datastore.Blob _config = null;

    @Persistent
    private Key owner;

    private GameLeague()
    {
        super();
    }

    public GameLeague(final Account account) throws GameException
    {
        super(KeyFactory.createKey(GameLeague.class.getSimpleName(), (account.getKey() + "%" + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date()))));
        if (this != DataStore.Add(this)) throw new AssertionError();
        setOwner(account);
    }

    /**
     * @param powner
     * @param name
     * @param _config
     * @throws GameException 
     */
    public GameLeague(final Account powner, final LeagueConfig config, final GameConfig game) throws GameException
    {
        this(powner);
        setConfig(game);
        final PersistenceManager entityManager = DataStore.create();
        setEntityManager(entityManager);
        DataStore.Add(this);
    }

    public GameConfig getConfig()
    {
        return Util.fromBytes(_config.getBytes());
    }

    public Account getOwner()
    {
        return Account.Load(owner);
    }

    public void setConfig(final GameConfig config)
    {
        _config = new Blob(Util.toBytes(config));
    }

    public void setOwner(final Account powner)
    {
        owner = powner.getKey();
    }
}
