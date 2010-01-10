package com.sawdust.gae.datastore.entities;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.sawdust.engine.controller.exceptions.SawdustSystemError;
import com.sawdust.engine.model.players.Player;
import com.sawdust.gae.datastore.DataObj;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class PromoRedemption extends DataObj implements com.sawdust.engine.controller.entities.SessionMember
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
    private Promotion gameSession;

    @Persistent
    private Date lastUpdate = new Date();
    
    @Persistent
    private Date queueTime = new Date();

    @Persistent
    public int memberIndex = 0;

    @Persistent
    private MemberStatus memberStatus = MemberStatus.Playing;

    public PromoRedemption() {
        super();
    }

    public PromoRedemption(final Promotion session2, final Account account2)
    {
        super(new KeyFactory.Builder((session2).getKey()).addChild(PromoRedemption.class.getSimpleName(), account2.getUserId()).getKey());
        gameSession = session2;
        setAccountKey(account2.getKey());
        if (getAccountKey() == null) throw new SawdustSystemError("Null accountKey ID!");
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final PromoRedemption other = (PromoRedemption) obj;
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
    public com.sawdust.engine.controller.entities.Account getAccount()
    {
        return com.sawdust.gae.datastore.entities.Account.Load(getAccountKey());
    }

    /**
     * @return the accountKey
     */
    private Key getAccountKey()
    {
        return KeyFactory.stringToKey(encodedAccountKey);
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
    public Promotion getPromotion()
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
