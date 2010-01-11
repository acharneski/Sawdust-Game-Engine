package com.sawdust.games.stop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.sawdust.engine.controller.PromotionConfig;
import com.sawdust.engine.controller.Util;
import com.sawdust.engine.controller.entities.Account;
import com.sawdust.engine.controller.entities.GameSession;
import com.sawdust.engine.controller.entities.Promotion;
import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.controller.exceptions.GameLogicException;
import com.sawdust.engine.controller.exceptions.SawdustSystemError;
import com.sawdust.engine.model.AgentFactory;
import com.sawdust.engine.model.GameType;
import com.sawdust.engine.model.basetypes.BaseGame;
import com.sawdust.engine.model.basetypes.GameState;
import com.sawdust.engine.model.basetypes.MultiPlayerGame;
import com.sawdust.engine.model.basetypes.TokenGame;
import com.sawdust.engine.model.players.Agent;
import com.sawdust.engine.model.players.MultiPlayer;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.players.Player;
import com.sawdust.engine.model.players.PlayerManager;
import com.sawdust.engine.model.state.CommandResult;
import com.sawdust.engine.model.state.GameCommand;
import com.sawdust.engine.model.state.GameLabel;
import com.sawdust.engine.model.state.IndexPosition;
import com.sawdust.engine.model.state.Token;
import com.sawdust.engine.view.config.GameConfig;
import com.sawdust.engine.view.game.ActivityEvent;
import com.sawdust.engine.view.game.Notification;
import com.sawdust.engine.view.game.SolidColorGameCanvas;
import com.sawdust.engine.view.geometry.Position;
import com.sawdust.engine.view.geometry.Vector;
import com.sawdust.games.go.GoLoot;
import com.sawdust.games.wordHunt.BoardToken;

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

    private static final Position basePosition = new Position(30, 10);
    private static final Vector columnOffset = new Vector(50, 0);
    private static final Position playPosition = new Position(525, 50);
    private static final Vector playTokenOffset = new Vector(0, 75);
    private static final Vector rowOffset = new Vector(0, 50);

    public static final int NUM_ROWS = 9;
    public static final int NUMBER_OF_PLAYERS = 2;
    public static final int OFFSET_BOARD = 20;
    public static final int ROW_PLAYERTOKEN = -2;

    protected MultiPlayer _mplayerManager;
    protected TokenArray _tokenArray;
    IndexPosition _lastPosition = null;
    GamePhase _currentState = GamePhase.Lobby;
    int _lastWinner = 0;
    BoardData _boardData[][] = new BoardData[NUM_ROWS][NUM_ROWS];
    ArrayList<GameCommand> _moves;

    protected StopGame()
    {
    }

    public StopGame(final GameConfig config)
    {
        super(config);
        setCanvas(new SolidColorGameCanvas("tan", "black"));
        _mplayerManager = new MultiPlayer(NUMBER_OF_PLAYERS);
        GameSession session = getSession();
        if (null != session) session.setMinimumPlayers(NUMBER_OF_PLAYERS);
    }

    protected StopGame(StopGame obj)
    {
        super(obj);
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

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        final BaseGame runtimeAncestor = this;
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
    public StopGame doAddPlayer(final Participant agent) throws GameException
    {
        _mplayerManager.doAddMember(this, agent);
        return (StopGame) super.doAddPlayer(agent);
    }

    public StopGame doFinishTurn(final Participant player) throws GameException
    {
        final int playerIdx = _mplayerManager.getPlayerManager().getPlayerIndex(player);
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
                String displayName = getDisplayName(player);
                doAddMessage("%s won by at least %d!", displayName, diff);
                _currentState = GamePhase.Complete;
                _lastWinner = playerIdx;

                final GameSession session = getSession();
                if (null != session)
                {
                    Participant otherPlayer = _mplayerManager.getPlayerManager().getPlayerName(otherPlayerIdx);
                    String opponentName = getDisplayName(otherPlayer);
                    if (player instanceof Player)
                    {
                        String type = "Win/Stop";
                        String event = String.format("I won a game of Stop against %s!", opponentName);
                        ((Player) player).doLogActivity(new ActivityEvent(type, event));
                        doRollForLoot(player);
                    }
                    if (otherPlayer instanceof Player)
                    {
                        String type = "Lose/Stop";
                        String event = String.format("I lost a game of Stop against %s!", displayName);
                        ((Player) otherPlayer).doLogActivity(new ActivityEvent(type, event));
                    }
                    final ArrayList<Player> collection = new ArrayList<Player>();
                    if (player instanceof Player)
                    {
                        collection.add((Player) player);
                    }
                    session.doSplitWagerPool(collection);
                }
                return this;
            }
        }
        final Participant gotoNextPlayer = _mplayerManager.getPlayerManager().gotoNextPlayer();
        doAddMessage("It is now %s's turn", getDisplayName(gotoNextPlayer));
        return this;
    }

    @Override
    public GameCommand<MultiPlayerGame> doForceMove(final Participant currentPlayer) throws GameException
    {
        return new GameCommand<MultiPlayerGame>()
        {
            @Override
            public CommandResult<MultiPlayerGame> doCommand(Participant p, String parameters) throws GameException
            {
                try
                {
                    _mplayerManager.doForceMove(StopGame.this, currentPlayer);
                    return new CommandResult<MultiPlayerGame>(StopGame.this);
                }
                catch (GameException e)
                {
                    throw new SawdustSystemError(e);
                }
            }
        };
    }

    public StopGame doMove(final IndexPosition position, final Participant player) throws GameException
    {
        _moves = null;
        if (!_mplayerManager.getPlayerManager().isCurrentPlayer(player)) throw new GameLogicException("Not your turn!");
        final int playerIdx = _mplayerManager.getPlayerManager().getPlayerIndex(player);
        final String tokenType = getPlayerTokenType(playerIdx);
        _lastPosition = position;

        // Move and capture
        int row = position.getCurveIndex();
        int col = position.getCardIndex();
        BoardData boardValue = getBoardData(row, col);
        if (null != boardValue && -1 != boardValue.value) throw new GameLogicException("Can only place tiles at empty nodes");
        setBoardData(row, col, playerIdx);
        this.doAdvanceTime(500);
        this.doSaveState();
        this.doAdvanceTime(500);

        final TokenArray ta = getTokenArray();
        ta.cleanIslands(playerIdx, this, true);
        ta.cleanIslands(-1, this, true);
        doFinishTurn(player);
        return this;

    }

    @Override
    public StopGame doRemoveMember(Participant agent) throws GameException
    {
        _mplayerManager.doRemoveMember(this, agent);
        return (StopGame) super.doRemoveMember(agent);
    }

    @Override
    public StopGame doReset()
    {
        _currentState = GamePhase.Lobby;
        return this;
    }

    public StopGame doResetBoard()
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
        return this;
    }

    StopGame doRollForLoot(Participant p) throws GameException
    {
        Account account = ((Player) p).getAccount();
        StopLoot resource = account.getResource(StopLoot.class);
        if (null == resource)
        {
            resource = new StopLoot();
        }
        PromotionConfig promoConfig = resource.getLoot();
        if (null != promoConfig)
        {
            Promotion awardPromotion = account.doAwardPromotion(promoConfig);
            doAddMessage(awardPromotion.getMessage()).setTo(p.getId());
        }
        account.setResource(StopLoot.class, resource);
        return this;
    }

    @Override
    public StopGame doStart() throws GameException
    {
        if (2 > _mplayerManager.getPlayerManager().getPlayerCount()) { throw new GameLogicException(
                "Two players are required in order to play a game"); }

        final GameSession session = getSession();
        if (session != null)
        {
            session.doUnitWager();
            for (final Participant p : getPlayerManager().getPlayers())
            {
                if (p instanceof Agent<?>)
                {
                    session.doWithdraw(-session.getUnitWager(), null, "Agent Ante Up");
                }
            }
        }

        doResetBoard();
        _mplayerManager.getPlayerManager();
        Participant nextPlayer = _mplayerManager.getPlayerManager().getCurrentPlayer();
        if (null == nextPlayer)
        {
            nextPlayer = _mplayerManager.getPlayerManager().gotoNextPlayer();
        }
        if (null != nextPlayer)
        {
            doAddMessage("It is now %s's turn", getDisplayName(nextPlayer));
        }
        _currentState = GamePhase.Playing;
        return this;
    }

    @Override
    public StopGame doUpdate() throws GameException
    {
        _mplayerManager.doUpdate(this);
        return this;
    }

    @Override
    public List<AgentFactory<? extends Agent<?>>> getAgentFactories()
    {
        final List<AgentFactory<? extends Agent<?>>> agentFactories = super.getAgentFactories();
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

    @Override
    public String getBasicHtml()
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
                if (null == boardData || -1 == boardData.value)
                {
                    final String cmd = String.format("Move %d, %d", i, j);
                    sb.append(String.format("<command txt=\"%s\">-</command>", cmd));
                }
                else if (0 == boardData.value)
                {
                    sb.append("B");
                }
                else if (1 == boardData.value)
                {
                    sb.append("W");
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

    public BoardData getBoardData(int row, int col)
    {
        if (row < 0) return null;
        if (row >= NUM_ROWS) return null;
        if (col >= NUM_ROWS) return null;
        if (col < 0) return null;
        return _boardData[row][col];
    }

    public GamePhase getCurrentPhase()
    {
        return _currentState;
    }

    @Override
    public Participant getCurrentPlayer()
    {
        return _mplayerManager.getPlayerManager().getCurrentPlayer();
    }

    public GamePhase getCurrentState()
    {
        return _currentState;
    }

    @Override
    public GameType<?> getGameType()
    {
        return StopGameType.INSTANCE;
    }

    @Override
    public Collection<GameLabel> getLabels(final Player access) throws GameException
    {
        final ArrayList<GameLabel> arrayList = new ArrayList<GameLabel>();
        if (_currentState == GamePhase.Lobby)
        {
            arrayList.addAll(_mplayerManager.getLobbyLabels(this, access));
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

    public int getLastWinner()
    {
        return _lastWinner;
    }

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

    int getPlayerFromTokenType(final String art)
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

    String getPlayerTokenType(final int i)
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

    public TokenArray getTokenArray()
    {
        if (null == _tokenArray)
        {
            _tokenArray = new TokenArray(NUM_ROWS, NUM_ROWS, this);
        }
        return new TokenArray(_tokenArray);
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
                        Participant player = _mplayerManager.getPlayerManager().getPlayerName(playerIdx.value);
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
                    int i = _mplayerManager.getPlayerManager().getPlayerIndex(p);
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

    public int getUpdateTime()
    {
        if (_currentState == GamePhase.Lobby) { return 15; }
        return _mplayerManager.isSinglePlayer() ? 90 : 5;
    }

    @Override
    public com.sawdust.engine.view.game.GameFrame getView(Player access) throws GameException
    {
        final com.sawdust.engine.view.game.GameFrame returnValue = super.getView(access);
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
    public boolean isInPlay()
    {
        return _currentState == GamePhase.Playing;
    }

    public StopGame setBoardData(int row, int col, int i)
    {
        _boardData[row][col] = ((-1 == i) ? null : new BoardData(i));
        if (null != _tokenArray)
        {
            _tokenArray.setPosition(row, col, i);
        }
        _moves = null;
        return this;
    }

    public StopGame setCurrentState(GamePhase _currentState)
    {
        this._currentState = _currentState;
        return this;
    }

    public StopGame setLastWinner(int _lastWinner)
    {
        this._lastWinner = _lastWinner;
        return this;
    }
    

    public StopGame setLastPosition(IndexPosition _lastPosition)
    {
        this._lastPosition = _lastPosition;
        return this;
    }

    
    public IndexPosition getLastPosition()
    {
        return _lastPosition;
    }
}
