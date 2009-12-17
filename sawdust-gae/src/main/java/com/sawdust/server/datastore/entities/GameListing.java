package com.sawdust.server.datastore.entities;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.sawdust.engine.game.players.Player;
import com.sawdust.engine.service.debug.GameException;
import com.sawdust.engine.service.debug.InputException;
import com.sawdust.engine.service.debug.SawdustSystemError;
import com.sawdust.server.datastore.DataObj;
import com.sawdust.server.datastore.DataStore;
import com.sawdust.server.datastore.SDDataEntity;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class GameListing extends SDDataEntity
{
    public enum InviteSearchParam
    {
        Game, MaxBid, MinBid
    }

    private static final Logger LOG = Logger.getLogger(GameListing.class.getName());

    public static GameListing get(final HashMap<InviteSearchParam, String> searchParameters) throws com.sawdust.engine.common.GameException
    {
        return list(searchParameters, 1).get(0);
    }

    public static ArrayList<GameListing> list(final HashMap<InviteSearchParam, String> searchParameters, final int numberToList) throws com.sawdust.engine.common.GameException
    {
        final PersistenceManager entityManager = DataStore.create();
        final Query newQuery = entityManager.newQuery(GameListing.class);
        boolean prefixBid = false;
        if (searchParameters.containsKey(InviteSearchParam.MinBid))
        {
            newQuery.setFilter(String.format("ante > %d", Integer.parseInt(searchParameters.get(InviteSearchParam.MinBid))));
            prefixBid = true;
        }
        if (searchParameters.containsKey(InviteSearchParam.MaxBid))
        {
            newQuery.setFilter(String.format("ante < %d", Integer.parseInt(searchParameters.get(InviteSearchParam.MaxBid))));
            prefixBid = true;
        }
        if (searchParameters.containsKey(InviteSearchParam.Game))
        {
            final String gameName = searchParameters.get(InviteSearchParam.Game);
            if (gameName.contains("\"")) throw new InputException("Bad Game Name!");
            newQuery.setFilter(String.format("game == \"%s\"", gameName));
        }
        String ordering = "timeCreated asc";
        if (prefixBid)
        {
            ordering = "ante asc, " + ordering;
        }
        newQuery.setOrdering(ordering);

        final ArrayList<GameListing> gameListings = new ArrayList<GameListing>();
        try
        {
            final List<GameListing> results = (List<GameListing>) newQuery.execute();
            for (final GameListing gameListing : results)
            {
                gameListing.setEntityManager(entityManager);
                final GameSession gameSession = gameListing.getSession();
                int numberOfMembers = 0;
                if(null != gameSession)
                {
                    gameSession.updateStatus();
                    if(gameSession.getSessionStatus() == com.sawdust.engine.service.data.GameSession.SessionStatus.Inviting)
                    {
                        numberOfMembers = gameSession.getReadyPlayers();
                    }
                }
                if (0 == numberOfMembers )
                {
                    LOG.info("Removing old invite record: " + gameListing.key);
                    gameListing.delete();
                    continue;
                }
                else
                {
                    gameListing.em = entityManager;
                    gameListings.add(gameListing);
                }
            }
        }
        catch (final JDOUserException e)
        {
            throw new SawdustSystemError(e);
        }
        return gameListings;
    }

    public static GameListing load(final Key k)
    {
        final GameListing findObj = DataStore.Get(GameListing.class, k);
        return findObj;
    }

    @Persistent
    private int ante = 0;

    protected PersistenceManager em = null;

    @Persistent
    private String game = "NULL";

    @Persistent
    @PrimaryKey
    @Id
    private Key key;

    @Persistent
    private Key sessionKey;

    @Persistent
    private Date timeCreated = new Date();;

    protected GameListing() {}
    
    public GameListing(final GameSession gameSession, final Player user)
    {
        sessionKey = gameSession.getKey();
        ante = gameSession.getAnte();
        game = gameSession.getGame();

        final String sKey = (user.getUserId() + "%" + DateFormat.getDateTimeInstance().format(new Date()));
        key = KeyFactory.createKey(GameListing.class.getSimpleName(), sKey);
        if (this != DataStore.Add(this)) throw new AssertionError();
    }

    /**
     * @return the ante
     */
    public int getAnte()
    {
        return ante;
    }

    /**
     * @return the game
     */
    public String getGame()
    {
        return game;
    }

    @Override
    public Key getKey()
    {
        return key;
    }

    public GameSession getSession()
    {
        GameSession load = GameSession.load(sessionKey, null);
        if(null == load)
        {
            delete();
        }
        else if(null == load.getLatestState())
        {
            LOG.warning("Removing defunc session");
            load.delete();
            delete();
            DataStore.Save();
            return null;
        }
        return load;
    }

    private void delete()
    {
        LOG.info("Deleting GameListting");
        PersistenceManager entityManager = getEntityManager();
        if(null == entityManager) 
        {
            entityManager = DataStore.create();
            entityManager.makePersistent(this);
        }
        entityManager.deletePersistent(this);
        entityManager.close();
    }

    public Key getSessionKey()
    {
        return sessionKey;
    }

    public Date getTimeCreated()
    {
        return timeCreated;
    }

    /**
     * @param pante
     *            the ante to set
     */
    public void setAnte(final int pante)
    {
        ante = pante;
    }

    /**
     * @param pgame
     *            the game to set
     */
    public void setGame(final String pgame)
    {
        game = pgame;
    }

    private void setKey(Key pkey)
    {
        this.key = pkey;
    }

    private void setSessionKey(Key psessionKey)
    {
        this.sessionKey = psessionKey;
    }

    private void setTimeCreated(Date ptimeCreated)
    {
        this.timeCreated = ptimeCreated;
    }
}
