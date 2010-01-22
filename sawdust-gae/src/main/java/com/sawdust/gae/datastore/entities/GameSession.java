package com.sawdust.gae.datastore.entities;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Serialized;
import javax.persistence.OrderBy;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.sawdust.engine.controller.Util;
import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.controller.exceptions.GameLogicException;
import com.sawdust.engine.model.Bank;
import com.sawdust.engine.model.basetypes.GameState;
import com.sawdust.engine.model.basetypes.MultiPlayerGame;
import com.sawdust.engine.model.players.Agent;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.players.Player;
import com.sawdust.engine.view.config.GameConfig;
import com.sawdust.gae.datastore.DataObj;
import com.sawdust.gae.datastore.DataStore;
import com.sawdust.gae.datastore.entities.Account.InterfacePreference;
import com.sawdust.gae.datastore.entities.SessionMember.MemberStatus;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class GameSession extends DataObj implements com.sawdust.engine.controller.entities.GameSession
{
    private static final Logger LOG = Logger.getLogger(GameSession.class.getName());

    @Persistent
    private int playerTimeout = 90;

    public static GameSession load(final Key key, Player whosAsking)
    {
        try
        {
            GameSession returnValue = DataStore.GetCache(GameSession.class, key);
            if (null == returnValue)
            {
                returnValue = DataStore.Get(GameSession.class, key);
                if (null == returnValue)
                {
                    LOG.info("Key not found");
                    return null;
                }

                if (null == returnValue.members)
                {
                    returnValue.members = new ArrayList<SessionMember>();
                }
                DataStore.Cache(returnValue);
                returnValue.doUpdateStatus();
            }

            return returnValue;
        }
        catch (Throwable e)
        {
            e.printStackTrace(System.out);
            return null;
        }
    }

    public static GameSession load(final String sessionKeyString, Player whosasking)
    {
        return GameSession.load(KeyFactory.stringToKey(sessionKeyString), whosasking);
    }

    @NotPersistent
    private GameState _cachedState = null;

    @NotPersistent
    private boolean _dirtyGame = false;

    @Persistent
    private Key account = null;

    @Persistent
    private int ante = 0;

    @Persistent
    private ArrayList<String> aiList = new ArrayList<String>();

    @Persistent
    public Key currentState = null;

    @Persistent
    private int currentVersion;

    @Persistent
    private String game = "NULL";

    @Persistent
    private Date lastGameUpdate = new Date();

    @NotPersistent
    private final transient ArrayList<GameStateEntity> localStates = new ArrayList<GameStateEntity>();

    @Persistent(mappedBy = "gameSession")
    @OrderBy(value = "memberIndex")
    private ArrayList<SessionMember> members = new ArrayList<SessionMember>();

    @Persistent
    @Serialized
    private HashMap<Class, Key> resources = new HashMap<Class, Key>();

    @Persistent
    private int moveTimeout = 0;

    @Persistent
    private String name = "No Title";

    @Persistent
    public SessionStatus sessionStatus = SessionStatus.Initializing;

    @Persistent
    private int timeOffset;

    @Persistent
    private int requiredPlayers = Integer.MAX_VALUE;

    @Persistent
    private String _url;

    private boolean _agentEnabled = true;

    protected GameSession()
    {
        super();
    }

    public GameSession(final Account paccount) throws GameException
    {
        super(getKey(paccount));
        if (this != DataStore.Add(this)) throw new AssertionError();
    }

    private static Key getKey(final Account paccount)
    {
        String timeString = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG).format(new Date());
        String accountString = paccount.getKey().toString();
        String randomNumber = Integer.toString((int) (Math.random() * 50));
        String keyString = String.format("User=%s<br/>Time=%s<br/>Rand=%s", accountString, timeString, randomNumber);
        return KeyFactory.createKey(GameSession.class.getSimpleName(), keyString);
    }

    public void addPlayer(final Participant p) throws GameException
    {
        if ((sessionStatus != SessionStatus.Inviting) && (sessionStatus != SessionStatus.Initializing))
            throw new GameLogicException("This session is not in the invite mode!");

        if (p instanceof Player)
        {
            Player player = (Player) p;
            final com.sawdust.engine.controller.entities.Account _account = player.getAccount();
            if (getUnitWager() > _account.getBalance()) throw new GameLogicException("The game's ante is too high!");
            ((Account) _account).addSession(this);

            final SessionMember sessionMember = new SessionMember(this, (com.sawdust.gae.datastore.entities.Account) _account);
            sessionMember.memberIndex = members.size();
            sessionMember.setMemberStatus(MemberStatus.Waiting);
            LOG.info(String.format("Adding Member to Session: %s", sessionMember.getAccount().getUserId()));
            members.add(sessionMember);
        }
        else if (p instanceof Agent<?>)
        {
            addAgent(p.getId());
        }

        final GameState _game = getState();
        if (null != _game)
        {
            _game.doAddPlayer(p);
            _game.doSaveState();
            doUpdateStatus();
        }
    }

    public void doUnitWager() throws GameException
    {
        final ArrayList<com.sawdust.engine.controller.entities.Account> toSave = new ArrayList<com.sawdust.engine.controller.entities.Account>();
        for (final Player member : getPlayers())
        {
            final com.sawdust.engine.controller.entities.Account laccount = member.getAccount();
            laccount.doWithdraw(ante, this, "Ante Up");
            toSave.add(laccount);
        }
    }

    public GameListing createOpenInvite(final Player user) throws GameException
    {
        if (sessionStatus != SessionStatus.Inviting) throw new GameLogicException("Invalid session status");
        return new GameListing(this, user);
    }

    private void dropMember(final SessionMember member)
    {
        final GameState lgame = getState();
        final Account laccount = (Account) member.getAccount();
        LOG.info(String.format("Remove member from session: %s", member.getAccount().getUserId()));
        if (null != lgame)
        {
            final Player playerID = member.getPlayer();
            try
            {
                lgame.doRemoveMember(playerID);
                _dirtyGame = true;
            }
            catch (final GameException e)
            {
                // Don't care
            }
        }
        getEntityManager().deletePersistent(member);
        laccount.doRemoveSession(this);
        int index = 0;
        for (final SessionMember m : members)
        {
            m.memberIndex = index++;
        }
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final GameSession other = (GameSession) obj;
        if (!super.equals(other)) return false;
        return true;
    }

    public SessionMember findMember(final Player email)
    {
        for (final SessionMember member : members)
        {
            final Player memberEmail = member.getPlayer();
            if (memberEmail.equals(email)) return member;
        }
        return null;
    }

    public MoneyAccount getBankAccount()
    {
        if (null != account) return MoneyAccount.Load(account);
        final MoneyAccount moneyAccount = new MoneyAccount(getStringId(), getKey());
        moneyAccount.setDisplayName(String.format("%s (%s game)", getName(), getGame()));
        account = moneyAccount.getKey();
        return moneyAccount;
    }

    /**
     * @return the ante
     */
    public int getUnitWager()
    {
        return ante;
    }

    public int getBalance()
    {
        return getBankAccount().getBalance();
    }

    /**
     * @return the game
     */
    public String getGame()
    {
        return game;
    }

    public String getHtml(final InterfacePreference xface)
    {
        final TinySession tiny = TinySession.load(this);
        final StringBuilder sb = new StringBuilder();
        sb.append("<div class='sdge-game-listing'>");
        String prefix = "p";
        switch (xface)
        {
            case Facebook:
                prefix = "f";
                break;
            case Mobile:
                prefix = "m";
                break;
        }
        sb.append(String.format("<strong>Name: <a href='/%s/%s'>%s</a></strong><br/>", prefix, tiny.getTinyId(), name));
        sb.append(String.format("Game: %s<br/>", game));
        sb.append(String.format("Ante: %d<br/>", ante));
        sb.append(String.format("Number of Players: %d<br/>", (null == members) ? 0 : members.size()));
        sb.append(String.format("Timeout: %d<br/>", getPlayerTimeout()));
        sb.append("</div>");
        return sb.toString();
    }

    public String getHtmlDescription()
    {
        return String.format("<b>%s</b><br/>(status=%s;game='%s';ante=%d)", name, sessionStatus.toString(), game, ante);
    }

    public String getStringId()
    {
        return KeyFactory.keyToString(getKey());
    }

    public Date getLastGameUpdate()
    {
        return lastGameUpdate;
    }

    public GameState getState()
    {
        if (null != _cachedState) return _cachedState;
        if (null == currentState) return null;
        try
        {
            GameStateEntity load = GameStateEntity.load(currentState);
            if(null == load)
            {
                this.delete(true);
                return null;
            }
            _cachedState = load.getState(this);
            return _cachedState;
        }
        catch (Throwable e)
        {
            LOG.info(Util.getFullString(e));
            return null;
        }
    }

    public int getLatestVersionNumber()
    {
        return currentVersion;
    }

    /**
     * @return the members
     */
    public Collection<Player> getPlayers()
    {
        final ArrayList<Player> returnValue = new ArrayList<Player>();
        for (final SessionMember o : members)
        {
            returnValue.add(o.getPlayer());
        }
        return returnValue;
    }

    public int getMoveTimeout()
    {
        return moveTimeout;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    public SessionMember getOwner()
    {
        if (0 == members.size()) return null;
        return members.get(0);
    }

    public int getPlayerTimeout()
    {
        GameState latestState = getState();
        if (null != latestState)
        {
            int updateTime = latestState.getUpdateTime() * 2;
            if (updateTime > playerTimeout) { return updateTime; }

        }
        return playerTimeout;
    }

    /**
     * @return the sessionStatus
     */
    public com.sawdust.engine.controller.entities.GameSession.SessionStatus getStatus()
    {
        return sessionStatus;
    }

    public ArrayList<GameState> doGetStatesSince(final int versionNumber)
    {
        final ArrayList<GameStateEntity> statelist = new ArrayList<GameStateEntity>();
        final List<GameStateEntity> statesSince = GameStateEntity.getStatesSince(this, versionNumber);
        // TODO: A more reliable merge with persisted tree.
        final HashSet<Integer> versionsFound = new HashSet<Integer>();
        for (final GameStateEntity state : localStates)
        {
            if (state.getVersionNumber() > versionNumber)
            {
                if (!versionsFound.contains(state.getVersionNumber()))
                {
                    versionsFound.add(state.getVersionNumber());
                    statelist.add(state);
                }
            }
        }
        for (final GameStateEntity state : statesSince)
        {
            if (!versionsFound.contains(state.getVersionNumber()))
            {
                versionsFound.add(state.getVersionNumber());
                statelist.add(state);
            }
        }
        Collections.sort(statelist, GameStateEntity.NaturalSort);
        final ArrayList<GameState> list = new ArrayList<GameState>();
        for (final GameStateEntity state : statelist)
        {
            list.add(state.getState(this));
        }
        return list;
    }

    public int getReadyPlayers()
    {
        int c = (aiList == null) ? 0 : aiList.size();
        for (final SessionMember member : members)
        {
            if (MemberStatus.Waiting == (member).getMemberStatus())
            {
                c++;
            }
        }
        // if(null != aiList) c += aiList.size();
        return c;
    }

    public boolean isPlaying()
    {
        return (sessionStatus == SessionStatus.Playing);
    }

    public boolean isMember(final String email)
    {
        for (final SessionMember member : members)
        {
            final String memberEmail = member.getAccount().getUserId();
            if (memberEmail.equals(email)) return true;
        }
        return false;
    }

    public SessionMember findMember(final String email)
    {
        SessionMember m = null;
        for (SessionMember s : members)
        {
            if (MemberStatus.Waiting == s.getMemberStatus())
            {
                if (email.equals(s.getAccount().getUserId()))
                {
                    m = s;
                }
            }
        }
        return m;
    }

    public SessionMember getMemberStatus(final String email)
    {
        for (final SessionMember member : members)
        {
            final String memberEmail = member.getAccount().getUserId();
            if (memberEmail.equals(email)) return member;
        }
        return null;
    }

    public void doSplitWagerPool(final Collection<Player> winners) throws GameException
    {
        if ((null != winners) && (0 < winners.size()))
        {
            final int winningPer = (int) Math.floor(getBalance() / winners.size());
            for (final Player member : winners)
            {
                payOut(member, winningPer);
            }
        }
        doWithdraw(getBalance(), null, "End of Game");
    }

    public void payOut(final Player member, final int winningPer) throws GameException
    {
        final com.sawdust.engine.controller.entities.Account laccount = member.getAccount();
        doWithdraw(winningPer, laccount, "Pay Out");
    }

    /**
     * @param pante
     *            the ante to set
     * @throws GameException
     */
    public void setUnitWager(final int pante) throws GameException
    {
        if (0 > pante) throw new GameLogicException(String.format("Cannot set the ante to %d", pante));
        ante = pante;
    }

    /**
     * @param lgame
     *            the game to set
     * @throws GameException
     */
    public void setGame(final String lgame) throws GameException
    {
        if (sessionStatus != SessionStatus.Initializing && !game.equals(lgame)) { throw new GameLogicException(
                "Runtime game name changes not allowed"); }
        game = lgame;
    }

    public void setMoveTimeout(final int lmoveTimeout)
    {
        moveTimeout = lmoveTimeout;
    }

    /**
     * @param pname
     *            the name to set
     * @throws GameException
     */
    public void setName(final String pname) throws GameException
    {
        if (sessionStatus != SessionStatus.Initializing && !name.equals(pname)) { throw new GameLogicException(
                "Runtime session name changes not allowed"); }
        name = pname;
    }

    public void setPlayerTimeout(final int pplayerTimeout)
    {
        playerTimeout = pplayerTimeout;
    }

    /**
     * @param sessionStatus
     *            the sessionStatus to set
     * @throws GameException
     */
    public boolean setStatus(final com.sawdust.engine.controller.entities.GameSession.SessionStatus sessionStatus2, GameState game)
            throws GameException
    {
        boolean isGameDirty = false;
        LOG.info("set SessionStatus " + sessionStatus2);
        int subscribedPlayers = 0;
        int idlePlayers = 0;
        for (final SessionMember member : members)
        {
            if (member.getMemberStatus() == MemberStatus.Quit)
            {
            }
            else if (member.getMemberStatus() == MemberStatus.Timeout)
            {
                idlePlayers++;
                subscribedPlayers++;
            }
            else
            {
                subscribedPlayers++;
            }

        }
        switch (sessionStatus2)
        {
            case Initializing:
                throw new GameLogicException("Cannot set session status to Initializing");
            case Inviting:
                sessionStatus = SessionStatus.Inviting;
                isGameDirty = dropInactivePlayers(game);
                break;
            case Playing:
                if (sessionStatus == SessionStatus.Closed) throw new GameLogicException("Cannot play a closed session");
                break;
            case Idle:
                if (sessionStatus != SessionStatus.Playing) throw new GameLogicException("Can only idle a playing session");
                if (0 == subscribedPlayers)
                {
                    delete(true);
                }
                break;
            case Finished:
                if (sessionStatus != SessionStatus.Playing) throw new GameLogicException("Can only finish a playing session");
                if (idlePlayers == subscribedPlayers)
                {
                    delete(true);
                }
                else
                {
                    isGameDirty = dropInactivePlayers(game);
                }
                break;
            case Closed:
                isGameDirty = dropInactivePlayers(game);
                break;
            default:
                throw new GameLogicException("Cannot set session status to " + sessionStatus2);
        }
        sessionStatus = sessionStatus2;
        return isGameDirty;
    }

    private boolean dropInactivePlayers(GameState game)
    {
        boolean isGameDirty = false;
        for (final SessionMember member : members)
        {
            if (member.getMemberStatus() == MemberStatus.Timeout)
            {
                game.doAddMessage("%s has been dropped from the game", member);
                isGameDirty = true;
                dropMember(member);
            }
            if (member.getMemberStatus() == MemberStatus.Quit)
            {
                game.doAddMessage("%s has been dropped from the game", member);
                isGameDirty = true;
                dropMember(member);
            }
        }
        return isGameDirty;
    }

    public void setState(final GameState newState) throws GameException
    {
        if (null == newState) throw new NullPointerException();
        boolean isPlaying = SessionStatus.Playing == sessionStatus;
        if (newState.isInPlay())
        {
            if (!isPlaying)
            {
                setStatus(SessionStatus.Playing, newState);
            }
        }
        else
        {
            if (isPlaying)
            {
                setStatus(SessionStatus.Finished, newState);
            }
        }

        LOG.fine("Append new state");
        newState.setVersionNumber(++currentVersion);
        if (newState.getTimeOffset() > timeOffset)
        {
            timeOffset = newState.getTimeOffset();
        }
        else
        {
            newState.setTimeOffset(timeOffset);
        }
        lastGameUpdate = new Date();
        _cachedState = newState;
        this.getState();
        final GameStateEntity gameState = new GameStateEntity(this);
        currentState = gameState.getKey();
        localStates.add(gameState);

        if(!newState.isIntermediateState())
        {
            LOG.fine("Move Agents");
            newState.moveAgents();
        }
    }

    public void doUpdateStatus() throws GameException
    {
        final GameState lgame = getState();
        final long now = new Date().getTime();

        // Update players' timeout status
        for (final SessionMember member : members)
        {
            final long lastUpdate = member.getLastUpdate().getTime();
            final long elapsedMilliseconds = now - lastUpdate;
            final Player memberEmail = member.getPlayer();
            if (elapsedMilliseconds > (getPlayerTimeout() * 1000))
            {
                if (member.getMemberStatus() != MemberStatus.Timeout)
                {
                    member.setMemberStatus(MemberStatus.Timeout);
                    if (null != lgame)
                    {
                        lgame.doAddMessage("%s seems to have disconnected...", lgame.getDisplayName(memberEmail));
                        _dirtyGame = true;
                    }
                }
            }
            else
            {
                if (member.getMemberStatus() == MemberStatus.Timeout)
                {
                    member.setMemberStatus(MemberStatus.Playing);
                    if (null != lgame)
                    {
                        lgame.doAddMessage("%s is back online!", lgame.getDisplayName(memberEmail));
                        _dirtyGame = true;
                    }
                }
            }
        }

        if (isPlaying())
        {
            // Generate automatic moves if needed
            final long elapsedMilliseconds = now - lastGameUpdate.getTime();
            if ((moveTimeout > 0) && (elapsedMilliseconds > (moveTimeout * 1000)))
            {
                if (null != lgame)
                {
                    if (lgame instanceof MultiPlayerGame)
                    {
                        final MultiPlayerGame multiPlayerCardGame = (MultiPlayerGame) lgame;
                        Participant currentPlayer = multiPlayerCardGame.getPlayerManager().getCurrentPlayer();
                        multiPlayerCardGame.doForceMove(currentPlayer).doCommand(currentPlayer, null);
                        _dirtyGame = true;
                    }
                }
            }
        }
        else if (areTriggersEnabled())
        {
            // Update player list
            for (final SessionMember member : members)
            {
                switch (member.getMemberStatus())
                {
                    case Waiting:
                        final long lastUpdate = member.getLastUpdate().getTime();
                        final long elapsedMilliseconds = now - lastUpdate;
                        final Player memberEmail = member.getPlayer();
                        if (elapsedMilliseconds > (getPlayerTimeout() * 1000))
                        {
                            if (null != lgame)
                            {
                                lgame.doAddMessage("%s disconnected and has been dropped from the game.", lgame.getDisplayName(memberEmail));
                                _dirtyGame = true;
                            }
                            dropMember(member);
                        }
                        break;
                    case Playing:
                        member.setMemberStatus(MemberStatus.Waiting);
                        member.setQueueTime(new Date(0));
                        break;
                    case Timeout:
                    case Quit:
                        dropMember(member);
                        break;
                }
            }

            int readyPlayers = getReadyPlayers();
            if (0 >= readyPlayers)
            {
                // Dead game
                // setSessionStatus(SessionStatus.Closed);
            }
            else if (readyPlayers < requiredPlayers)
            {
                // Not enough players.
                sessionStatus = SessionStatus.Inviting;
            }
            else
            {
                doStart();
            }
        }
        if (_dirtyGame)
        {
            lgame.doSaveState();
        }
    }

    private boolean areTriggersEnabled()
    {
        if (sessionStatus == SessionStatus.Initializing) return false;
        if (sessionStatus == SessionStatus.Finished) return false;
        return true;
    }

    public void doStart() throws GameException
    {
        if (getReadyPlayers() < requiredPlayers) return;
        // if (isPlaying()) { throw new GameLogicException(""); }

        final GameState tokenGame2 = getState();
        if (null != tokenGame2)
        {
            tokenGame2.doStart();
            tokenGame2.moveAgents();
            tokenGame2.doSaveState();
        }
    }

    public void doWithdraw(final int amount, final Bank from, final String description) throws GameException
    {
        if (null != from)
        {
            if (from instanceof Account)
            {
                LOG.fine(String.format("Withdrawl: %d (%s) from account %s to session %s", amount, description, ((Account) from).getName(),
                        getName()));
                MoneyTransaction.Transfer(getBankAccount(), ((Account) from).getAccount(), amount, description);
            }
            else if (from instanceof GameSession)
            {
                LOG.fine(String.format("Withdrawl: %d (%s) from session %s to session %s", amount, description, ((GameSession) from)
                        .getName(), getName()));
                MoneyTransaction.Transfer(getBankAccount(), ((GameSession) from).getBankAccount(), amount, description);
            }
            else
            {
                LOG.warning(String.format("Withdrawl: %d (%s) from UNKNOWN %s to session %s", amount, description, from, getName()));
                from.doWithdraw(-amount, null, description);
            }
        }
        else
        {
            LOG.fine(String.format("Withdrawl: %d (%s) from system to session %s", amount, description, getName()));
            MoneyTransaction.Transfer(getBankAccount(), null, amount, description);
        }
    }

    public void setMinimumPlayers(int requiredPlayers)
    {
        this.requiredPlayers = requiredPlayers;
    }

    public int getRequiredPlayers()
    {
        return this.requiredPlayers;
    }

    public void addAgent(String name)
    {
        if (null == aiList) aiList = new ArrayList<String>();
        aiList.add(name);
    }

    public void doModifyWagerPool(double factor, String msg) throws GameException
    {
        double balance = getBalance();
        int newBalance = (int) (balance * factor);
        this.doWithdraw((int) (balance - newBalance), null, msg);
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

    @Override
    public <T extends Serializable> void setResource(Class<T> c, T markovChain)
    {
        resources.put(c, new SessionResource(this, markovChain).getKey());
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
    public void doUpdateConfig(GameConfig newConfig) throws GameException
    {
        GameState latestState = this.getState();
        GameConfig currentConfig = null;
        if (null != latestState)
        {
            currentConfig = latestState.getConfig();
            latestState.setConfig(newConfig);
        }
        this.setGame(newConfig.getGameName());
        this.setMoveTimeout(Integer.parseInt(newConfig.getProperties().get(GameConfig.MOVE_TIMEOUT).value));
        this.setUnitWager(Integer.parseInt(newConfig.getProperties().get(GameConfig.ANTE).value));
        String name = newConfig.getProperties().get(GameConfig.GAME_NAME).value;
        name = name.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        this.setName(name);

    }

    @Override
    public int getActivePlayers() throws GameException
    {
        doUpdateStatus();
        int cnt = getReadyPlayers();
        for (final SessionMember member : members)
        {
            if (member.getMemberStatus() == MemberStatus.Playing)
            {
                cnt++;
            }
            else if (member.getMemberStatus() == MemberStatus.Waiting)
            {
                cnt++;
            }
        }
        return cnt;
    }

    @Override
    public void setAgentEnabled(boolean b)
    {
        _agentEnabled  = b;
    }
}
