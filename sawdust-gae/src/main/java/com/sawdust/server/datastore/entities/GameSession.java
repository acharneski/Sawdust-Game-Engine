package com.sawdust.server.datastore.entities;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.NullValue;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.Id;
import javax.persistence.OrderBy;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.sawdust.engine.common.Bank;
import com.sawdust.engine.game.BaseGame;
import com.sawdust.engine.game.Game;
import com.sawdust.engine.game.MultiPlayerCardGame;
import com.sawdust.engine.game.MultiPlayerGame;
import com.sawdust.engine.game.players.Agent;
import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.game.players.Player;
import com.sawdust.engine.service.Util;
import com.sawdust.engine.service.debug.GameException;
import com.sawdust.engine.service.debug.GameLogicException;
import com.sawdust.server.datastore.DataObj;
import com.sawdust.server.datastore.DataStore;
import com.sawdust.server.datastore.DataObj;
import com.sawdust.server.datastore.entities.Account.InterfacePreference;
import com.sawdust.server.datastore.entities.SessionMember.MemberStatus;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class GameSession extends DataObj implements com.sawdust.engine.service.data.GameSession
{
   private static final Logger LOG           = Logger.getLogger(GameSession.class.getName());
   
   @Persistent
   public int                  playerTimeout = 90;
   
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
            returnValue.updateStatus();
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
   private Game                                 _cachedState   = null;
   
   @NotPersistent
   private boolean                              _dirtyGame     = false;
   
   @Persistent
   private Key                                  account        = null;
   
   @Persistent
   private int                                  ante           = 0;
   
   @Persistent
   private ArrayList<String>                    aiList         = new ArrayList<String>();
   
   @Persistent
   public Key                                   currentState   = null;
   
   @Persistent
   private int                                  currentVersion;
   
   @Persistent
   private String                               game           = "NULL";
   
   @Persistent
   private Date                                 lastGameUpdate = new Date();
   
   @NotPersistent
   private final transient ArrayList<GameState> localStates    = new ArrayList<GameState>();
   
   @Persistent(mappedBy = "gameSession")
   @OrderBy(value = "memberIndex")
   private ArrayList<SessionMember>             members        = new ArrayList<SessionMember>();
   
   @Persistent
   private int                                  moveTimeout    = 0;
   
   @Persistent
   private String                               name           = "No Title";
   
   @Persistent
   public SessionStatus                         sessionStatus  = SessionStatus.Initializing;
   
   @Persistent
   private int                                  timeOffset;
   
   protected GameSession()
   {
       super();
   }
   
   public GameSession(final Account paccount) throws GameException
   {
      super(KeyFactory.createKey(GameSession.class.getSimpleName(), (paccount.getKey() + "%" + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date()))));
      if (this != DataStore.Add(this)) throw new AssertionError();
   }
   
   public void addPlayer(final Participant p) throws GameException
   {
      if ((sessionStatus != SessionStatus.Inviting) && (sessionStatus != SessionStatus.Initializing))
         throw new GameLogicException("This session is not in the invite mode!");
      
      if (p instanceof Player)
      {
         Player player = (Player) p;
         final com.sawdust.engine.service.data.Account _account = player.loadAccount();
         if (getAnte() > _account.getBalance()) throw new GameLogicException("The game's ante is too high!");
         ((Account) _account).addSession(this);
         
         final SessionMember sessionMember = new SessionMember(this, (com.sawdust.server.datastore.entities.Account) _account);
         sessionMember.memberIndex = members.size();
         sessionMember.setMemberStatus(MemberStatus.Waiting);
         LOG.info(String.format("Adding Member to Session: %s", sessionMember.getAccount().getUserId()));
         members.add(sessionMember);
      }
      else if (p instanceof Agent<?>)
      {
         addAi(p.getId());
      }
      
      final Game _game = getLatestState();
      if (null != _game)
      {
         _game.addMember(p);
         _game.saveState();
      }
   }
   
   public void anteUp() throws com.sawdust.engine.common.GameException
   {
      final ArrayList<com.sawdust.engine.service.data.Account> toSave = new ArrayList<com.sawdust.engine.service.data.Account>();
      for (final Player member : getMembers())
      {
         final com.sawdust.engine.service.data.Account laccount = member.loadAccount();
         laccount.withdraw(ante, this, "Ante Up");
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
      final Game lgame = getLatestState();
      final Account laccount = (Account) member.getAccount();
      LOG.info(String.format("Remove member from session: %s", member.getAccount().getUserId()));
      if (null != lgame)
      {
         final Player playerID = member.getPlayer();
         try
         {
            lgame.removeMember(playerID);
            _dirtyGame = true;
         }
         catch (final GameException e)
         {
            // Don't care
         }
      }
      getEntityManager().deletePersistent(member);
      laccount.removeSession(this);
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
   
   public MoneyAccount getAccount()
   {
      if (null != account) return MoneyAccount.Load(account);
      final MoneyAccount moneyAccount = new MoneyAccount(getId(), getKey());
      moneyAccount.setDisplayName(String.format("%s (%s game)", getName(), getGame()));
      account = moneyAccount.getKey();
      return moneyAccount;
   }
   
   /**
    * @return the ante
    */
   public int getAnte()
   {
      return ante;
   }
   
   public int getBalance()
   {
      return getAccount().getCurrentBalence();
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
      switch (xface) {
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
      sb.append(String.format("Timeout: %d<br/>", playerTimeout));
      sb.append("</div>");
      return sb.toString();
   }
   
   public String getHtmlDescription()
   {
      return String.format("<b>%s</b><br/>(status=%s;game='%s';ante=%d)", name, sessionStatus.toString(), game, ante);
   }
   
   public String getId()
   {
      return KeyFactory.keyToString(getKey());
   }
   
   public Date getLastGameUpdate()
   {
      return lastGameUpdate;
   }
   
   public Game getLatestState()
   {
      if (null != _cachedState) return _cachedState;
      if (null == currentState) return null;
      try
      {
         _cachedState = GameState.load(currentState).getState(this);
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
   public Collection<Player> getMembers()
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
      return playerTimeout;
   }
   
   /**
    * @return the sessionStatus
    */
   public com.sawdust.engine.service.data.GameSession.SessionStatus getSessionStatus()
   {
      return sessionStatus;
   }
   
   public ArrayList<Game> getStatesSince(final int versionNumber)
   {
      final ArrayList<GameState> statelist = new ArrayList<GameState>();
      final List<GameState> statesSince = GameState.getStatesSince(this, versionNumber);
      // TODO: A more reliable merge with persisted tree.
      final HashSet<Integer> versionsFound = new HashSet<Integer>();
      for (final GameState state : localStates)
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
      for (final GameState state : statesSince)
      {
         if (!versionsFound.contains(state.getVersionNumber()))
         {
            versionsFound.add(state.getVersionNumber());
            statelist.add(state);
         }
      }
      Collections.sort(statelist, GameState.NaturalSort);
      final ArrayList<Game> list = new ArrayList<Game>();
      for (final GameState state : statelist)
      {
         list.add(state.getState(this));
      }
      return list;
   }
   
   @Persistent
   private int requiredPlayers = Integer.MAX_VALUE;
   
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
   
   public int getMemberQueuePosition(final String email)
   {
      if (null == email) return -1;
      SessionMember m = findMember(email);
      if (null == m) return -1;
      ArrayList<SessionMember> arrayList = getPlayerQueue();
      return arrayList.indexOf(m);
   }
   
   private ArrayList<SessionMember> getPlayerQueue()
   {
      ArrayList<SessionMember> arrayList = new ArrayList<SessionMember>();
      for (SessionMember s : members)
      {
         if (MemberStatus.Waiting == s.getMemberStatus())
         {
            arrayList.add(s);
         }
      }
      Collections.sort(arrayList, new Comparator<SessionMember>()
      {
         
         public int compare(SessionMember o1, SessionMember o2)
         {
            // TODO Auto-generated method stub
            return 0;
         }
      });
      return arrayList;
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
   
   public void payOut(final Collection<Player> winners) throws com.sawdust.engine.common.GameException
   {
      if ((null != winners) && (0 < winners.size()))
      {
         final int winningPer = (int) Math.floor(getBalance() / winners.size());
         for (final Player member : winners)
         {
            payOut(member, winningPer);
         }
      }
      withdraw(getBalance(), null, "End of Game");
   }
   
   public void payOut(final Player member, final int winningPer) throws com.sawdust.engine.common.GameException
   {
      final com.sawdust.engine.service.data.Account laccount = member.loadAccount();
      withdraw(winningPer, laccount, "Pay Out");
   }
   
   /**
    * @param pante
    *           the ante to set
    * @throws GameException
    */
   public void setAnte(final int pante) throws GameException
   {
      if (sessionStatus != SessionStatus.Initializing) throw new GameLogicException("This session is already configured!");
      if (0 > pante) throw new GameLogicException(String.format("Cannot set the ante to %d", pante));
      ante = pante;
   }
   
   /**
    * @param lgame
    *           the game to set
    * @throws GameException
    */
   public void setGame(final String lgame) throws GameException
   {
      if (sessionStatus != SessionStatus.Initializing)
      {
         throw new GameLogicException("This session is already configured!");
      }
      game = lgame;
   }
   
   public void setMoveTimeout(final int lmoveTimeout)
   {
      moveTimeout = lmoveTimeout;
   }
   
   /**
    * @param pname
    *           the name to set
    * @throws GameException
    */
   public void setName(final String pname) throws GameException
   {
      if (sessionStatus != SessionStatus.Initializing) throw new GameLogicException("This session is already configured!");
      name = pname;
   }
   
   public void setPlayerTimeout(final int pplayerTimeout)
   {
      playerTimeout = pplayerTimeout;
   }
   
   /**
    * @param sessionStatus
    *           the sessionStatus to set
    * @throws GameException
    */
   public boolean setSessionStatus(final com.sawdust.engine.service.data.GameSession.SessionStatus sessionStatus2, Game game)
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
      switch (sessionStatus2) {
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
   
   private boolean dropInactivePlayers(Game game)
   {
      boolean isGameDirty = false;
      for (final SessionMember member : members)
      {
         if (member.getMemberStatus() == MemberStatus.Timeout)
         {
            game.addMessage("%s has been dropped from the game", member);
            isGameDirty = true;
            dropMember(member);
         }
         if (member.getMemberStatus() == MemberStatus.Quit)
         {
            game.addMessage("%s has been dropped from the game", member);
            isGameDirty = true;
            dropMember(member);
         }
      }
      return isGameDirty;
   }
   
   public void setState(final Game newState) throws GameException
   {
      if (null == newState) throw new NullPointerException();
      boolean isPlaying = SessionStatus.Playing == sessionStatus;
      if (newState.isInPlay())
      {
         if (!isPlaying)
         {
            setSessionStatus(SessionStatus.Playing, newState);
         }
      }
      else
      {
         if (isPlaying)
         {
            setSessionStatus(SessionStatus.Finished, newState);
         }
      }
      
      LOG.info("Append new state");
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
      this.getLatestState();
      final GameState gameState = new GameState(this);
      currentState = gameState.getKey();
      localStates.add(gameState);
   }
   
   public void updateStatus() throws com.sawdust.engine.common.GameException
   {
      final Game lgame = getLatestState();
      final long now = new Date().getTime();
      
      if (isPlaying())
      {
         // Update players' timeout status
         for (final SessionMember member : members)
         {
            final long lastUpdate = member.getLastUpdate().getTime();
            final long elapsedMilliseconds = now - lastUpdate;
            final Player memberEmail = member.getPlayer();
            if (elapsedMilliseconds > (playerTimeout * 1000))
            {
               if (member.getMemberStatus() != MemberStatus.Timeout)
               {
                  member.setMemberStatus(MemberStatus.Timeout);
                  if (null != lgame)
                  {
                     lgame.addMessage("%s seems to have disconnected...", lgame.displayName(memberEmail));
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
                     lgame.addMessage("%s is back online!", lgame.displayName(memberEmail));
                     _dirtyGame = true;
                  }
               }
            }
         }
         
         // Generate automatic moves if needed
         final long elapsedMilliseconds = now - lastGameUpdate.getTime();
         if ((moveTimeout > 0) && (elapsedMilliseconds > (moveTimeout * 1000)))
         {
            if (null != lgame)
            {
               if (lgame instanceof MultiPlayerGame)
               {
                  final MultiPlayerGame multiPlayerCardGame = (MultiPlayerGame) lgame;
                  multiPlayerCardGame.doForceMove(multiPlayerCardGame.getPlayerManager().getCurrentPlayer());
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
            switch (member.getMemberStatus()) {
            case Waiting:
               final long lastUpdate = member.getLastUpdate().getTime();
               final long elapsedMilliseconds = now - lastUpdate;
               final Player memberEmail = member.getPlayer();
               if (elapsedMilliseconds > (playerTimeout * 1000))
               {
                  if (null != lgame)
                  {
                     lgame.addMessage("%s disconnected and has been dropped from the game.", lgame.displayName(memberEmail));
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
            // pick playing players ...
            ArrayList<Participant> players = new ArrayList<Participant>();
            for (final SessionMember m : getPlayerQueue())
            {
               if (players.size() >= requiredPlayers) break;
               players.add(m.getPlayer());
            }
            start(players);
         }
      }
      if (_dirtyGame)
      {
         lgame.saveState();
      }
   }
   
   private boolean areTriggersEnabled()
   {
      if (sessionStatus == SessionStatus.Initializing) return false;
      if (sessionStatus == SessionStatus.Finished) return false;
      return true;
   }
   
   public void start(Collection<Participant> players) throws com.sawdust.engine.common.GameException
   {
      if (getReadyPlayers() < requiredPlayers) return;
      // if (isPlaying()) { throw new GameLogicException(""); }
      
      final Game tokenGame2 = getLatestState();
      if (null != tokenGame2)
      {
         tokenGame2.start();
         tokenGame2.update();
         tokenGame2.saveState();
      }
   }
   
   public void withdraw(final int amount, final Bank from, final String description) throws com.sawdust.engine.common.GameException
   {
      if (null != from)
      {
         if (from instanceof Account)
         {
            LOG.fine(String.format("Withdrawl: %d (%s) from account %s to session %s", amount, description, ((Account) from).getName(),
                  getName()));
            MoneyTransaction.Transfer(getAccount(), ((Account) from).getAccount(), amount, description);
         }
         else if (from instanceof GameSession)
         {
            LOG.fine(String.format("Withdrawl: %d (%s) from session %s to session %s", amount, description, ((GameSession) from).getName(),
                  getName()));
            MoneyTransaction.Transfer(getAccount(), ((GameSession) from).getAccount(), amount, description);
         }
         else
         {
            LOG.warning(String.format("Withdrawl: %d (%s) from UNKNOWN %s to session %s", amount, description, from, getName()));
            from.withdraw(-amount, null, description);
         }
      }
      else
      {
         LOG.fine(String.format("Withdrawl: %d (%s) from system to session %s", amount, description, getName()));
         MoneyTransaction.Transfer(getAccount(), null, amount, description);
      }
   }
   
   public void setRequiredPlayers(int requiredPlayers)
   {
      this.requiredPlayers = requiredPlayers;
   }
   
   public int getRequiredPlayers()
   {
      return this.requiredPlayers;
   }
   
   public void addAi(String name)
   {
      if (null == aiList) aiList = new ArrayList<String>();
      aiList.add(name);
   }
   
   public void modifyPayout(double factor, String msg) throws com.sawdust.engine.common.GameException
   {
      double balance = getBalance();
      int newBalance = (int) (balance * factor);
      this.withdraw((int) (balance - newBalance), null, msg);
   }
}
