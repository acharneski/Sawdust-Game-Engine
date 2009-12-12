package com.sawdust.engine.game.wordHunt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.common.config.PropertyConfig;
import com.sawdust.engine.common.game.GameState;
import com.sawdust.engine.common.game.Notification;
import com.sawdust.engine.common.geometry.Position;
import com.sawdust.engine.common.geometry.Vector;
import com.sawdust.engine.game.GameType;
import com.sawdust.engine.game.PersistantTokenGame;
import com.sawdust.engine.game.players.Agent;
import com.sawdust.engine.game.players.MultiPlayer;
import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.game.players.Player;
import com.sawdust.engine.game.state.GameCommand;
import com.sawdust.engine.game.state.GameLabel;
import com.sawdust.engine.game.state.IndexPosition;
import com.sawdust.engine.game.state.Token;
import com.sawdust.engine.game.wordHunt.TokenArray.ArrayPosition;
import com.sawdust.engine.service.data.GameSession;
import com.sawdust.engine.service.debug.GameException;

public abstract class WordHuntGame extends PersistantTokenGame
{
    public enum GameState
    {
        Complete, Lobby, Playing
    }

    public enum PlayerState
    {
        Complete, Playing
    }

    private static final Position basePosition = new Position(50, 10);
    private static final Vector cmdOffset = new Vector(0, 40);
    private static final Position cmdPosition = new Position(525, 10);
    private static final Vector columnOffset = new Vector(55, 0);

    public static final int CURVE_CMDS = -1;
    public static final String[] letters = new String[]
    { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
    public static final int NUM_COLS = 7;
    public static final int NUM_ROWS = 8;
    private static final Vector rowOffset = new Vector(0, 55);

    private GameState _currentState = GameState.Lobby;
    private MultiPlayer _mplayerManager;

    private Date _roundEndTime;

    private HashMap<String, ArrayList<IndexPosition>> currentPath = new HashMap<String, ArrayList<IndexPosition>>();

    private HashMap<String, ArrayList<String>> currentWords = new HashMap<String, ArrayList<String>>();

    private HashMap<String, PlayerState> playerStatus = new HashMap<String, PlayerState>();

    private HashMap<String, String> spellingBuffer = new HashMap<String, String>();

    /**
     * 
     */
    public WordHuntGame()
    {
        super();
    }

    /**
     * @param config
     */
    public WordHuntGame(final GameConfig config)
    {
        super(config);
        int numberOfPlayers = getNumberOfPlayers(config);
        _mplayerManager = new MultiPlayer(numberOfPlayers);
        getSession().setRequiredPlayers(numberOfPlayers);
    }

    @Override
    public void addMember(final Participant agent) throws GameException
    {
        super.addMember(agent);
        _mplayerManager.addMember(this, agent);
    }

    private boolean adjacent(final IndexPosition position, final IndexPosition prevPosition)
    {
        if (null == position) return false;
        if (null == prevPosition) return false;
        if (position.getCurveIndex() + 1 < prevPosition.getCurveIndex()) return false;
        if (position.getCurveIndex() - 1 > prevPosition.getCurveIndex()) return false;
        if (position.getCardIndex() + 1 < prevPosition.getCardIndex()) return false;
        if (position.getCardIndex() - 1 > prevPosition.getCardIndex()) return false;
        final boolean curveMatch = position.getCurveIndex() == prevPosition.getCurveIndex();
        final boolean cardMatch = position.getCardIndex() == prevPosition.getCardIndex();
        if (curveMatch && cardMatch) return true;
        if (curveMatch || cardMatch) return true;
        return true;
    }

    private void clearCurrentWord(final String userId)
    {
        getPath(userId).clear();
        setSpellingBuffer(userId, "");
        for (final Token t : getTokens())
        {
            ((BoardToken) t).selectedFor.remove(userId);
        }
    }

    private BoardToken generateToken(final int chainSize, final MarkovPredictor mk, final TokenArray a, final ArrayPosition p)
    {
        // Generate raw ideas
        final HashMap<String, Integer> ideas = new HashMap<String, Integer>();
        final HashSet<String> nearbyStuff = new HashSet<String>();
        for (final ArrayList<ArrayPosition> na : p.getNeighborChain(chainSize))
        {
            nearbyStuff.add(a.get(na.get(0)).state);
            final StringBuilder state = new StringBuilder();
            for (final ArrayPosition n : na)
            {
                state.append(a.get(n).state);
            }
            for (int n = 0; n < 10; n++)
            {
                final String rand = mk.predict(state.toString());
                int v = 0;
                if (ideas.containsKey(rand))
                {
                    v = ideas.get(rand);
                }
                ideas.put(rand, ++v);
            }
        }

        // Sort and collate
        final ArrayList<String> arrayList = new ArrayList<String>(ideas.keySet());
        for (final String rand : arrayList)
        {
            int v = 0;
            if (ideas.containsKey(rand))
            {
                v = ideas.get(rand);
            }
            if (nearbyStuff.contains(rand))
            {
                v *= 0.1;
            }
            ideas.put(rand, (int) (100 * v * Math.random()));
        }
        Collections.sort(arrayList, new Comparator<String>()
        {
            public int compare(final String o1, final String o2)
            {
                return -ideas.get(o1).compareTo(ideas.get(o2));
            }
        });

        // Finish up
        String winningString;
        if (0 == arrayList.size())
        {
            winningString = mk.predict("");
        }
        else
        {
            winningString = arrayList.get(0);
        }
        a.put(p, a.new ElementState(winningString));
        final IndexPosition position = new IndexPosition(p.row, p.col);
        final BoardToken token = new BoardToken(++cardIdCounter, "WORD1", winningString, null, null, false, position);
        token.setText(winningString);
        token.toggleImageId = winningString + "2";
        token.toggleCommand = String.format("Select (%d, %d)", p.row, p.col);
        return token;
    }

    @Override
    public GameType<WordHuntGame> getGameType()
    {
        return WordHuntGameType.INSTANCE;
    }

    @Override
    public Collection<GameLabel> getLabels(final Player access) throws GameException
    {
        final ArrayList<GameLabel> arrayList = new ArrayList<GameLabel>();
        if (_currentState == GameState.Lobby)
        {
            arrayList.addAll(_mplayerManager.setupLobbyLabels(this, access));
        }
        else if (_currentState == GameState.Playing)
        {
            int idx = 0;
            GameLabel cmdButton = new GameLabel("SubmitWord", new IndexPosition(CURVE_CMDS, idx++), "Submit Word");
            cmdButton.setCommand("Enter Word");
            cmdButton.setWidth(150);
            arrayList.add(cmdButton);

            cmdButton = new GameLabel("Finish", new IndexPosition(CURVE_CMDS, idx++), "Finish");
            cmdButton.setCommand("Finish");
            cmdButton.setWidth(150);
            arrayList.add(cmdButton);

            cmdButton = new GameLabel("Clear", new IndexPosition(CURVE_CMDS, idx++), "Clear");
            cmdButton.setCommand("Clear");
            cmdButton.setWidth(150);
            arrayList.add(cmdButton);

            final long ctime = new Date().getTime();
            final long etime = _roundEndTime.getTime();
            final double timeLeft = (etime - ctime) / 1000.0;
            arrayList.add(new GameLabel("TimeLeft", new IndexPosition(CURVE_CMDS, idx++), Integer.toString((int) Math.ceil(timeLeft)) + " sec left"));

            final String txt = String.format("My Score: %d (%d)", getScore(access), getWordList(access.getUserId()).size());
            arrayList.add(new GameLabel("WordCount", new IndexPosition(CURVE_CMDS, idx++), txt));

            for (final Participant p : _mplayerManager.getPlayerManager().getPlayers())
            {
                if (p.equals(access))
                {
                    continue;
                }
                final String txt2 = String.format("%s: %d (%d)", displayName(p), getScore(access), getWordList(p.getId()).size());
                arrayList.add(new GameLabel("WordCount", new IndexPosition(CURVE_CMDS, idx++), txt2));

            }
        }
        else if (_currentState == GameState.Complete)
        {
            int idx = 0;
            final GameLabel cmdButton = new GameLabel("SubmitWord", new IndexPosition(CURVE_CMDS, idx++), "Deal Again");
            cmdButton.setCommand("Deal");
            cmdButton.setWidth(150);
            arrayList.add(cmdButton);
        }
        return arrayList;
    }

    private MarkovPredictor getMarkov(final int chainSize)
    {
        final int refreshMs = 1000 * 60 * 60;
        final String uniqueTime = Integer.toString((int) (new Date().getTime() / refreshMs));
        final MarkovPredictor mk = new MarkovPredictor(chainSize + 1);
        final ArrayList<String> trainingData = new ArrayList<String>();
        trainingData.add(getUrl("http://news.google.com/#" + uniqueTime));
        trainingData.add(getUrl("http://slashdot.org/#" + uniqueTime));
        for (final String latestNews : trainingData)
        {
            if (null == latestNews)
            {
                continue;
            }
            mk.learn(latestNews.toUpperCase().replaceAll("</?[^>]+>", "").replaceAll("[^A-Z]", ""));
        }
        return mk;
    }

    @Override
    public ArrayList<GameCommand> getMoves(final Participant access) throws GameException
    {
        final ArrayList<GameCommand> arrayList = new ArrayList<GameCommand>();
        if (_currentState == GameState.Lobby)
        {
            arrayList.addAll(_mplayerManager.getMoves(this, access));
        }
        else if (_currentState == GameState.Playing)
        {
            final HashMap<IndexPosition, Token> tokenIndexByPosition = getTokenIndexByPosition();
            final ArrayList<IndexPosition> path = getPath(access.getId());
            final IndexPosition prevPosition = (0 == path.size()) ? null : path.get(path.size() - 1);
            for (int i = 0; i < NUM_ROWS; i++)
            {
                for (int j = 0; j < NUM_COLS; j++)
                {
                    final String cmd = String.format("Select (%d, %d)", i, j);
                    final IndexPosition position = new IndexPosition(i, j);
                    if (tokenIndexByPosition.containsKey(position))
                    {
                        final BoardToken token = (BoardToken) tokenIndexByPosition.get(position);
                        arrayList.add(new GameCommand()
                        {

                            @Override
                            public String getHelpText()
                            {
                                return "Submit the currently selected word";
                            }

                            @Override
                            public String getCommandText()
                            {
                                return cmd;
                            }

                            @Override
                            public boolean doCommand(Participant p) throws com.sawdust.engine.common.GameException
                            {
                                Player user = (Player) p;
                                if ((null != prevPosition) && !adjacent(position, prevPosition))
                                {
                                    WordHuntGame.this.addMessage("Non-adjacent letter selected: " + token.letter).setTo(user.getUserId());
                                    clearCurrentWord(user.getUserId());

                                    getPath(user.getUserId()).add(position);
                                    token.selectedFor.add(user.getUserId());
                                    setSpellingBuffer(user.getUserId(), token.letter);

                                    WordHuntGame.this.saveState();
                                }
                                else
                                {
                                    final String tSpellingBuffer = getSpellingBuffer(user.getUserId());
                                    final String currentWord = tSpellingBuffer + token.letter;

                                    getPath(user.getUserId()).add(position);
                                    token.selectedFor.add(user.getUserId());
                                    setSpellingBuffer(user.getUserId(), currentWord);

                                    maybeComplete();
                                    WordHuntGame.this.saveState();
                                }
                                return true;
                            }
                        });
                    }
                }
            }
            arrayList.add(new GameCommand()
            {
                public boolean doCommand(Participant p) throws com.sawdust.engine.common.GameException
                {
                    Player user = (Player) p;
                    GameSession game = WordHuntGame.this.getSession();
                    final String currentWord = getSpellingBuffer(user.getUserId());
                    final ArrayList<String> wordList = getWordList(user.getUserId());
                    if (wordList.contains(currentWord))
                    {
                        WordHuntGame.this.addMessage("Already Entered: " + currentWord).setTo(user.getUserId());
                        WordHuntGame.this.saveState();
                    }
                    else if (WordHuntGame.this.verifyWord(currentWord))
                    {
                        WordHuntGame.this.addMessage(String.format("New Word: %s (%d points)", currentWord, getWordScore(currentWord))).setTo(user.getUserId());
                        addWord(user, currentWord);
                    }
                    else
                    {
                        WordHuntGame.this.addMessage("Rejected: " + currentWord).setTo(user.getUserId());
                    }
                    clearCurrentWord(user.getUserId());
                    maybeComplete();
                    WordHuntGame.this.saveState();
                    return true;
                }

                public String getCommandText()
                {
                    return "Enter Word";
                }

                public String getHelpText()
                {
                    return "Submit the currently selected word";
                }
            });
            arrayList.add(new GameCommand()
            {
                public boolean doCommand(Participant p) throws GameException
                {
                    Player user = (Player) p;
                    GameSession game = WordHuntGame.this.getSession();
                    clearCurrentWord(user.getUserId());
                    WordHuntGame.this.saveState();
                    return true;
                }

                public String getCommandText()
                {
                    return "Clear";
                }

                public String getHelpText()
                {
                    return "Clear selected letters";
                }
            });
            arrayList.add(new GameCommand()
            {
                public boolean doCommand(Participant p) throws com.sawdust.engine.common.GameException
                {
                    Player user = (Player) p;
                    GameSession game = WordHuntGame.this.getSession();
                    playerStatus.put(user.getUserId(), PlayerState.Complete);
                    maybeComplete();
                    WordHuntGame.this.saveState();
                    return true;
                }

                public String getCommandText()
                {
                    return "Finish";
                }

                public String getHelpText()
                {
                    return "Finish the game; no more words are found";
                }
            });
        }
        return arrayList;
    }

    private int getNumberOfPlayers(final GameConfig config)
    {
        final PropertyConfig propertyConfig = config.getProperties().get(GameConfig.NUM_PLAYERS);
        if (null != propertyConfig) return propertyConfig.getInteger();
        return 2;
    }

    private ArrayList<IndexPosition> getPath(final String string)
    {
        if (!currentPath.containsKey(string))
        {
            currentPath.put(string, new ArrayList<IndexPosition>());
        }
        return currentPath.get(string);
    }

    private PlayerState getPlayerStatus(final String userId)
    {
        if (!playerStatus.containsKey(userId)) return PlayerState.Playing;
        return playerStatus.get(userId);
    }

    public Position getPosition(final IndexPosition key, final Player access) throws GameException
    {
        if ((key.getCurveIndex() >= 0) && (key.getCurveIndex() < NUM_ROWS))
            return basePosition.add(rowOffset.scale(key.getCurveIndex())).add(columnOffset.scale(key.getCardIndex()));
        else if (CURVE_CMDS == key.getCurveIndex())
            return cmdPosition.add(cmdOffset.scale(key.getCardIndex()));
        else return _mplayerManager.getPosition(key, access);
    }

    private void addWord(Player user, String currentWord)
    {
        if (!currentWords.containsKey(user.getUserId()))
        {
            currentWords.put(user.getUserId(), new ArrayList<String>());
        }
        currentWords.get(user.getUserId()).add(currentWord);
    }

    private int getScore(final Participant p)
    {
        int thisScore = 0;
        for (final String s : getWordList(p.getId()))
        {
            thisScore += getWordScore(s);
        }
        return thisScore;
    }

    private String getSpellingBuffer(final String userId)
    {
        if (!spellingBuffer.containsKey(userId)) return "";
        return spellingBuffer.get(userId);
    }

    private ArrayList<String> getWordList(final String string)
    {
        if (!currentWords.containsKey(string))
        {
            currentWords.put(string, new ArrayList<String>());
        }
        return new ArrayList<String>(currentWords.get(string));
    }

    private int getWordScore(final String s)
    {
        return s.length();
    }

    public boolean isInPlay()
    {
        return _currentState == GameState.Playing;
    }

    public void maybeComplete() throws com.sawdust.engine.common.GameException
    {
        boolean isEveryoneDone = true;
        for (final Participant p : _mplayerManager.getPlayerManager().getPlayers())
        {
            if (getPlayerStatus(p.getId()) == PlayerState.Playing)
            {
                isEveryoneDone = false;
                break;
            }
        }
        final boolean isTimeUp = _roundEndTime.before(new Date());
        if (!isEveryoneDone && !isTimeUp) return;
        if (isEveryoneDone)
        {
            addMessage("Everyone is done!");
        }
        if (isTimeUp)
        {
            addMessage("Time is up!");
        }
        int winningScore = -1;
        Participant winner = null;
        for (final Participant p : _mplayerManager.getPlayerManager().getPlayers())
        {
            final int thisScore = getScore(p);
            if (thisScore > winningScore)
            {
                winningScore = thisScore;
                winner = p;
            }
            addMessage("%s's score: %d", displayName(p), thisScore);
        }
        if (isEveryoneDone || isTimeUp)
        {
            _currentState = GameState.Complete;
            addMessage("<strong>%s won</strong>", displayName(winner));
            final GameSession session = getSession();
            final ArrayList<Player> collection = new ArrayList<Player>();
            if (winner instanceof Player)
            {
                collection.add((Player) winner);
            }
            session.payOut(collection);
        }
    }

    private ArrayList<ArrayPosition> randomPositions(final TokenArray a)
    {
        final ArrayList<ArrayPosition> pos = a.getAllPositions();
        Collections.sort(pos, new Comparator<ArrayPosition>()
        {
            public int compare(final ArrayPosition o1, final ArrayPosition o2)
            {
                return (Math.random() < 0.5) ? -1 : 1;
            }
        });
        return pos;
    }

    @Override
    public void reset()
    {
        // TODO Auto-generated method stub
    }

    private void setSpellingBuffer(final String userId, final String string)
    {
        spellingBuffer.put(userId, string);
    }

    @Override
    public void start() throws com.sawdust.engine.common.GameException
    {
        final GameSession session = getSession();
        session.anteUp();
        for (final Participant p : _mplayerManager.getPlayerManager().getPlayers())
        {
            if (p instanceof Agent<?>)
            {
                session.withdraw(-session.getAnte(), null, "Agent Ante Up");
            }
        }

        playerStatus.clear();
        currentWords.clear();
        currentPath.clear();
        _currentState = GameState.Playing;
        _roundEndTime = new Date(new Date().getTime() + (1000 * 60 * 5));

        clearTokens();
        final int chainSize = 3;
        final MarkovPredictor mk = getMarkov(chainSize);

        final TokenArray a = new TokenArray(NUM_ROWS, NUM_COLS);
        final ArrayList<ArrayPosition> pos = randomPositions(a);
        for (final ArrayPosition p : pos)
        {
            final BoardToken token = generateToken(chainSize, mk, a, p);
            add(token);
        }
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
        if (!_mplayerManager.getPlayerManager().isMember(access))
        {
            Notification notification = new Notification();
            notification.notifyText = "You are currently observing this game.";
            notification.add("Join Table", "Join Game");
            returnValue.setNotification(notification);
        }
        else if (!isInPlay())
        {
            Notification notification = new Notification();
            notification.notifyText = "No game is currently in progress";
            notification.add("Leave Table", "Leave Game");
            returnValue.setNotification(notification);
        }
        return returnValue;
    }

	@Override
	public Participant getCurrentPlayer() {
		return _mplayerManager.getPlayerManager().getCurrentPlayer();
	}

    protected void setCurrentPath(HashMap<String, ArrayList<IndexPosition>> currentPath)
    {
        this.currentPath = currentPath;
    }

    protected HashMap<String, ArrayList<IndexPosition>> getCurrentPath()
    {
        return currentPath;
    }

    protected void setCurrentWords(HashMap<String, ArrayList<String>> currentWords)
    {
        this.currentWords = currentWords;
    }

    protected HashMap<String, ArrayList<String>> getCurrentWords()
    {
        return currentWords;
    }

    protected void setPlayerStatus(HashMap<String, PlayerState> playerStatus)
    {
        this.playerStatus = playerStatus;
    }

    protected HashMap<String, PlayerState> getPlayerStatus()
    {
        return playerStatus;
    }

    public void setSpellingBuffer(HashMap<String, String> spellingBuffer)
    {
        this.spellingBuffer = spellingBuffer;
    }

    public HashMap<String, String> getSpellingBuffer()
    {
        return spellingBuffer;
    }
    
    @Override
    public void removeMember(Participant agent) throws GameException
    {
        super.removeMember(agent);
        _mplayerManager.addMember(this, agent);
    }

    private LanguageProvider _language;
    
    public boolean verifyWord(String word)
    {
        return getLanguage().verifyWord(word);
    }
    
    public String getUrl(String urlString)
    {
        return getLanguage().getUrl(urlString);
    }

    public void setLanguage(LanguageProvider _language)
    {
        this._language = _language;
    }

    public LanguageProvider getLanguage()
    {
        return _language;
    }
    
}
