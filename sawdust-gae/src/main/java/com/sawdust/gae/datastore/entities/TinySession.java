package com.sawdust.gae.datastore.entities;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.sawdust.engine.controller.Util;
import com.sawdust.engine.controller.entities.GameSession;
import com.sawdust.gae.datastore.DataObj;
import com.sawdust.gae.datastore.DataStore;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class TinySession extends DataObj
{

    private static int TINYLENGTH = 5;

    public static TinySession load(final GameSession session)
    {
        final TinySession returnValue = new TinySession(session);
        final String md5 = Util.md5hex(returnValue.sessionId);
        for (int i = 3; i < md5.length(); i++)
        {
            returnValue.tinyId = md5.substring(0, i);
            final TinySession existing = load(returnValue.tinyId);
            if (null != existing)
            {
                if (existing.sessionId.equals(returnValue.sessionId)) return returnValue;
            }
            else
            {
                break;
            }
        }
        return (TinySession) DataStore.Add(returnValue);
    }

    public static TinySession load(final Key key)
    {
        final TinySession returnValue = DataStore.Get(TinySession.class, key);
        return returnValue;
    }

    public static TinySession load(final String tinyId)
    {
        TinySession myData = null;
        final PersistenceManager entityManager = DataStore.create();
        try
        {
            final Query newQuery = entityManager.newQuery(TinySession.class);
            newQuery.setFilter("tinyId == tinyIdParam");
            newQuery.setUnique(true);
            newQuery.declareParameters("String tinyIdParam");
            myData = (TinySession) newQuery.execute(tinyId);
        }
        catch (final Throwable e)
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
    private String sessionId;

    @Persistent
    private String tinyId;

    private TinySession()
    {
        super();
    }

    public TinySession(GameSession session)
    {
        super(KeyFactory.createKey(TinySession.class.getSimpleName(), session.getStringId()));
        sessionId = session.getStringId();
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final TinySession other = (TinySession) obj;
        if (!super.equals(other)) return false;
        if (sessionId == null)
        {
            if (other.sessionId != null) return false;
        }
        else if (!sessionId.equals(other.sessionId)) return false;
        if (tinyId == null)
        {
            if (other.tinyId != null) return false;
        }
        else if (!tinyId.equals(other.tinyId)) return false;
        return true;
    }

    public String getSessionId()
    {
        return sessionId;
    }

    public String getTinyId()
    {
        return tinyId;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
        result = prime * result + ((tinyId == null) ? 0 : tinyId.hashCode());
        return result;
    }

    public void setSessionId(final String psessionId)
    {
        sessionId = psessionId;
    }

    public void setTinyId(final String ptinyId)
    {
        tinyId = ptinyId;
    }
}
