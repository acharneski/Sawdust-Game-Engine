package com.sawdust.gae.datastore.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Serialized;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.sawdust.engine.controller.PromotionConfig;
import com.sawdust.engine.controller.Util;
import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.controller.exceptions.GameLogicException;
import com.sawdust.engine.model.Bank;
import com.sawdust.engine.model.players.Player;
import com.sawdust.engine.view.game.ActivityEvent;
import com.sawdust.gae.datastore.DataObj;
import com.sawdust.gae.datastore.DataStore;
import com.sawdust.gae.logic.UserLogic;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class Account extends DataObj implements com.sawdust.engine.controller.entities.Account
{
    public enum InterfacePreference
    {
        Facebook, Mobile, Standard
    }

    private static Key generateKey(String userId)
    {
        return (KeyFactory.createKey(Account.class.getSimpleName(), userId));
    }

    @Persistent
    private Blob logicProvider;

    private static final Logger LOG = Logger.getLogger(Account.class.getName());

    static final int MIN_CREDITS = 1;

    private static String emailToName(final String str)
    {
        final int idx = str.indexOf("@");
        if (0 > idx) return str;
        return str.substring(0, idx);
    }

    public static Account Load(final Key accountKey)
    {
        final Account myData = DataStore.Get(Account.class, accountKey);
        if (null != myData)
        {
            if (null == myData.sessions)
            {
                myData.sessions = new HashSet<Key>();
            }
        }
        return myData;
    }

    public static Account Load(final String userId)
    {
        Account myData = LoadIfExists(userId);
        if (null == myData)
        {
            LOG.info("Create Account: " + userId);
            myData = new Account(userId);
        }
        return myData;
    }

    public static Account LoadIfExists(final String userId)
    {
        Account myData = DataStore.Get(Account.class, generateKey(userId));
        if (null == myData)
        {
            final PersistenceManager entityManager = DataStore.create();
            try
            {
                final Query newQuery = entityManager.newQuery(Account.class);
                newQuery.setFilter("email == emailParam");
                newQuery.setUnique(true);
                newQuery.declareParameters("String emailParam");
                myData = (Account) newQuery.execute(userId);
            }
            catch (final Throwable e)
            {
                LOG.warning(Util.getFullString(e));
                myData = null;
            }
            finally
            {
                if (null == myData)
                {
                    entityManager.close();
                }
                else
                {
                    myData.setEntityManager(entityManager);
                    if (null == myData.sessions)
                    {
                        myData.sessions = new HashSet<Key>();
                    }
                    return (Account) DataStore.Add(myData);
                }
            }
        }
        return myData;
    }

    @Persistent
    private Key bankKey = null;

    @Persistent
    private String displayName;

    @Persistent
    private String email;

    @Persistent
    private InterfacePreference interfacePreference = InterfacePreference.Standard;

    @Persistent
    public int isAdmin = 0;

    @Persistent
    private HashSet<Key> sessions = new HashSet<Key>();

    @Persistent
    @Serialized
    private HashMap<Class, Key> resources = new HashMap<Class, Key>();

    protected Account()
    {
        super();
    }

    public Account(final String v)
    {
        super(generateKey(v));
        email = v;
        displayName = emailToName(v);
        if (this != DataStore.Add(this)) throw new AssertionError();
    }

    public void addSession(final GameSession session2)
    {
        final Key id2 = session2.getKey();
        if (null == id2)
        {
            LOG.warning("Warning: null key");
        }
        super.update();
        sessions.add(id2);
    }

    @Override
    public com.sawdust.engine.controller.entities.Promotion doAwardPromotion(PromotionConfig p) throws GameException
    {
        Promotion promo = Promotion.load(this, p);
        return promo;
    }

    @Override
    public void doLogActivity(ActivityEvent event)
    {
        UserLogic logic = getLogic();
        if(null != logic) 
        {
            LOG.info(String.format("SUPRESS Facebook event: %s", event.event));
            try
            {
                //logic.publishActivity(event.event);
            }
            catch (Exception e)
            {
                LOG.warning(Util.getFullString(e));
            }
        }
        new ActivityEventRecord(getAccount(),event);
    }

    public void doRemoveSession(final com.sawdust.engine.controller.entities.GameSession gameSession)
    {
        final Key id2 = ((GameSession) gameSession).getKey();
        if (null == id2)
        {
            LOG.warning("Warning: null key");
        }
        else
        {
            super.update();
            sessions.remove(id2);
        }
    }

    public void doWithdraw(int amount, final Bank depositTarget, final String description) throws GameException
    {
        final int finalAmt = getBalance() - amount;
        super.update();
        if (finalAmt < 0)
        {
            String errMsg = String.format("Insufficient credits. Current Balance = %d; Required = %d", getBalance(), amount);
            LOG.info(errMsg);
            throw new GameLogicException(errMsg);
        }
        if (finalAmt < MIN_CREDITS)
        {
            String msg = String.format("Overdraft Protection");
            MoneyTransaction.Transfer(getAccount(), null, finalAmt - MIN_CREDITS, msg);
        }
        if (null != depositTarget)
        {
            if (depositTarget instanceof Account)
            {
                LOG.fine(String.format("Withdrawl: %d (%s) from account %s to account %s", amount, description, ((Account) depositTarget)
                        .getName(), getName()));
                MoneyAccount account = ((Account) depositTarget).getAccount();
                MoneyTransaction.Transfer(getAccount(), account, amount, description);
            }
            else if (depositTarget instanceof GameSession)
            {
                LOG.fine(String.format("Withdrawl: %d (%s) from session %s to account %s", amount, description,
                        ((GameSession) depositTarget).getName(), getName()));
                MoneyAccount account = ((GameSession) depositTarget).getBankAccount();
                MoneyTransaction.Transfer(getAccount(), account, amount, description);
            }
            else
            {
                LOG.warning(String
                        .format("Withdrawl: %d (%s) from UNKNOWN %s to account %s", amount, description, depositTarget, getName()));
                depositTarget.doWithdraw(-amount, null, description);
            }
        }
        else
        {
            LOG.fine(String.format("Withdrawl: %d (%s) from system to %s", amount, description, getName()));
            MoneyTransaction.Transfer(getAccount(), null, amount, description);
        }
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final Account other = (Account) obj;
        if (email == null)
        {
            if (other.email != null) return false;
        }
        else if (!email.equals(other.email)) return false;
        return true;
    }

    public MoneyAccount getAccount()
    {
        Key bankKey2 = getBankKey();
        MoneyAccount moneyAccount = null;
        if (null != bankKey2)
        {
            moneyAccount = MoneyAccount.Load(bankKey2);
            if (null != moneyAccount)
            {
                moneyAccount.setDisplayName(displayName);
                return moneyAccount;
            }
            else
            {
                LOG.warning("Bad Key: " + bankKey2.toString());
            }
        }
        if (null == moneyAccount)
        {
            moneyAccount = MoneyAccount.Load(email, getKey());
            moneyAccount.setDisplayName(displayName);
            setBankKey(moneyAccount.getKey());
        }
        return moneyAccount;
    }

    /**
     * @return the bank
     */
    public int getBalance()
    {
        final int currentBalence = getAccount().currentBalence;
        if (currentBalence < MIN_CREDITS) return MIN_CREDITS;
        return currentBalence;
    }

    public Key getBankKey()
    {
        return bankKey;
    }

    public InterfacePreference getInterfacePreference()
    {
        if (null == interfacePreference) return InterfacePreference.Standard;
        return interfacePreference;
    }

    public <T extends UserLogic> T getLogic()
    {
        if(null == this.logicProvider) return null;
        T fromBytes = null;
        try
        {
            
            fromBytes = (T) Util.fromBytes(this.logicProvider.getBytes());
            LOG.fine(String.format("Get UserLogic: %d bytes: %s", this.logicProvider.getBytes().length, fromBytes.toString()));
        }
        catch (Exception e)
        {
            LOG.warning(String.format("Get UserLogic: %s", Util.getFullString(e)));
        }
        return fromBytes;
    }

    public String getName()
    {
        if (null == displayName)
        {
            displayName = emailToName(email);
        }
        return displayName;
    }

    public Player getPlayer()
    {
        return new AccountPlayer(this);
    }

    @Override
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

    public GameSession getSession(final String sessionID) throws GameException
    {
        GameSession findResult = null;
        if ((null != sessionID) && !sessionID.isEmpty())
        {
            final Key sessionKey = KeyFactory.stringToKey(sessionID);
            findResult = GameSession.load(sessionKey, getPlayer());
        }
        if (null != findResult)
            return findResult;
        else
            return null;
    }

    public Set<Key> getSessionKeys()
    {
        return sessions;
    }

    /**
     * @return the email
     */
    public String getUserId()
    {
        return email;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        return result;
    }

    public boolean isAdmin()
    {
        return (isAdmin == 1);
    }
    
    @Override
    public boolean isValid()
    {
        if(!getUserId().contains("@")) return false;
        if(getUserId().endsWith("@guest.null"))
        {
            long t = new Date().getTime() - getUpdated().getTime();
            if(t > (1000*60*60*24*1)) return false;
        }
        return super.isValid();
    }

    public void setAdmin(final boolean pIsAdmin)
    {
        isAdmin = (pIsAdmin ? 1 : 0);
    }

    public void setBankKey(final Key pbankKey)
    {
        bankKey = pbankKey;
    }

    public void setInterfacePreference(final InterfacePreference pinterfacePreference)
    {
        interfacePreference = pinterfacePreference;
    }

    public void setLogic(UserLogic userLogic)
    {
        this.logicProvider = new Blob(Util.toBytes(userLogic));
    }

    public void setName(final String pdisplayName)
    {
        displayName = pdisplayName;
    }

    @Override
    public <T extends Serializable> void setResource(Class<T> c, T markovChain)
    {
        resources.put(c, new SessionResource(this, markovChain).getKey());
    }
}
