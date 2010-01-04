package com.sawdust.server.datastore.entities;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Serialized;
import javax.persistence.OrderBy;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.sawdust.engine.common.game.Message;
import com.sawdust.engine.game.players.Player;
import com.sawdust.engine.service.PromotionConfig;
import com.sawdust.engine.service.Util;
import com.sawdust.engine.service.debug.GameException;
import com.sawdust.engine.service.debug.GameLogicException;
import com.sawdust.server.datastore.DataObj;
import com.sawdust.server.datastore.DataStore;
import com.sawdust.server.datastore.entities.Account.InterfacePreference;
import com.sawdust.server.datastore.entities.PromoRedemption.MemberStatus;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class Promotion extends DataObj implements com.sawdust.engine.service.data.Promotion
{
    private static final Logger LOG = Logger.getLogger(Promotion.class.getName());

    public static Promotion load(final Key key)
    {
        try
        {
            Promotion returnValue = DataStore.GetCache(Promotion.class, key);
            if (null == returnValue)
            {
                returnValue = DataStore.Get(Promotion.class, key);
                if (null == returnValue)
                {
                    LOG.info("Key not found");
                    return null;
                }

                if (null == returnValue.members)
                {
                    returnValue.members = new ArrayList<PromoRedemption>();
                }
                DataStore.Cache(returnValue);
            }

            return returnValue;
        }
        catch (Throwable e)
        {
            e.printStackTrace(System.out);
            return null;
        }
    }

    @Persistent
    private Key account = null;

    @Persistent
    private int value = 0;

    @Persistent(mappedBy = "gameSession")
    @OrderBy(value = "memberIndex")
    private ArrayList<PromoRedemption> members = new ArrayList<PromoRedemption>();

    @Persistent
    @Serialized
    private HashMap<Class, Key> resources = new HashMap<Class, Key>();

    @Persistent
    private String name = "No Title";

    @Persistent
    private int _maxRedemption;

    @Persistent
    private String _url;

    @Persistent
    private Key _owner;

    @Persistent
    private String _message;

    @Persistent
    private String _attachment;

    protected Promotion()
    {
        super();
    }

    public static Promotion load(String url) throws GameException
    {
        Promotion myData = null;
        final PersistenceManager entityManager = DataStore.create();
        try
        {
            final Query newQuery = entityManager.newQuery(Promotion.class);
            newQuery.setFilter("_url == url");
            newQuery.setUnique(true);
            newQuery.declareParameters("String url");
            myData = (Promotion) newQuery.execute(url);
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

    public static Promotion load(final Account paccount, PromotionConfig p) throws GameException
    {
        Promotion self = new Promotion(paccount, p);
        final String md5 = Util.md5hex(KeyFactory.keyToString(self.getKey()));
        for (int i = 3; i < md5.length(); i++)
        {
            self._url = md5.substring(0, i);
            final Promotion existing = load(self._url);
            if (null != existing)
            {
                if (existing.getKey().equals(self.getKey())) 
                {
                    return existing;
                }
            }
            else
            {
                break;
            }
        }
        
        if (self != DataStore.Add(self)) throw new AssertionError();
        return self;
    }


    protected Promotion(final Account paccount, PromotionConfig p) throws GameException
    {
        super(getKey(paccount));
        name = p._name;
        _owner = paccount.getKey();
        _maxRedemption = p._maxRedemption;
        _attachment = p._attachment;
        this.value = p._value;
        this._message = p._msg;
        _attachment = _attachment.replaceAll("PROMOLINK", getFullUrl());
        
        final PromoRedemption sessionMember = new PromoRedemption(this, (com.sawdust.server.datastore.entities.Account) paccount);
        sessionMember.memberIndex = members.size();
        sessionMember.setMemberStatus(MemberStatus.Waiting);
        LOG.info(String.format("Adding Owner to Promotion: %s", sessionMember.getAccount().getUserId()));
        members.add(sessionMember);
    }

    private static Key getKey(final Account paccount)
    {
        String timeString = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG).format(new Date());
        String accountString = paccount.getKey().toString();
        String randomNumber = Integer.toString((int) (Math.random() * 50));
        String keyString = String.format("User=%s<br/>Time=%s<br/>Rand=%s", accountString, timeString, randomNumber);
        return KeyFactory.createKey(Promotion.class.getSimpleName(), keyString);
    }

    public void addAccount(final com.sawdust.engine.service.data.Account _account) throws GameLogicException
    {
        if(_maxRedemption <= members.size()) throw new GameLogicException(String.format("This promotion has already been redeemed %d times", members.size()));
        for (final PromoRedemption member : members)
        {
            if (member.getAccount().getUserId().equals(_account.getUserId())) 
            {
                throw new GameLogicException("You have already redeemed this gift");
            }
        }
        
        final PromoRedemption sessionMember = new PromoRedemption(this, (com.sawdust.server.datastore.entities.Account) _account);
        sessionMember.memberIndex = members.size();
        sessionMember.setMemberStatus(MemberStatus.Waiting);
        LOG.info(String.format("Adding Member to Promotion: %s", sessionMember.getAccount().getUserId()));
        members.add(sessionMember);

        LOG.fine(String.format("Withdrawl: %d (%s) from account %s to session %s", getValue(), getName(), ((Account) _account).getName(),
                getName()));
        MoneyTransaction.Transfer(getAccount(), ((Account) _account).getAccount(), getValue(), getName());
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final Promotion other = (Promotion) obj;
        if (!super.equals(other)) return false;
        return true;
    }

    public PromoRedemption findMember(final Player email)
    {
        for (final PromoRedemption member : members)
        {
            final Player memberEmail = member.getPlayer();
            if (memberEmail.equals(email)) return member;
        }
        return null;
    }

    public MoneyAccount getAccount()
    {
        if (null != account) return MoneyAccount.Load(account);
        final MoneyAccount moneyAccount = new MoneyAccount(getId(), getKey());
        moneyAccount.setDisplayName(String.format("%s (Promotion)", getName()));
        account = moneyAccount.getKey();
        return moneyAccount;
    }

    /**
     * @return the ante
     */
    public int getValue()
    {
        return value;
    }

    public int getBalance()
    {
        return getAccount().getBalance();
    }

    public String getHtml(final InterfacePreference xface)
    {
        final StringBuilder sb = new StringBuilder();
        sb.append("<div class='sdge-game-listing'>");
        sb.append(String.format("<strong>Name: <a href='/c/%s'>%s</a></strong><br/>", _url, name));
        sb.append(String.format("Value: %d<br/>", value));
        sb.append(String.format("Times redeemed: %d<br/>", (null == members) ? 0 : members.size()));
        sb.append("</div>");
        return sb.toString();
    }

    public String getId()
    {
        return KeyFactory.keyToString(getKey());
    }

    /**
     * @return the members
     */
    public Collection<Player> getMembers()
    {
        final ArrayList<Player> returnValue = new ArrayList<Player>();
        for (final PromoRedemption o : members)
        {
            returnValue.add(o.getPlayer());
        }
        return returnValue;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    public PromoRedemption getOwner()
    {
        return DataStore.Get(PromoRedemption.class, _owner);
    }

    public boolean isMember(final String email)
    {
        for (final PromoRedemption member : members)
        {
            final String memberEmail = member.getAccount().getUserId();
            if (memberEmail.equals(email)) return true;
        }
        return false;
    }

    public PromoRedemption findMember(final String email)
    {
        for (PromoRedemption s : members)
        {
            if (email.equals(s.getAccount().getUserId()))
            {
                return s;
            }
        }
        return null;
    }

    public void setValue(final int newValue) throws GameException
    {
        if (0 > newValue) throw new GameLogicException(String.format("Cannot set the value to %d", newValue));
        value = newValue;
    }

    public void setName(final String pname) throws GameException
    {
        name = pname;
    }

    public <T extends Serializable> T getResource(Class<T> c)
    {
        if (!resources.containsKey(c)) return null;
        Key key = resources.get(c);
        SessionResource get = DataStore.Get(SessionResource.class, key);
        if (null == get)
        {
            LOG.warning("null == get");
            return null;
        }
        return get.getData(c);
    }

    public <T extends Serializable> void setResource(Class<T> c, T markovChain)
    {
        resources.put(c, new SessionResource(this, markovChain).getKey());
    }

    @Override
    public String getFullUrl()
    {
        return "http://sawdust-games.appspot.com/r/" + _url;
    }

    @Override
    public String getUrl()
    {
        return _url;
    }

    public void setUrl(String url)
    {
        _url = url;
    }

    @Override
    public Message getMessage()
    {
        Message msg = new Message(_message).setSocialActivity(true);
        msg.fbAttachment = _attachment;
        return msg;
    }
}
