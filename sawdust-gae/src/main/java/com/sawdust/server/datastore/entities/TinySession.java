package com.sawdust.server.datastore.entities;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.sawdust.engine.service.Util;
import com.sawdust.engine.service.data.GameSession;
import com.sawdust.server.datastore.DataObj;
import com.sawdust.server.datastore.DataStore;
import com.sawdust.server.datastore.SDDataEntity;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class TinySession extends SDDataEntity
{

    private static int TINYLENGTH = 5;

    public static TinySession load(final GameSession session)
    {
        final TinySession returnValue = new TinySession();
        returnValue.sessionId = session.getId();
        returnValue.id = KeyFactory.createKey(TinySession.class.getSimpleName(), returnValue.sessionId);
        final String md5 = Util.md5base64(returnValue.sessionId);
        for (int i = 0; i < md5.length() - TINYLENGTH; i++)
        {
            returnValue.tinyId = md5.substring(i, TINYLENGTH);
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
    @PrimaryKey
    @Id
    private Key id;

    @Persistent
    private String sessionId;

    @Persistent
    private String tinyId;

    private TinySession()
    {
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final TinySession other = (TinySession) obj;
        if (id == null)
        {
            if (other.id != null) return false;
        }
        else if (!id.equals(other.id)) return false;
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

    @Override
    public Key getKey()
    {
        return id;
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
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
