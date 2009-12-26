package com.sawdust.engine.game.stop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.common.game.Notification;
import com.sawdust.engine.common.geometry.Position;
import com.sawdust.engine.common.geometry.Vector;
import com.sawdust.engine.game.AgentFactory;
import com.sawdust.engine.game.GameType;
import com.sawdust.engine.game.MultiPlayerGame;
import com.sawdust.engine.game.TokenGame;
import com.sawdust.engine.game.players.ActivityEvent;
import com.sawdust.engine.game.players.Agent;
import com.sawdust.engine.game.players.MultiPlayer;
import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.game.players.Player;
import com.sawdust.engine.game.players.PlayerManager;
import com.sawdust.engine.game.state.GameCommand;
import com.sawdust.engine.game.state.GameLabel;
import com.sawdust.engine.game.state.IndexPosition;
import com.sawdust.engine.game.state.Token;
import com.sawdust.engine.game.wordHunt.BoardToken;
import com.sawdust.engine.service.Util;
import com.sawdust.engine.service.data.GameSession;
import com.sawdust.engine.service.debug.GameException;
import com.sawdust.engine.service.debug.GameLogicException;
import com.sawdust.engine.service.debug.SawdustSystemError;

public abstract class StopGame extends TokenGame implements MultiPlayerGame
{
   public enum GamePhase
   {
      Complete, Lobby, Playing
   }
   
   public enum PlayerState
   {
      Playing, Waiting
   }
   
   private static final Position basePosition      = new Position(30, 10);
   private static final Vector   columnOffset      = new Vector(50, 0);
   private static final Position playPosition      = new Position(525, 50);
   private static final Vector   playTokenOffset   = new Vector(0, 75);
   private static final Vector   rowOffset         = new Vector(0, 50);
   
   public static final int       NUM_ROWS          = 9;
   public static final int       NUMBER_OF_PLAYERS = 2;
   public static final int       OFFSET_BOARD      = 20;
   public static final int       ROW_PLAYERTOKEN   = -2;
   
   private GamePhase           _currentState     = GamePhase.Lobby;
   protected IndexPosition       _lastPosition     = null;
   protected MultiPlayer         _mplayerManager;
   private int                 _lastWinner       = 0;
   protected BoardData           _boardData[][]    = new BoardData[NUM_ROWS][NUM_ROWS];
   
   protected StopGame()
   {
   }
   
   @Override
   public void removeMember(Participant agent) throws GameException
   {
      super.removeMember(agent);
      _mplayerManager.removeMember(this, agent);
   }
   
   protected StopGame(StopGame obj)
   {
      _currentState = obj._currentState;
      _lastPosition = obj._lastPosition;
      _mplayerManager = Util.Copy(obj._mplayerManager);
      for (int i = 0; i < NUM_ROWS; i++)
      {
         for (int j = 0; j < NUM_ROWS; j++)
         {
            _boardData[i][j] = obj._boardData[i][j];
         }
      }
   }
   
   public StopGame(final GameConfig config)
   {
      super(config);
      _mplayerManager = new MultiPlayer(NUMBER_OF_PLAYERS);
      GameSession session = getSession();
      if (null != session) session.setRequiredPlayers(NUMBER_OF_PLAYERS);
   }
   
   @Override
   public void addMember(final Participant agent) throws GameException
   {
      super.addMember(agent);
      _mplayerManager.addMember(this, agent);
   }
   
   public void doMove(final IndexPosition position, final Participant player) throws com.sawdust.engine.common.GameException
   {
      _moves = null;
      if (!_mplayerManager.getPlayerManager().isCurrentPlayer(player)) throw new GameLogicException("Not your turn!");
      final int playerIdx = _mplayerManager.getPlayerManager().findPlayer(player);
      final String tokenType = getPlayerTokenType(playerIdx);
      _lastPosition = position;
      
      // Move and capture
      int row = position.getCurveIndex();
      int col = position.getCardIndex();
      BoardData boardValue = getBoardData(row, col);
      if (null != boardValue && -1 != boardValue.value) throw new GameLogicException("Can only place tiles at empty nodes");
      setBoardData(row, col, playerIdx);
      this.advanceTime(500);
      this.saveState();
      this.advanceTime(500);

      final TokenArray ta = getTokenArray();
      ta.cleanIslands(playerIdx, this, true);
      ta.cleanIslands(-1, this, true);
      finishTurn(player);

   }
   
   public void finishTurn(final Participant player) throws com.sawdust.engine.common.GameException
   {
      final int playerIdx = _mplayerManager.getPlayerManager().findPlayer(player);
      final int otherPlayerIdx = (playerIdx == 0) ? 1 : 0;
      final TokenArray ta2 = getTokenArray();
      
      boolean anyImmortals = true;
      for (final StopIsland i : ta2.getIslands())
      {
         if (i.getPlayer() != TokenArray.EMPTY_VALUE)
         {
            if (i.isImmortal()) anyImmortals = true;
         }
      }
      if (anyImmortals)
      {
         
         for (final ArrayPosition p : ta2.getAllPositions())
         {
            if (-1 == ta2.getPosition(p))
            {
               ta2.setPosition(p, otherPlayerIdx);
            }
         }
         ta2.cleanIslands(otherPlayerIdx, this, false);
         for (final ArrayPosition p : ta2.getAllPositions())
         {
            ElementState elementState = ta2.get(p);
            if (null == elementState || -1 == elementState.state)
            {
               ta2.setPosition(p, otherPlayerIdx);
            }
         }
         ta2.cleanIslands(playerIdx, this, false);
         final int score = ta2.getScore(playerIdx) + ta2.getScore(-1);
         final int score2 = ta2.getScore(otherPlayerIdx);
         final int diff = score - score2;
         if (diff > 0)
         {
            String displayName = displayName(player);
            addMessage("%s won by at least %d!", displayName, diff);
            _currentState = GamePhase.Complete;
            _lastWinner = playerIdx;
            
            final GameSession session = getSession();
            if (null != session)
            {
                Participant otherPlayer = _mplayerManager.getPlayerManager().playerName(otherPlayerIdx);
                String opponentName = displayName(otherPlayer);
                if(player instanceof Player)
                {
                    String type = "Win/Stop";
                    String event = String.format("I won a game of Stop against %s!", opponentName);
                    ((Player)player).logActivity(new ActivityEvent(type,event));
                }
                if(otherPlayer instanceof Player)
                {
                    String type = "Lose/Stop";
                    String event = String.format("I lost a game of Stop against %s!", displayName);
                    ((Player)otherPlayer).logActivity(new ActivityEvent(type,event));
                }
               final ArrayList<Player> collection = new ArrayList<Player>();
               if (player instanceof Player)
               {
                  collection.add((Player) player);
               }
               session.payOut(collection);
            }
            return;
         }
      }
      final Participant gotoNextPlayer = _mplayerManager.getPlayerManager().gotoNextPlayer();
      addMessage("It is now %s's turn", displayName(gotoNextPlayer));
   }
   
   @Override
   public List<AgentFactory<?>> getAgentFactories()
   {
      final List<AgentFactory<?>> agentFactories = super.getAgentFactories();
      final PlayerManager playerManager = getPlayerManager();
      agentFactories.add(new AgentFactory<Stupid1>()
      {
         @Override
         public Stupid1 getAgent(final String string)
         {
            return new Stupid1("AI " + playerManager.getPlayerCount());
         }
         
         @Override
         public String getName()
         {
            return "Easy";
         }
      });
      agentFactories.add(new AgentFactory<StopAgent1<StopGame>>()
      {
         @Override
         public StopAgent1<StopGame> getAgent(final String string)
         {
            return new StopAgent1<StopGame>("AI " + playerManager.getPlayerCount(), 1, 15);
         }
         
         @Override
         public String getName()
         {
            return "Normal";
         }
      });
      return agentFactories;
   }
   
   public GamePhase getCurrentPhase()
   {
      return _currentState;
   }
   
   @Override
   public GameType<StopGame> getGameType()
   {
      return StopGameType.INSTANCE;
   }
   
   @Override
   public Collection<GameLabel> getLabels(final Player access) throws GameException
   {
      final ArrayList<GameLabel> arrayList = new ArrayList<GameLabel>();
      if (_currentState == GamePhase.Lobby)
      {
         arrayList.addAll(_mplayerManager.setupLobbyLabels(this, access));
      }
      else if (_currentState == GamePhase.Complete)
      {
         int idx = 2;
         final GameLabel cmdButton = new GameLabel("START", new IndexPosition(ROW_PLAYERTOKEN, idx++), "New Game");
         cmdButton.setCommand("Deal");
         cmdButton.setWidth(150);
         arrayList.add(cmdButton);
      }
      return arrayList;
   }
   
   private transient ArrayList<GameCommand> _moves = null;
   
   @Override
   public ArrayList<GameCommand> getMoves(final Participant access) throws GameException
   {
      if (null != _moves) return _moves;
      final ArrayList<GameCommand> arrayList = new ArrayList<GameCommand>();
      _moves = arrayList;
      if (_currentState == GamePhase.Lobby)
      {
         arrayList.addAll(_mplayerManager.getMoves(this, access));
      }
      else if (_currentState == GamePhase.Playing)
      {
         final TokenArray ta = getTokenArray();
         ArrayList<ArrayPosition> allPositions = ta.getAllPositions();
         for (final ArrayPosition pos : allPositions)
         {
            if (-1 == ta.getPosition(pos))
            {
               final String cmd = String.format("Move %d, %d", pos.row, pos.col);
               arrayList.add(new MoveCommand(this, cmd, pos));
            }
         }
      }
      return arrayList;
   }
   
   private int getPlayerFromTokenType(final String art)
   {
      if (art.equals("GO:BLACK"))
         return 0;
      else if (art.equals("GO:WHITE")) return 1;
      return -2;
   }
   
   public PlayerManager getPlayerManager()
   {
      final PlayerManager playerManager = _mplayerManager.getPlayerManager();
      return playerManager;
   }
   
   private String getPlayerTokenType(final int i)
   {
      if (0 == i)
         return "GO:BLACK";
      else if (1 == i) return "GO:WHITE";
      throw new IndexOutOfBoundsException();
   }
   
   @Override
   public Position getPosition(final IndexPosition key, final Player access) throws GameException
   {
      if (null == key) return null;
      final int offsetIndex = key.getCurveIndex() - OFFSET_BOARD;
      if ((offsetIndex >= 0) && (offsetIndex < NUM_ROWS))
      {
         final Position add = basePosition.add(rowOffset.scale(offsetIndex)).add(columnOffset.scale(key.getCardIndex()));
         add.setZ(-1);
         return add;
      }
      if ((key.getCurveIndex() >= 0) && (key.getCurveIndex() < NUM_ROWS)) // System.out.println(String.format("%d , %d",
         // key.getCurveIndex(), key.getCardIndex()));
         return basePosition.add(rowOffset.scale(key.getCurveIndex())).add(columnOffset.scale(key.getCardIndex()));
      else if (key.getCurveIndex() == ROW_PLAYERTOKEN)
         return playPosition.add(playTokenOffset.scale(key.getCardIndex()));
      else return _mplayerManager.getPosition(key, access);
   }
   
   protected transient TokenArray _tokenArray = null;
   
   public TokenArray getTokenArray()
   {
      if (null == _tokenArray)
      {
         _tokenArray = new TokenArray(NUM_ROWS, NUM_ROWS, this);
      }
      return new TokenArray(_tokenArray);
   }
   
   @Override
   public boolean isInPlay()
   {
      return _currentState == GamePhase.Playing;
   }
   
   @Override
   public void reset()
   {
      _currentState = GamePhase.Lobby;
   }
   
   @Override
   public void start() throws com.sawdust.engine.common.GameException
   {
      if (2 > _mplayerManager.getPlayerManager().getPlayerCount())
      {
         throw new GameLogicException("Two players are required in order to play a game");
      }
      
      final GameSession session = getSession();
      if (session != null)
      {
         session.anteUp();
         for (final Participant p : getPlayerManager().getPlayers())
         {
            if (p instanceof Agent<?>)
            {
               session.withdraw(-session.getAnte(), null, "Agent Ante Up");
            }
            if(p instanceof Player)
            {
                ((Player)p).logActivity(new ActivityEvent("Start/Go","I am starting a game of Stop!"));
            }
         }
      }
      
      resetBoard();
      _mplayerManager.getPlayerManager();
      Participant nextPlayer = _mplayerManager.getPlayerManager().getCurrentPlayer();
      if (null == nextPlayer)
      {
         nextPlayer = _mplayerManager.getPlayerManager().gotoNextPlayer();
      }
      if (null != nextPlayer)
      {
         addMessage("It is now %s's turn", displayName(nextPlayer));
      }
      _currentState = GamePhase.Playing;
   }
   
   public void resetBoard()
   {
      for (int i = 0; i < NUM_ROWS; i++)
      {
         for (int j = 0; j < NUM_ROWS; j++)
         {
            _boardData[i][j] = null;
         }
      }
      _lastPosition = null;
      _moves = null;
      _tokenArray = null;
   }
   
   @Override
   public void update() throws com.sawdust.engine.common.GameException
   {
      _mplayerManager.update(this);
   }
   
   @Override
   public com.sawdust.engine.common.game.GameState toGwt(Player access) throws GameException
   {
      final com.sawdust.engine.common.game.GameState returnValue = super.toGwt(access);
      PlayerManager playerManager = _mplayerManager.getPlayerManager();
      boolean isMember = playerManager.isMember(access);
      boolean inPlay = isInPlay();
      if (!isMember)
      {
         Notification notification = new Notification();
         notification.notifyText = "You are currently observing this game.";
         notification.add("Join Table", "Join Game");
         returnValue.setNotification(notification);
      }
      else if (!inPlay)
      {
         Notification notification = new Notification();
         notification.notifyText = "No game is currently in progress";
         notification.add("Leave Table", "Leave Game");
         returnValue.setNotification(notification);
      }
      return returnValue;
   }
   
   @Override
   public Object clone() throws CloneNotSupportedException
   {
      final StopGame runtimeAncestor = this;
      return new StopGame(this)
      {
         @Override
         public GameSession getSession()
         {
            return runtimeAncestor.getSession();
         }
      };
   }
   
   @Override
   public ArrayList<Token> getTokens()
   {
      ArrayList<Token> returnValue = new ArrayList<Token>();
      int cardIdCounter = 0;
      if (GamePhase.Playing == _currentState || GamePhase.Complete == _currentState)
      {
         IndexPosition lastPosition = _lastPosition;
         if (null != lastPosition)
         {
            Participant player = _mplayerManager.getPlayerManager().getCurrentPlayer();
            BoardToken token = new BoardToken(0X1001, "GO1", "GO:HIGHLIGHT", player, null, false, lastPosition);
            token.getPosition().setZ(2);
            token.setText("Last-moved piece");
            returnValue.add(token);
         }
         
         cardIdCounter = 0x3000;
         for (int i = 0; i < NUM_ROWS; i++)
         {
            for (int j = 0; j < NUM_ROWS; j++)
            {
               IndexPosition position = new IndexPosition(i + OFFSET_BOARD, j, 0);
               BoardToken token = new BoardToken(++cardIdCounter, "GO1", "GO:BOARD", null, null, false, position);
               token.getPosition().setZ(1);
               token.setText("Board Tile");
               returnValue.add(token);
               
               cardIdCounter++;
               BoardData playerIdx = getBoardData(i, j);
               if (null != playerIdx && -1 != playerIdx.value)
               {
                  position = new IndexPosition(i, j, 3);
                  Participant player = _mplayerManager.getPlayerManager().playerName(playerIdx.value);
                  String tokenType = getPlayerTokenType(playerIdx.value);
                  token = new BoardToken(cardIdCounter, "GO1", tokenType, player, null, false, position);
                  token.getPosition().setZ(3);
                  // token.setText(tokenType + " (placed @ " +
                  // this.versionNumber + ")");
                  returnValue.add(token);
               }
            }
         }
         
         cardIdCounter = 0x4000;
         ArrayList<IndexPosition> openPos = new ArrayList<IndexPosition>();
         for (int i = 0; i < NUM_ROWS; i++)
         {
            for (int j = 0; j < NUM_ROWS; j++)
            {
               final IndexPosition position = new IndexPosition(i, j);
               BoardData boardData = getBoardData(i, j);
               if (null == boardData || -1 == boardData.value)
               {
                  openPos.add(position);
               }
            }
         }
         for (final Participant p : _mplayerManager.getPlayerManager().getPlayers())
         {
            try
            {
               int i = _mplayerManager.getPlayerManager().findPlayer(p);
               final IndexPosition position = new IndexPosition(ROW_PLAYERTOKEN, 0, 1);
               final String art = getPlayerTokenType(i);
               final BoardToken token = new BoardToken(++cardIdCounter, "GO1", art, p, "", true, position);
               token.getPosition().setZ(4);
               token.setText("Place this piece on the board to move");
               for (final IndexPosition pos : openPos)
               {
                  final String cmd = String.format("Move %d, %d", pos.getCurveIndex(), pos.getCardIndex());
                  token.getMoveCommands().put(pos, cmd);
               }
               returnValue.add(token);
            }
            catch (GameException e)
            {
               throw new SawdustSystemError(e);
            }
         }
      }
      return returnValue;
   }
   
   @Override
   public Participant getCurrentPlayer()
   {
      return _mplayerManager.getPlayerManager().getCurrentPlayer();
   }
   
   public void setBoardData(int row, int col, int i)
   {
      _boardData[row][col] = ((-1 == i) ? null : new BoardData(i));
      if (null != _tokenArray)
      {
         _tokenArray.setPosition(row, col, i);
      }
      _moves = null;
   }
   
   @Override
   public void doForceMove(Participant currentPlayer) throws com.sawdust.engine.common.GameException
   {
      try
      {
         _mplayerManager.doForceMove(this, currentPlayer);
      }
      catch (GameException e)
      {
         e.printStackTrace();
      }
   }
   
   public BoardData getBoardData(int row, int col)
   {
      if (row < 0) return null;
      if (row >= NUM_ROWS) return null;
      if (col >= NUM_ROWS) return null;
      if (col < 0) return null;
      return _boardData[row][col];
   }
   
   public void setLastWinner(int _lastWinner)
   {
      this._lastWinner = _lastWinner;
   }
   
   public int getLastWinner()
   {
      return _lastWinner;
   }

   public void setCurrentState(GamePhase _currentState)
   {
      this._currentState = _currentState;
   }

   public GamePhase getCurrentState()
   {
      return _currentState;
   }

   @Override
   public String renderBasicHtml()
   {
       StringBuilder sb = new StringBuilder();
       final HashMap<IndexPosition, Token> tokenIndexByPosition = getTokenIndexByPosition();
       sb.append("<table>");
       for (int i = 0; i < NUM_ROWS; i++)
       {
           sb.append("<tr>");
           for (int j = 0; j < NUM_ROWS; j++)
           {
               sb.append("<td>");
               final IndexPosition position = new IndexPosition(i, j);
               BoardData boardData = getBoardData(i, j);
               if (null == boardData)
               {
                   if(null == boardData || -1 == boardData.value)
                   {
                       final String cmd = String.format("Move %d, %d", i, j);
                       sb.append(String.format("<command txt=\"%s\">.</command>", cmd));
                   }
                   else if(0 == boardData.value)
                   {
                       sb.append("X");
                   }
                   else if(1 == boardData.value)
                   {
                       sb.append("O");
                   }
                   else 
                   {
                       sb.append("?");
                   }
               }
               else
               {
                   sb.append("?");
               }
               sb.append("</td>");
           }
           sb.append("</tr>");
       }
       sb.append("</table>");
       return sb.toString();
   }
}
