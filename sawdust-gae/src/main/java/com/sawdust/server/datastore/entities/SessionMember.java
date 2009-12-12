package com.sawdust.server.datastore.entities;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.sawdust.engine.game.players.Player;
import com.sawdust.engine.service.debug.SawdustSystemError;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class SessionMember implements com.sawdust.engine.service.data.SessionMember
{

    public enum MemberStatus
    {
    	Waiting,
        Playing, 
        Timeout, 
        Quit, 
    }

    @Persistent
    private String encodedAccountKey;

    @Persistent(name="gameSession")
    private GameSession gameSession;

    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY, primaryKey = "true")
    @Id
    @PrimaryKey
    private Key id;

    @Persistent
    private Date lastUpdate = new Date();
    
    @Persistent
    private Date queueTime = new Date();

    @Persistent
    public int memberIndex = 0;

    @Persistent
    private MemberStatus memberStatus = MemberStatus.Playing;

    public SessionMember() {}

    public SessionMember(final GameSession session2, final Account account2)
    {
        // super(account2.getEmail(), false);
        gameSession = session2;
        setAccountKey(account2.getKey());
        if (getAccountKey() == null) throw new SawdustSystemError("Null accountKey ID!");
        id = new KeyFactory.Builder((session2).getKey()).addChild(SessionMember.class.getSimpleName(), account2.getUserId()).getKey();
        // DataStore.Add(this);
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final SessionMember other = (SessionMember) obj;
        if (encodedAccountKey == null)
        {
            if (other.encodedAccountKey != null) return false;
        }
        else if (!encodedAccountKey.equals(other.encodedAccountKey)) return false;
        return true;
    }

    //
    // @Override
    // public com.sawdust.engine.data.Account loadAccount()
    // {
    // return this.getAccount();
    // }

    /**
     * @return the accountKey
     */
    public com.sawdust.engine.service.data.Account getAccount()
    {
        return com.sawdust.server.datastore.entities.Account.Load(getAccountKey());
    }

    /**
     * @return the accountKey
     */
    private Key getAccountKey()
    {
        return KeyFactory.stringToKey(encodedAccountKey);
    }

    /**
     * @return the _id
     */
    public Key getId()
    {
        return id;
    }

    public Date getLastUpdate()
    {
        return lastUpdate;
    }

    public MemberStatus getMemberStatus()
    {
        return memberStatus;
    }

    public Player getPlayer()
    {
        final Account account = (Account) getAccount();
        return new AccountPlayer(account);
    }

    /**
     * @return the gameSession
     */
    public GameSession getGameSession()
    {
        return gameSession;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((encodedAccountKey == null) ? 0 : encodedAccountKey.hashCode());
        return result;
    }

    /**
     * @param accountKey
     *            the accountKey to set
     */
    private void setAccountKey(final Key accountKey)
    {
        encodedAccountKey = KeyFactory.keyToString(accountKey);
    }

    protected void setId(final Key pid)
    {
        id = pid;
    }

    public void setLastUpdate()
    {
        lastUpdate = new Date();
    }

    public void setMemberStatus(final MemberStatus pmemberStatus)
    {
    	if(pmemberStatus == MemberStatus.Waiting && memberStatus != MemberStatus.Waiting)
    	{
    		queueTime = new Date();
    	}
        memberStatus = pmemberStatus;
    }

	protected void setQueueTime(Date pqueueTime) {
		this.queueTime = pqueueTime;
	}

	protected Date getQueueTime() {
		return queueTime;
	}
}
