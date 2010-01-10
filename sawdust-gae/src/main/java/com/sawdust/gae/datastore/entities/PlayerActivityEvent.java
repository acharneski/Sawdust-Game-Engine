package com.sawdust.gae.datastore.entities;

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
import com.sawdust.engine.controller.Util;
import com.sawdust.engine.controller.entities.GameSession.SessionStatus;
import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.basetypes.BaseGame;
import com.sawdust.engine.model.basetypes.GameState;
import com.sawdust.gae.datastore.DataObj;
import com.sawdust.gae.datastore.DataStore;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class PlayerActivityEvent extends DataObj
{
    private static final Logger LOG = Logger.getLogger(PlayerActivityEvent.class.getName());

    public static final Comparator<PlayerActivityEvent> NaturalSort = new Comparator<PlayerActivityEvent>()
    {
        public int compare(final PlayerActivityEvent o1, final PlayerActivityEvent o2)
        {
            return -o1.time.compareTo(o2.time);
        }
    };

    public static List<PlayerActivityEvent> getStatesSince(final GameSession psession, final int version)
    {
        final PersistenceManager entityManager = DataStore.create();
        final List<PlayerActivityEvent> finalResult = new ArrayList<PlayerActivityEvent>();

        final Query newQuery = entityManager.newQuery(PlayerActivityEvent.class);
        newQuery.setFilter("currentVersion > _currentVersion && session == _session");
        newQuery.declareParameters("java.lang.String _session, int _currentVersion");
        newQuery.setOrdering("currentVersion desc");
        final String keyToString = KeyFactory.keyToString(psession.getKey());
        final List<PlayerActivityEvent> queryResult = (List<PlayerActivityEvent>) newQuery.execute(keyToString, version);
        for (final PlayerActivityEvent transaction : queryResult)
        {
            // finalResult.add((MoneyTransaction) DataStore.Add(transaction));
            finalResult.add(transaction);
        }

        Collections.sort(finalResult, NaturalSort);
        return finalResult;
    }

    public static PlayerActivityEvent load(final Key key)
    {
        try
        {
            final PlayerActivityEvent returnValue = DataStore.Get(PlayerActivityEvent.class, key);
            return returnValue;
        }
        catch (final Throwable e)
        {
            e.printStackTrace(System.out);
            return null;
        }
    }

    public static PlayerActivityEvent load(final String sessionKeyString)
    {
        return PlayerActivityEvent.load(KeyFactory.stringToKey(sessionKeyString));
    }

    @NotPersistent
    private GameState _cachedState = null;

    @Persistent
    private int currentVersion;

    @Persistent
    private String session = null;

    @Persistent
    private com.google.appengine.api.datastore.Blob state = null;

    @Persistent
    private final Date time = new Date();

    protected PlayerActivityEvent()
    {
        super();
    }

    public PlayerActivityEvent(final Blob state2, final int currentVersion2, final String session2)
    {
        super(KeyFactory.createKey(PlayerActivityEvent.class.getSimpleName(), (session2 + "%" + currentVersion2)));
        state = state2;
        currentVersion = currentVersion2;
        session = session2;
        if (this != DataStore.Add(this)) throw new AssertionError();
    }

    public PlayerActivityEvent(final GameSession fromSession) throws GameException
    {
        this(new Blob(Util.toBytes(fromSession.getState())), fromSession.getLatestVersionNumber(), KeyFactory.keyToString(fromSession.getKey()));
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final PlayerActivityEvent other = (PlayerActivityEvent) obj;
        if (!super.equals(other)) return false;
        return true;
    }

    public String getId()
    {
        return KeyFactory.keyToString(getKey());
    }

    public GameState getState(final GameSession _parent)
    {
        if (null != _cachedState) return _cachedState;
        if (null == state) return null;
        try
        {
            _cachedState = (GameState) Util.fromBytes(state.getBytes());
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
    public boolean isValid()
    {
        try
        {
            if(null == session) return false;
            if(session.isEmpty()) return false;
            GameSession s = GameSession.load(session, null);
            if(null == s) return false;
            if(s.sessionStatus == SessionStatus.Closed) return false;
            if(s.getLatestVersionNumber() > currentVersion) return false;
        }
        catch (Throwable e)
        {
            return false;
        }
        return super.isValid();
    }
}
