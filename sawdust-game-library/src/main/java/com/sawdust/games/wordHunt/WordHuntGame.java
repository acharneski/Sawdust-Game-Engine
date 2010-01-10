package com.sawdust.games.wordHunt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import com.sawdust.engine.controller.HttpInterface;
import com.sawdust.engine.controller.HttpResponse;
import com.sawdust.engine.controller.LanguageProvider;
import com.sawdust.engine.controller.MarkovPredictor;
import com.sawdust.engine.controller.PromotionConfig;
import com.sawdust.engine.controller.entities.Account;
import com.sawdust.engine.controller.entities.GameSession;
import com.sawdust.engine.controller.entities.Promotion;
import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.GameType;
import com.sawdust.engine.model.basetypes.PersistantTokenGame;
import com.sawdust.engine.model.players.ActivityEvent;
import com.sawdust.engine.model.players.Agent;
import com.sawdust.engine.model.players.MultiPlayer;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.players.Player;
import com.sawdust.engine.model.state.GameCommand;
import com.sawdust.engine.model.state.GameLabel;
import com.sawdust.engine.model.state.IndexPosition;
import com.sawdust.engine.model.state.Token;
import com.sawdust.engine.view.config.GameConfig;
import com.sawdust.engine.view.config.PropertyConfig;
import com.sawdust.engine.view.game.GameFrame;
import com.sawdust.engine.view.game.Notification;
import com.sawdust.engine.view.game.SolidColorGameCanvas;
import com.sawdust.engine.view.geometry.Position;
import com.sawdust.engine.view.geometry.Vector;
import com.sawdust.games.go.GoLoot;
import com.sawdust.games.stop.StopGame.GamePhase;
import com.sawdust.games.wordHunt.TokenArray.ArrayPosition;

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
    private HttpInterface httpInterface = null;
    private TokenArray tokenArray;
    private static final int chainSize = 3;


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
        setCanvas(new SolidColorGameCanvas("black","white"));
        int numberOfPlayers = getNumberOfPlayers(config);
        _mplayerManager = new MultiPlayer(numberOfPlayers);
        getSession().setMinimumPlayers(numberOfPlayers);
    }

    @Override
    public void doAddPlayer(final Participant agent) throws GameException
    {
        super.doAddPlayer(agent);
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

    private BoardToken generateToken(final int chainSize, final MarkovPredictor mk, final TokenArray a, final IndexPosition p)
    {
        return generateToken(chainSize, mk, a, a.new ArrayPosition(p.getCurveIndex(), p.getCardIndex()));
    }

    private BoardToken generateToken(final int chainSize, final MarkovPredictor mk, final TokenArray a, final ArrayPosition p)
    {
        // Generate raw ideas
        final HashMap<String, Integer> ideas = new HashMap<String, Integer>();
        final HashMap<String, Double> nearbyStuff = new HashMap<String, Double>();
        for (final ArrayList<ArrayPosition> na : p.getNeighborChain(chainSize))
        {
            final StringBuilder state = new StringBuilder();
            for (final ArrayPosition n : na)
            {
                String c = a.get(n).state;
                state.append(c);
                Double v = 0.0;
                if (nearbyStuff.containsKey(c))
                {
                    v = nearbyStuff.get(c);
                }
                nearbyStuff.put(c, ++v);
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
            if (nearbyStuff.containsKey(rand))
            {
                v /= nearbyStuff.get(rand);
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
            arrayList.add(new GameLabel("TimeLeft", new IndexPosition(CURVE_CMDS, idx++), "<TIMER>" + Integer.toString((int) Math.ceil(timeLeft))
                    + "</TIMER> sec left"));

            final String txt = String.format("My Score: %d (%d)", getScore(access), getWordList(access.getUserId()).size());
            arrayList.add(new GameLabel("WordCount", new IndexPosition(CURVE_CMDS, idx++), txt));

            for (final Participant p : _mplayerManager.getPlayerManager().getPlayers())
            {
                if (p.equals(access))
                {
                    continue;
                }
                final String txt2 = String.format("%s: %d (%d)", getDisplayName(p), getScore(access), getWordList(p.getId()).size());
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
            arrayList.add(new GameCommand()
            {

                @Override
                public boolean doCommand(Participant p, String commandText) throws GameException
                {
                    ArrayList<ArrayList<IndexPosition>> paths = findWord(p, commandText);
                    if(null == paths) return false;
                    currentPath.put(p.getId(), paths.get(0));
                    enterWord(p,commandText);
                    return true;
                    
                }

                @Override
                public String getCommandText()
                {
                    return ""; // Filter everything
                }

                @Override
                public String getHelpText()
                {
                    return "";
                }
            });
            
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
                            public boolean doCommand(Participant p, String commandText) throws GameException
                            {
                                Player user = (Player) p;
                                if ((null != prevPosition) && !adjacent(position, prevPosition))
                                {
                                    WordHuntGame.this.doAddMessage("Non-adjacent letter selected: " + token.letter).setTo(user.getUserId());
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
                public boolean doCommand(Participant p, String commandText) throws GameException
                {
                    Player user = (Player) p;
                    GameSession game = WordHuntGame.this.getSession();
                    final String currentWord = getSpellingBuffer(user.getUserId());
                    enterWord(user, currentWord);
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
                public boolean doCommand(Participant p, String commandText) throws GameException
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
                public boolean doCommand(Participant p, String commandText) throws GameException
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


    @Override
    public String renderBasicHtml()
    {
        StringBuilder sb = new StringBuilder();
        final HashMap<IndexPosition, Token> tokenIndexByPosition = getTokenIndexByPosition();
        sb.append("<table>");
        for (int i = 0; i < NUM_ROWS; i++)
        {
            sb.append("<tr>");
            for (int j = 0; j < NUM_COLS; j++)
            {
                sb.append("<td>");
                final IndexPosition position = new IndexPosition(i, j);
                if (tokenIndexByPosition.containsKey(position))
                {
                    final BoardToken token = (BoardToken) tokenIndexByPosition.get(position);
                    sb.append(token.letter);
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

    private ArrayList<ArrayList<IndexPosition>> findWord(Participant p, String commandText)
    {
        commandText = _language.normalizeString(commandText);
        if(commandText.contains(" ")) return null;
        final HashMap<IndexPosition, Token> tokenIndexByPosition = getTokenIndexByPosition();
        HashMap<String, ArrayList<IndexPosition>> index = new HashMap<String, ArrayList<IndexPosition>>();
        HashMap<IndexPosition,String> rindex = new HashMap<IndexPosition, String>();
        for(Entry<IndexPosition, Token> entry : tokenIndexByPosition.entrySet())
        {
            BoardToken value = (BoardToken) entry.getValue();
            rindex.put(entry.getKey(), value.letter);

            if(!index.containsKey(value.letter)) index.put(value.letter, new ArrayList<IndexPosition>());
            index.get(value.letter).add(entry.getKey());

        }
        ArrayList<ArrayList<IndexPosition>> paths = new ArrayList<ArrayList<IndexPosition>>();
        boolean isFirstLetter = true;
        for(String letter : _language.tokens(commandText))
        {
            ArrayList<IndexPosition> matches = index.get(letter);
            if(null == matches) return null;
            ArrayList<ArrayList<IndexPosition>> newpaths = new ArrayList<ArrayList<IndexPosition>>();
            for(IndexPosition match : matches)
            {
                
                if(isFirstLetter)
                {
                    ArrayList<IndexPosition> a = new ArrayList<IndexPosition>();
                    a.add(match);
                    newpaths.add(a);
                }
                else
                {
                    for(ArrayList<IndexPosition> path : paths)
                    {
                        IndexPosition indexPosition = path.get(path.size()-1);
                        if(match.is2dAdjacentTo(indexPosition) && !path.contains(match))
                        {
                            path = new ArrayList<IndexPosition>(path);
                            path.add(match);
                            newpaths.add(path);
                        }
                    }
                }
            }
            if(newpaths.size()==0)
            {
                return null;
            }
            paths = newpaths;
            isFirstLetter = false;
        }
        if(paths.size()==0)
        {
            return null;
        }
        return paths;
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

    private void addWord(Participant p, ArrayList<IndexPosition> currentWord)
    {
        String word = "";
        HashMap<IndexPosition, Token> tokenIndexByPosition = getTokenIndexByPosition();
        for (IndexPosition l : currentWord)
        {
            final BoardToken token = (BoardToken) tokenIndexByPosition.get(l);
            word += token.letter;
        }
        currentPath.put(p.getId(), currentWord);
        currentWords.get(p.getId()).add(word);
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

    public void maybeComplete() throws GameException
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
            doAddMessage("Everyone is done!");
        }
        if (isTimeUp)
        {
            doAddMessage("Time is up!");
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
            doAddMessage("%s's score: %d", getDisplayName(p), thisScore);
        }
        if (isEveryoneDone || isTimeUp)
        {
            _currentState = GameState.Complete;
            doAddMessage("<strong>%s won</strong>", getDisplayName(winner));
            final GameSession session = getSession();
            final ArrayList<Player> collection = new ArrayList<Player>();
            if (winner instanceof Player)
            {
                rollForLoot(winner);
                
                String type = "Win/WordHunt";
                String event = String.format("I won a game of WordHunt!");
                ((Player) winner).logActivity(new ActivityEvent(type, event));
                collection.add((Player) winner);
            }
            session.doSplitWagerPool(collection);
        }
    }


    private void rollForLoot(Participant p) throws GameException
    {
        Account account = ((Player) p).loadAccount();
        WordHuntLoot resource = account.getResource(WordHuntLoot.class);
        if(null == resource)
        {
            resource = new WordHuntLoot();
        }
        PromotionConfig promoConfig = resource.getLoot();
        if(null != promoConfig)
        {
            Promotion awardPromotion = account.doAwardPromotion(promoConfig);
            addMessage(awardPromotion.getMessage()).setTo(p.getId());
        }
        account.setResource(WordHuntLoot.class, resource);
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
    public void doReset()
    {
        // TODO Auto-generated method stub
    }

    private void setSpellingBuffer(final String userId, final String string)
    {
        spellingBuffer.put(userId, string);
    }

    @Override
    public void doStart() throws GameException
    {
        final GameSession session = getSession();
        session.doUnitWager();
        for (final Participant p : _mplayerManager.getPlayerManager().getPlayers())
        {
            if (p instanceof Agent<?>)
            {
                session.withdraw(-session.getUnitWager(), null, "Agent Ante Up");
            }
        }

        playerStatus.clear();
        currentWords.clear();
        currentPath.clear();
        _currentState = GameState.Playing;
        _roundEndTime = new Date(new Date().getTime() + (1000 * 60 * 5));

        clearTokens();
        tokenArray = new TokenArray(NUM_ROWS, NUM_COLS);

        final ArrayList<ArrayPosition> pos = randomPositions(tokenArray);
        for (final ArrayPosition p : pos)
        {
            final BoardToken token = generateToken(chainSize, getMarkovChain(), tokenArray, p);
            add(token);
        }
    }

    @Override
    public void doUpdate() throws GameException
    {
        _mplayerManager.update(this);
    }

    @Override
    public com.sawdust.engine.view.game.GameFrame getView(Player access) throws GameException
    {
        final com.sawdust.engine.view.game.GameFrame returnValue = super.getView(access);
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
    public Participant getCurrentPlayer()
    {
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
    public void doRemoveMember(Participant agent) throws GameException
    {
        super.doRemoveMember(agent);
        _mplayerManager.addMember(this, agent);
    }

    private LanguageProvider _language;

    public boolean verifyWord(String word)
    {
        return getLanguage().verifyWord(word, httpInterface);
    }

    public String getUrl(String urlString)
    {
        HttpInterface xface = getHttpInterface();
        HttpResponse url = xface.getURL(urlString);
        String content = url.getContent();
        return content;
    }

    public void setLanguage(LanguageProvider _language)
    {
        this._language = _language;
    }

    public LanguageProvider getLanguage()
    {
        return _language;
    }

    public void setHttpInterface(HttpInterface httpInterface)
    {
        this.httpInterface = httpInterface;
    }

    public HttpInterface getHttpInterface()
    {
        return httpInterface;
    }

    protected void attemptWord(Player user, final String currentWord) throws GameException
    {
    }

    protected void enterWord(Participant p, final String currentWord) throws GameException
    {
        final ArrayList<String> wordList = getWordList(p.getId());
        final ArrayList<IndexPosition> wordPath = getPath(p.getId());
        if (wordList.contains(currentWord))
        {
            WordHuntGame.this.doAddMessage("Already Entered: " + currentWord).setTo(p.getId());
            WordHuntGame.this.saveState();
        }
        else if (WordHuntGame.this.verifyWord(currentWord))
        {
            WordHuntGame.this.doAddMessage(String.format("%s spelled %s (%d points)", getDisplayName(p), currentWord, getWordScore(currentWord)));
            addWord(p, wordPath);
            explodeWord(p, wordPath);
        }
        else
        {
            WordHuntGame.this.doAddMessage("Rejected: " + currentWord).setTo(p.getId());
        }
        clearCurrentWord(p.getId());
        maybeComplete();
        WordHuntGame.this.saveState();
    }

    private void explodeWord(Participant p, ArrayList<IndexPosition> wordPath)
    {
        HashMap<IndexPosition, Token> tokenIndexByPosition = getTokenIndexByPosition();
        for (IndexPosition l : wordPath)
        {
            final BoardToken token = (BoardToken) tokenIndexByPosition.get(l);
            final BoardToken newToken = generateToken(chainSize, getMarkovChain(), tokenArray, l);
            removeToken(newToken);
            add(newToken);
        }
    }

    private transient MarkovPredictor _markovChain;
    
    private void setMarkovChain(MarkovPredictor markovChain)
    {
        _markovChain = markovChain;
        getSession().setResource(MarkovPredictor.class, markovChain);
    }

    private MarkovPredictor getMarkovChain()
    {
        if(null != _markovChain) return _markovChain;
        _markovChain = getSession().getResource(MarkovPredictor.class);
        if(null == _markovChain) 
        {
            setMarkovChain(createMarkovChain());
        }
        return _markovChain;
    }

    private MarkovPredictor createMarkovChain()
    {
        final int refreshMs = 1000 * 60 * 60;
        final String uniqueTime = Integer.toString((int) (new Date().getTime() / refreshMs));
        final MarkovPredictor mk = new MarkovPredictor(chainSize + 1, _language);
        final ArrayList<String> trainingData = new ArrayList<String>();
        trainingData.add("When in the Course of human events it becomes necessary for one people to dissolve the political bands which have connected them with another and to assume among the powers of the earth, the separate and equal station to which the Laws of Nature and of Nature's God entitle them, a decent respect to the opinions of mankind requires that they should declare the causes which impel them to the separation. We hold these truths to be self-evident, that all men are created equal, that they are endowed by their Creator with certain unalienable Rights, that among these are Life, Liberty and the pursuit of Happiness. — That to secure these rights, Governments are instituted among Men, deriving their just powers from the consent of the governed, — That whenever any Form of Government becomes destructive of these ends, it is the Right of the People to alter or to abolish it, and to institute new Government, laying its foundation on such principles and organizing its powers in such form, as to them shall seem most likely to effect their Safety and Happiness. Prudence, indeed, will dictate that Governments long established should not be changed for light and transient causes; and accordingly all experience hath shewn that mankind are more disposed to suffer, while evils are sufferable than to right themselves by abolishing the forms to which they are accustomed. But when a long train of abuses and usurpations, pursuing invariably the same Object evinces a design to reduce them under absolute Despotism, it is their right, it is their duty, to throw off such Government, and to provide new Guards for their future security. — Such has been the patient sufferance of these Colonies; and such is now the necessity which constrains them to alter their former Systems of Government. The history of the present King of Great Britain is a history of repeated injuries and usurpations, all having in direct object the establishment of an absolute Tyranny over these States. To prove this, let Facts be submitted to a candid world. He has refused his Assent to Laws, the most wholesome and necessary for the public good. He has forbidden his Governors to pass Laws of immediate and pressing importance, unless suspended in their operation till his Assent should be obtained; and when so suspended, he has utterly neglected to attend to them. He has refused to pass other Laws for the accommodation of large districts of people, unless those people would relinquish the right of Representation in the Legislature, a right inestimable to them and formidable to tyrants only. He has called together legislative bodies at places unusual, uncomfortable, and distant from the depository of their Public Records, for the sole purpose of fatiguing them into compliance with his measures. He has dissolved Representative Houses repeatedly, for opposing with manly firmness his invasions on the rights of the people. He has refused for a long time, after such dissolutions, to cause others to be elected, whereby the Legislative Powers, incapable of Annihilation, have returned to the People at large for their exercise; the State remaining in the mean time exposed to all the dangers of invasion from without, and convulsions within. He has endeavoured to prevent the population of these States; for that purpose obstructing the Laws for Naturalization of Foreigners; refusing to pass others to encourage their migrations hither, and raising the conditions of new Appropriations of Lands. He has obstructed the Administration of Justice by refusing his Assent to Laws for establishing Judiciary Powers. He has made Judges dependent on his Will alone for the tenure of their offices, and the amount and payment of their salaries. He has erected a multitude of New Offices, and sent hither swarms of Officers to harass our people and eat out their substance. He has kept among us, in times of peace, Standing Armies without the Consent of our legislatures. He has affected to render the Military independent of and superior to the Civil Power. He has combined with others to subject us to a jurisdiction foreign to our constitution, and unacknowledged by our laws; giving his Assent to their Acts of pretended Legislation: For quartering large bodies of armed troops among us: For protecting them, by a mock Trial from punishment for any Murders which they should commit on the Inhabitants of these States: For cutting off our Trade with all parts of the world: For imposing Taxes on us without our Consent: For depriving us in many cases, of the benefit of Trial by Jury: For transporting us beyond Seas to be tried for pretended offences: For abolishing the free System of English Laws in a neighbouring Province, establishing therein an Arbitrary government, and enlarging its Boundaries so as to render it at once an example and fit instrument for introducing the same absolute rule into these Colonies For taking away our Charters, abolishing our most valuable Laws and altering fundamentally the Forms of our Governments: For suspending our own Legislatures, and declaring themselves invested with power to legislate for us in all cases whatsoever. He has abdicated Government here, by declaring us out of his Protection and waging War against us. He has plundered our seas, ravaged our coasts, burnt our towns, and destroyed the lives of our people. He is at this time transporting large Armies of foreign Mercenaries to compleat the works of death, desolation, and tyranny, already begun with circumstances of Cruelty & Perfidy scarcely paralleled in the most barbarous ages, and totally unworthy the Head of a civilized nation. He has constrained our fellow Citizens taken Captive on the high Seas to bear Arms against their Country, to become the executioners of their friends and Brethren, or to fall themselves by their Hands. He has excited domestic insurrections amongst us, and has endeavoured to bring on the inhabitants of our frontiers, the merciless Indian Savages whose known rule of warfare, is an undistinguished destruction of all ages, sexes and conditions. In every stage of these Oppressions We have Petitioned for Redress in the most humble terms: Our repeated Petitions have been answered only by repeated injury. A Prince, whose character is thus marked by every act which may define a Tyrant, is unfit to be the ruler of a free people. Nor have We been wanting in attentions to our British brethren. We have warned them from time to time of attempts by their legislature to extend an unwarrantable jurisdiction over us. We have reminded them of the circumstances of our emigration and settlement here. We have appealed to their native justice and magnanimity, and we have conjured them by the ties of our common kindred to disavow these usurpations, which would inevitably interrupt our connections and correspondence. They too have been deaf to the voice of justice and of consanguinity. We must, therefore, acquiesce in the necessity, which denounces our Separation, and hold them, as we hold the rest of mankind, Enemies in War, in Peace Friends. We, therefore, the Representatives of the united States of America, in General Congress, Assembled, appealing to the Supreme Judge of the world for the rectitude of our intentions, do, in the Name, and by Authority of the good People of these Colonies, solemnly publish and declare, That these united Colonies are, and of Right ought to be Free and Independent States, that they are Absolved from all Allegiance to the British Crown, and that all political connection between them and the State of Great Britain, is and ought to be totally dissolved; and that as Free and Independent States, they have full Power to levy War, conclude Peace, contract Alliances, establish Commerce, and to do all other Acts and Things which Independent States may of right do. — And for the support of this Declaration, with a firm reliance on the protection of Divine Providence, we mutually pledge to each other our Lives, our Fortunes, and our sacred Honor.");
        trainingData.add(getUrl("http://news.google.com/#" + uniqueTime));
        trainingData.add(getUrl("http://slashdot.org/#" + uniqueTime));
        for (final String latestNews : trainingData)
        {
            if (null == latestNews)
            {
                continue;
            }
            learnWords(mk, latestNews);
        }
        return mk;
    }

    private void learnWords(final MarkovPredictor mk, final String latestNews)
    {
        String textToLearn = latestNews.toUpperCase().replaceAll("</?[^>]+>", " ").replaceAll("[^A-Z]", " ");
        for(String word : textToLearn.split(" "))
        {
            if(word.length() < 3) continue;
            mk.learn(word);
        }
    }

    public int getUpdateTime()
    {
        if (_currentState == GameState.Lobby)
        {
            return 15;
        }
        return _mplayerManager.isSinglePlayer()?90:5;
    }

}
