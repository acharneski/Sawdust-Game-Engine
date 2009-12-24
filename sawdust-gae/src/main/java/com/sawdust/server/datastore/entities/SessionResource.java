package com.sawdust.server.datastore.entities;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.sawdust.engine.game.players.Player;
import com.sawdust.engine.service.Util;
import com.sawdust.engine.service.debug.SawdustSystemError;
import com.sawdust.server.datastore.DataObj;
import com.sawdust.server.datastore.DataStore;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class SessionResource extends DataObj
{
    @Persistent(name="gameSession")
    private Key gameSession;

    @Persistent
    private String className;

    @Persistent
    private Blob data;

    public SessionResource() {
        super();
    }

    public SessionResource(final GameSession session2, Serializable resource)
    {
        super(new KeyFactory.Builder((session2).getKey()).addChild(SessionResource.class.getSimpleName(), resource.getClass().getName()).getKey());
        gameSession = session2.getKey();
        className = resource.getClass().getName();
        data = new Blob(Util.toBytes(resource));
        if (this != DataStore.Cache(this)) throw new AssertionError();
    }

    public <T extends Serializable> T getData(Class<T> c)
    {
        T fromBytes = (T) Util.fromBytes(data.getBytes());
        return fromBytes;
    }

    /**
     * @return the gameSession
     */
    public GameSession getGameSession()
    {
        return DataStore.Get(GameSession.class, gameSession);
    }
}
