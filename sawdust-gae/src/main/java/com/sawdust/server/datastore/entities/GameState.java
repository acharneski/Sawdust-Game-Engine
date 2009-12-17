package com.sawdust.server.datastore.entities;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.sawdust.engine.game.BaseGame;
import com.sawdust.engine.game.Game;
import com.sawdust.engine.service.Util;
import com.sawdust.engine.service.debug.GameException;
import com.sawdust.server.datastore.DataObj;
import com.sawdust.server.datastore.DataStore;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class GameState extends DataObj
{
    private static final Logger LOG = Logger.getLogger(GameState.class.getName());

    public static final Comparator<GameState> NaturalSort = new Comparator<GameState>()
    {
        public int compare(final GameState o1, final GameState o2)
        {
            return -o1.time.compareTo(o2.time);
        }
    };

    public static List<GameState> getStatesSince(final GameSession psession, final int version)
    {
        final PersistenceManager entityManager = DataStore.create();
        final List<GameState> finalResult = new ArrayList<GameState>();

        final Query newQuery = entityManager.newQuery(GameState.class);
        newQuery.setFilter("currentVersion > _currentVersion && session == _session");
        newQuery.declareParameters("java.lang.String _session, int _currentVersion");
        newQuery.setOrdering("currentVersion desc");
        final String keyToString = KeyFactory.keyToString(psession.getKey());
        final List<GameState> queryResult = (List<GameState>) newQuery.execute(keyToString, version);
        for (final GameState transaction : queryResult)
        {
            // finalResult.add((MoneyTransaction) DataStore.Add(transaction));
            finalResult.add(transaction);
        }

        Collections.sort(finalResult, NaturalSort);
        return finalResult;
    }

    public static GameState load(final Key key)
    {
        try
        {
            final GameState returnValue = DataStore.Get(GameState.class, key);
            return returnValue;
        }
        catch (final Exception e)
        {
            e.printStackTrace(System.out);
            return null;
        }
    }

    public static GameState load(final String sessionKeyString)
    {
        return GameState.load(KeyFactory.stringToKey(sessionKeyString));
    }

    @NotPersistent
    private Game _cachedState = null;

    @Persistent
    private int currentVersion;

    @Persistent
    @PrimaryKey
    @Id
    private Key id;

    @Persistent
    private String session = null;

    @Persistent
    private com.google.appengine.api.datastore.Blob state = null;

    @Persistent
    private final Date time = new Date();

    protected GameState()
    {
    }

    public GameState(final Blob state2, final int currentVersion2, final String session2)
    {
        state = state2;
        currentVersion = currentVersion2;
        session = session2;
        final String sKey = (session + "%" + currentVersion);
        id = KeyFactory.createKey(this.getClass().getSimpleName(), sKey);
        if (this != DataStore.Add(this)) throw new AssertionError();
    }

    public GameState(final GameSession fromSession) throws GameException
    {
        this(new Blob(Util.toBytes(fromSession.getLatestState())), fromSession.getLatestVersionNumber(), KeyFactory.keyToString(fromSession.getKey()));
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final GameState other = (GameState) obj;
        if (id == null)
        {
            if (other.id != null) return false;
        }
        else if (!id.equals(other.id)) return false;
        return true;
    }

    public String getId()
    {
        return KeyFactory.keyToString(id);
    }

    /**
     * @return the _id
     */
    @Override
    public Key getKey()
    {
        return id;
    }

    public Game getState(final GameSession _parent)
    {
        if (null != _cachedState) return _cachedState;
        if (null == state) return null;
        try
        {
            _cachedState = Util.fromBytes(state.getBytes());
            return _cachedState;
        }
        catch (Throwable e)
        {
            LOG.warning(Util.getFullString(e));
            return null;
        }
    }

    public int getVersionNumber()
    {
        return currentVersion;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

}
