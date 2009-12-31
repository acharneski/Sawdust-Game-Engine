package com.sawdust.engine.game.go;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.Map.Entry;

import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.common.geometry.Position;
import com.sawdust.engine.common.geometry.Vector;
import com.sawdust.engine.game.AgentFactory;
import com.sawdust.engine.game.GameType;
import com.sawdust.engine.game.players.ActivityEvent;
import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.game.players.Player;
import com.sawdust.engine.game.players.PlayerManager;
import com.sawdust.engine.game.state.GameCommand;
import com.sawdust.engine.game.state.GameLabel;
import com.sawdust.engine.game.state.IndexPosition;
import com.sawdust.engine.game.stop.StopGame;
import com.sawdust.engine.game.stop.StopGameType;
import com.sawdust.engine.game.stop.StopIsland;
import com.sawdust.engine.game.go.Stupid1;
import com.sawdust.engine.game.stop.TokenArray;
import com.sawdust.engine.game.stop.StopGame.GamePhase;
import com.sawdust.engine.service.data.GameSession;
import com.sawdust.engine.service.debug.GameException;
import com.sawdust.engine.service.debug.GameLogicException;

public abstract class GoGame extends StopGame
{
    HashMap<Participant, PlayerScore> _scores = new HashMap<Participant, PlayerScore>();

    public static final int ROW_SCORES = -3;
    private static final Position scorePosition = new Position(525, 150);
    private static final Vector scroreOffset = new Vector(0, 35);

    @Override
    public Position getPosition(IndexPosition key, Player access) throws GameException
    {
        if (key.getCurveIndex() == ROW_SCORES) { return scorePosition.add(scroreOffset.scale(key.getCardIndex())); }
        return super.getPosition(key, access);
    }

    protected GoGame()
    {
    }

    protected GoGame(GoGame game)
    {
        super(game);
        for (Entry<Participant, PlayerScore> entry : game._scores.entrySet())
        {
            _scores.put(entry.getKey(), new PlayerScore(entry.getValue()));
        }
        _lastPlayerPassed = game._lastPlayerPassed;
    }

    public GoGame(final GameConfig config)
    {
        super(config);
    }

    @Override
    public void addMember(Participant agent) throws GameException
    {
        super.addMember(agent);
        _scores.put(agent, new PlayerScore());
    }

    @Override
    public Collection<GameLabel> getLabels(Player access) throws GameException
    {
        final ArrayList<GameLabel> labels = (ArrayList<GameLabel>) super.getLabels(access);
        if (getCurrentState() != GamePhase.Playing) return labels;

        int card = 0;

        labels.add(new GameLabel("PASS_CMD", new IndexPosition(ROW_SCORES, card++), "Pass").setCommand("Pass"));

        Participant black = getPlayerManager().playerName(0);
        PlayerScore blackScore = _scores.get(black);

        if (null != blackScore)
        {
            labels.add(new GameLabel("BLACK_PRISONERS_LABEL", new IndexPosition(ROW_SCORES, card++), "Black Prisoners:"));

            labels.add(new GameLabel("BLACK_PRISONERS_VALUE", new IndexPosition(ROW_SCORES, card++), Integer.toString(blackScore
                    .getPrisoners())));

            labels.add(new GameLabel("BLACK_TERRITORY_LABEL", new IndexPosition(ROW_SCORES, card++), "Black Territory:"));

            labels.add(new GameLabel("BLACK_TERRITORY_VALUE", new IndexPosition(ROW_SCORES, card++), Integer.toString(blackScore
                    .getTerritory())));
        }

        Participant white = getPlayerManager().playerName(1);
        PlayerScore whiteScore = _scores.get(white);

        if (null != whiteScore)
        {
            labels.add(new GameLabel("WHITE_PRISONERS_LABEL", new IndexPosition(ROW_SCORES, card++), "White Prisoners:"));

            labels.add(new GameLabel("WHITE_PRISONERS_VALUE", new IndexPosition(ROW_SCORES, card++), Integer.toString(whiteScore
                    .getPrisoners())));

            labels.add(new GameLabel("WHITE_TERRITORY_LABEL", new IndexPosition(ROW_SCORES, card++), "White Territory:"));

            labels.add(new GameLabel("WHITE_TERRITORY_VALUE", new IndexPosition(ROW_SCORES, card++), Integer.toString(whiteScore
                    .getTerritory())));
        }
        return labels;
    }

    boolean _lastPlayerPassed = false;

    @Override
    public ArrayList<GameCommand> getMoves(Participant access) throws GameException
    {
        ArrayList<GameCommand> moves = new ArrayList<GameCommand>();

        if (getCurrentState() == GamePhase.Lobby)
        {
            moves.addAll(_mplayerManager.getMoves(this, access));
        }
        else if (getCurrentState() == GamePhase.Playing)
        {
            moves.addAll(super.getMoves(access));
            moves.add(new GameCommand()
            {

                @Override
                public String getHelpText()
                {
                    return "Pass on the opportunity to move and yield play to the opponent. If both player pass in succession, the game is ended.";
                }

                @Override
                public String getCommandText()
                {
                    return "Pass";
                }

                @Override
                public boolean doCommand(Participant p, String commandText) throws GameException
                {
                    if (GoGame.this._lastPlayerPassed)
                    {
                        GoGame.this.finishTurn(p);
                        GoGame.this.finishGame(p);
                    }
                    else
                    {
                        GoGame.this._lastPlayerPassed = true;
                        _lastPosition = null;
                        GoGame.this.addMessage("%s passed!", GoGame.this.displayName(p));
                        GoGame.this.finishTurn(p);
                    }
                    return true;
                }
            });
        }

        return moves;
    }

    protected void finishGame(Participant p) throws GameException
    {
        int winningScore = 0;
        Participant winner = null;
        for (Entry<Participant, PlayerScore> s : _scores.entrySet())
        {
            int score = s.getValue().getScore();
            Participant key = s.getKey();
            this.addMessage("%s has %d points.", displayName(key), score);
            if (null == winner || score > winningScore)
            {
                winner = key;
            }
        }
        setCurrentState(GamePhase.Complete);
        setLastWinner(getPlayerManager().findPlayer(winner));
        this.addMessage("%s wins!", displayName(winner));

        final GameSession session = getSession();
        if (null != session)
        {
            String displayName = displayName(p);
            final int playerIdx = _mplayerManager.getPlayerManager().findPlayer(p);
            final int otherPlayerIdx = (playerIdx == 0) ? 1 : 0;
            Participant otherPlayer = _mplayerManager.getPlayerManager().playerName(otherPlayerIdx);
            String opponentName = displayName(otherPlayer);
            if (p instanceof Player)
            {
                String type = "Win/Go";
                String event = String.format("I won a game of Stop against %s!", opponentName);
                ((Player) p).logActivity(new ActivityEvent(type, event));
            }
            if (otherPlayer instanceof Player)
            {
                String type = "Lose/Go";
                String event = String.format("I lost a game of Stop against %s!", displayName);
                ((Player) otherPlayer).logActivity(new ActivityEvent(type, event));
            }
            final ArrayList<Player> collection = new ArrayList<Player>();
            if (winner instanceof Player)
            {
                collection.add((Player) winner);
            }
            session.payOut(collection);
        }
    }

    @Override
    public void removeMember(Participant agent) throws GameException
    {
        super.removeMember(agent);
        _scores.remove(agent);
    }

    @Override
    public void reset()
    {
        super.reset();
        // _scores.clear();
    }

    @Override
    public void start() throws GameException
    {
        for (PlayerScore s : _scores.values())
        {
            s.clear();
        }
        super.start();
    }

    @Override
    public void finishTurn(final Participant player) throws GameException
    {
        // Recalculate territory
        for (PlayerScore s : _scores.values())
        {
            s.setTerritory(0);
        }
        ArrayList<StopIsland> islands = getTokenArray().getIslands();
        HashSet<GoIsland> countedIslands = new HashSet<GoIsland>();
        for (StopIsland stopIsland : islands)
        {
            GoIsland goIsland = (GoIsland) stopIsland;
            HashMap<GoIsland, Boolean> eyes = goIsland.getEyes();
            for (GoIsland eye : eyes.keySet())
            {
                if (!countedIslands.contains(eye))
                {
                    int opposingPlayer = stopIsland.getPlayer();
                    Participant playerName = getPlayerManager().playerName(opposingPlayer);
                    PlayerScore playerScore = _scores.get(playerName);
                    playerScore.setTerritory(playerScore.getTerritory() + eye.getSize());
                    countedIslands.add(eye);
                }
            }
        }

        final Participant gotoNextPlayer = _mplayerManager.getPlayerManager().gotoNextPlayer();
        addMessage("It is now %s's turn", displayName(gotoNextPlayer));
    }

    @Override
    public List<AgentFactory<?>> getAgentFactories()
    {
        final List<AgentFactory<?>> agentFactories = new ArrayList<AgentFactory<?>>();
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
        agentFactories.add(new AgentFactory<GoAgent1>()
        {
            @Override
            public GoAgent1 getAgent(final String string)
            {
                return new GoAgent1("AI " + playerManager.getPlayerCount(), 1, 15);
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
    public TokenArray getTokenArray()
    {
        if (null == _tokenArray)
        {
            _tokenArray = new GoTokenArray(NUM_ROWS, NUM_ROWS, this);
        }
        return new GoTokenArray((GoTokenArray) _tokenArray);
    }

    public void captureIsland(StopIsland i)
    {
        int islandSize = i.getAllPositions().size();
        Participant playerName = getPlayerManager().playerName(i.getPlayer());
        PlayerScore playerScore = _scores.get(playerName);
        playerScore.addPrisoners(islandSize);
    }

    LinkedList<TokenArray> history = new LinkedList<TokenArray>();

    @Override
    public void doMove(IndexPosition position, Participant player) throws GameException
    {
        TokenArray begin = getTokenArray();
        history.push(begin);
        while (history.size() > 2)
        {
            history.removeLast();
        }
        super.doMove(position, player);
        TokenArray end = getTokenArray();
        int playerIdx = getPlayerManager().findPlayer(player);
        for (TokenArray h : history)
        {
            if (end.equals(h)) { throw new GameLogicException("Illegal Suicide"); }
        }
        if (begin.getScore(playerIdx) > end.getScore(playerIdx)) { throw new GameLogicException("Illegal Suicide"); }
        _lastPlayerPassed = false;
    }

    @Override
    public void resetBoard()
    {
        for (PlayerScore s : _scores.values())
        {
            s.clear();
        }
        super.resetBoard();
    }
    
    @Override
    public GameType<?> getGameType()
    {
       return GoGameType.INSTANCE;
    }

}
