package com.sawdust.games.blackjack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Logger;

import com.sawdust.engine.controller.entities.GameSession;
import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.controller.exceptions.GameLogicException;
import com.sawdust.engine.model.GameType;
import com.sawdust.engine.model.basetypes.GameState;
import com.sawdust.engine.model.basetypes.IndexCardGame;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.players.Player;
import com.sawdust.engine.model.state.CommandResult;
import com.sawdust.engine.model.state.GameCommand;
import com.sawdust.engine.model.state.GameLabel;
import com.sawdust.engine.model.state.IndexCard;
import com.sawdust.engine.model.state.IndexPosition;
import com.sawdust.engine.model.state.Token;
import com.sawdust.engine.view.cards.Card;
import com.sawdust.engine.view.cards.Ranks;
import com.sawdust.engine.view.cards.Suits;
import com.sawdust.engine.view.config.GameConfig;
import com.sawdust.engine.view.game.GameFrame;
import com.sawdust.engine.view.game.Message;
import com.sawdust.engine.view.game.Message.MessageType;
import com.sawdust.engine.view.geometry.Position;
import com.sawdust.engine.view.geometry.Vector;

public abstract class BlackjackGame extends IndexCardGame
{
    private static final int DEALER_PAUSE_MS = 1000;
   private static final Logger LOG = Logger.getLogger(BlackjackGame.class.getName());

    enum GamePhases
    {
        Lost, Null, Playing, Won
    };
    
    static final boolean HACK_FORCE_PAIRS = false;
    public static final int HAND_COMMANDS = -4;
    public static final int HAND_HIT_COMMANDS = -5;
    public static final int HAND_DEALER = 1;
    public static final int HAND_PLAYER = 0;
    
    public static final Vector OFFSET_CARD = new Vector(25, 10);
    public static final Vector OFFSET_PLAYER_HANDS = new Vector(130, 0);
    public static final Vector OFFSET_COMMAND = new Vector(0, 30);
    public static final Vector OFFSET_HIT_COMMAND = new Vector(20, -33);
    
    public static final Position POSITION_COMMAND = new Position(270, 10);
    public static final Position POSITION_DEALER = new Position(40, 10);
    public static final Position POSITION_PRIMARY = new Position(40, 170);
    
    private GamePhases _currentPhase = GamePhases.Null;
    private Participant _owner = null;
    
    protected BlackjackGame()
    {
        super();
    }
    
    public BlackjackGame(final GameConfig config)
    {
        super(config);
        this.getSession().setPlayerTimeout(900);
        if (config.getProperties().containsKey(GameConfig.RANDOM_SEED))
        {
            final String seed = config.getProperties().get(GameConfig.RANDOM_SEED).value;
            this.doAddMessage("Setting seed: %s", seed).setTo(Message.ADMIN);
            getDeck().setSeed(seed);
        }
        getSession().setMinimumPlayers(1);
    }
    
    @Override
    public GameState doAddPlayer(final Participant s) throws GameException
    {
        if (null != _owner) throw new GameLogicException(String.format("This game is already inhabited by %s", _owner));
        _owner = s;
        return this;
    }
    
    @Override
    public String getDisplayName(final Participant userId)
    {
        return "Player";
    }
    
    enum PlayerHandStatus
    {
        Null, Playing, Lost, Stay
    }
    
    HashMap<Integer, PlayerHandStatus> playerHandStatus = new HashMap<Integer, PlayerHandStatus>();
    
    public void doStay() throws GameException
    {
        if (GamePhases.Playing != _currentPhase) throw new GameLogicException("Game is not in progress");
        for (final Token newCard : getCurveCards(HAND_DEALER))
        {
            newCard.setPublic();
        }
        
        /*
         * R-B-0029 rules. If the dealer has less than 17, he must hit. If the
         * dealer has R-B-0030 17 or more, he must stand (take no more cards),
         * unless it is a "soft 17" R-B-0031 (a hand that includes an ace valued
         * as "11," for example a hand consisting R-B-0032 of Ace+6, or
         * Ace+2+4). With a soft 17, the dealer follows the casino rules
         * R-B-0033 printed on the blackjack table, either to "hit soft 17" or
         * to "stand on R-B-0034 all 17's."
         */
        int dealerScore = getScore(HAND_DEALER);
        this.doSaveState();
        this.doAdvanceTime(DEALER_PAUSE_MS);
        while (dealerScore < 17)
        {
            this.doAddMessage("Dealer has %d; dealer hits", dealerScore);
            dealerScore = hit(HAND_DEALER);
            this.doSaveState();
            this.doAdvanceTime(DEALER_PAUSE_MS);
        }
        this.doAddMessage("Dealer has %d", dealerScore);
        endTurn();
    }
    
    private void lose(int i, GameSession gameSession) throws GameException
    {
        
        this.doAddMessage("Player busts... ").setType(MessageType.Compact);
        boolean stillPlaying = false;
        int won = 0;
        int lost = 0;
        playerHandStatus.put(i, PlayerHandStatus.Lost);
        for (PlayerHandStatus status : playerHandStatus.values())
        {
            if (PlayerHandStatus.Playing == status) stillPlaying = true;
        }
        
        if (!stillPlaying)
        {
            doStay();
        }
    }
    
    private void endTurn() throws GameException
    {
        final GameSession gameSession = getSession();
        int dealerScore = getScore(HAND_DEALER);
        final boolean dealerBust = (dealerScore > 21);
        
        /*
         * R-B-0049 The payoff for a player blackjack is 3:2, meaning that the
         * casino pays R-B-0050 $3 for each $2 originally bet. (There are many
         * single-deck games which R-B-0051 pay only 6:5 for a blackjack.)
         * Blackjack pays the same as all wins, at 1:1.
         */
        if (dealerBust)
        {
            this.doAddMessage(MessageType.Compact, "Dealer busts... ");
            this.doAddMessage(MessageType.Compact, "<strong><i>Everyone Wins!</i></strong>");
            this.doAddMessage("");
            _currentPhase = GamePhases.Won;
            
            gameSession.doSplitWagerPool(gameSession.getPlayers());
            return;
        }
        else
        {
            double wins = 0;
            double losses = 0;
            for (int i : getPlayerCurves())
            {
                final int playerScore = getScore(i);
                if(-1 == playerScore) continue;
                final boolean playerBust = (playerScore > 21);
                final boolean playerWin = (playerScore > dealerScore);
                
                if (playerBust)
                {
                    losses += 1;
                }
                else if (playerWin)
                {
                    wins += 1;
                }
                else
                {
                    losses += 1;
                }
            }
            if (wins > 0 && losses > 0)
            {
                String msg = String.format("<strong><i>You won %d hands, and lost %d.</i></strong>", wins, losses);
                this.doAddMessage(msg );
                gameSession.doModifyWagerPool(wins/(wins+losses),msg);
                gameSession.doSplitWagerPool(gameSession.getPlayers());
                _currentPhase = GamePhases.Won;
            }
            else if (wins > 0)
            {
                this.doAddMessage("<strong><i>You Win.</i></strong>");
                gameSession.doSplitWagerPool(gameSession.getPlayers());
                _currentPhase = GamePhases.Won;
            }
            else if (losses > 0)
            {
                this.doAddMessage("<strong><i>You Lose.</i></strong>");
                gameSession.doSplitWagerPool(null);
                _currentPhase = GamePhases.Lost;
            }
            else
            {
                throw new RuntimeException();
            }
        }
    }
    
    private int getCardCount(final int playerIndex)
    {
        return getCurveCards(playerIndex).size();
    }
    
    @Override
    public GameType<BlackjackGame> getGameType()
    {
        return BlackjackGameType.INSTANCE;
    }
    
    @Override
    public String getKeywords()
    {
        return "Blackjack: gambling card game";
    }
    
    @Override
    public Collection<GameLabel> getLabels(final Player c)
    {
        final ArrayList<GameLabel> arrayList = new ArrayList<GameLabel>();
        GameLabel l;
        int labelNumber = 0;
        
        // HAND_HIT_COMMANDS
        
        if (GamePhases.Playing == _currentPhase)
        {
            for (int i : getPlayerCurves())
            {
                if (0 == getCurveCards(i).size()) continue;
                l = new GameLabel("CMD_HIT_" + i, new IndexPosition(HAND_HIT_COMMANDS, i), "Hit Me");
                l.setCommand("Hit " + i);
                arrayList.add(l);
            }
            
            l = new GameLabel("CMD_STAY", new IndexPosition(HAND_COMMANDS, labelNumber++), "Stay");
            l.setCommand("Stay");
            arrayList.add(l);
            
            if (Commands.DoubleDown.canDo(c, this))
            {
                l = new GameLabel("CMD_DD", new IndexPosition(HAND_COMMANDS, labelNumber++), "Double Down");
                l.setCommand("Double Down");
                arrayList.add(l);
            }
            
            if (Commands.SplitPair.canDo(c, this))
            {
                l = new GameLabel("CMD_SPLIT", new IndexPosition(HAND_COMMANDS, labelNumber++), "Split Pair");
                l.setCommand("Split Pair");
                arrayList.add(l);
            }
        }
        else
        {
            l = new GameLabel("CMD1", new IndexPosition(HAND_COMMANDS, labelNumber++), "Deal");
            l.setCommand("Deal");
            arrayList.add(l);
            
        }
        
        return arrayList;
    }
    
    private int[] getPlayerCurves()
    {
        return new int[]
        {
                HAND_PLAYER, HAND_DEALER + 1
        };
    }
    
    @Override
    public ArrayList<GameCommand> getMoves(final Participant access) throws GameException
    {
        /*
         * R-B-0055 After receiving his initial two cards, the player has four
         * standard options: R-B-0056 he can "Hit," "Stand," "Double Down," or
         * "Split a pair." Each option requires R-B-0057 the use of a hand
         * signal. At some casinos or tables, the player may have R-B-0058 a
         * fifth option called "Surrender." At this time, only hit and stand are
         * supported.
         */
        final ArrayList<GameCommand> returnValue = new ArrayList<GameCommand>();
        if (GamePhases.Playing == _currentPhase)
        {
            for (final Commands o : com.sawdust.games.blackjack.Commands.values())
            {
                if (!o.canDo(access, this)) continue;
                returnValue.add(new GameCommand()
                {
                    
                    @Override
                    public String getHelpText()
                    {
                        return o.getHelpText();
                    }
                    
                    @Override
                    public String getCommandText()
                    {
                        return o.getCommandText();
                    }
                    
                    @Override
                    public CommandResult doCommand(Participant p, String commandText) throws GameException
                    {
                        o.doCommand(p, BlackjackGame.this, "");
                        return new CommandResult<GameState>(BlackjackGame.this);
                    }
                });
            }
            for (final int i : getPlayerCurves())
            {
                if (0 == getCurveCards(i).size()) continue;
                returnValue.add(new GameCommand()
                {
                    public void doCommand(final Participant user, final GameSession gameSession, final String param)
                            throws GameException
                    {
                        BlackjackGame.this.doHit(i);
                        BlackjackGame.this.doSaveState();
                    }
                    
                    public String getCommandText()
                    {
                        return "Hit " + i;
                    }
                    
                    public String getHelpText()
                    {
                        return "Deals the Participant another card on deck " + i;
                    }
                    
                    @Override
                    public CommandResult doCommand(Participant p, String commandText) throws GameException
                    {
                        doCommand(p, BlackjackGame.this.getSession(), "");
                        return new CommandResult<GameState>(BlackjackGame.this);
                    }
                });
            }
        }
        return returnValue;
    }
    
    @Override
    public Position getPosition(final IndexPosition key, final Player access) throws GameException
    {
        // OFFSET_HIT_COMMAND
        if (key.getCurveIndex() == HAND_PLAYER)
        {
            Position p = POSITION_PRIMARY.add(OFFSET_CARD.scale(key.getCardIndex()));
            p.setZ(key.getCardIndex());
            return p;
        }
        else if (key.getCurveIndex() == HAND_HIT_COMMANDS)
        {
            Position p = getPosition(new IndexPosition(key.getCardIndex(), 0), access).add(OFFSET_HIT_COMMAND);
            p.setZ(1);
            return p;
        }
        else if (key.getCurveIndex() == HAND_DEALER)
        {
            Position p = POSITION_DEALER.add(OFFSET_CARD.scale(key.getCardIndex()));
            p.setZ(key.getCardIndex());
            return p;
        }
        else if (key.getCurveIndex() > HAND_DEALER)
        {
            int playerExtra = key.getCurveIndex() - HAND_DEALER;
            Position p = POSITION_PRIMARY.add(OFFSET_PLAYER_HANDS.scale(playerExtra)).add(OFFSET_CARD.scale(key.getCardIndex()));
            p.setZ(key.getCardIndex());
            return p;
        }
        else if (key.getCurveIndex() == HAND_COMMANDS)
        {
            return POSITION_COMMAND.add(OFFSET_COMMAND.scale(key.getCardIndex()));
        }
        else throw new GameLogicException(String.format("Unknown curve index: %d", key.getCurveIndex()));
    }
    
    private int getScore(final int playerIndex)
    {
        int totalScore = 0;
        int numberOfAces = 0;
        int numberOfCards = 0;
        for (final Token newCard : getCurveCards(playerIndex))
        {
            numberOfCards++;
            if (null != newCard)
            {
                if (((IndexCard) newCard).getCard().getRank() == Ranks.Ace)
                {
                    numberOfAces++;
                }
                totalScore += ((IndexCard) newCard).getCard().getRank().getRank();
            }
        }
        while ((totalScore > 21) && (numberOfAces > 0))
        {
            numberOfAces--;
            totalScore -= 10;
        }
        if(0 == numberOfCards) return -1;
        return totalScore;
    }
    
    public void doHit(int i) throws GameException
    {
        final GameSession gameSession = getSession();
        if (GamePhases.Playing != _currentPhase) throw new GameLogicException("Game is not in progress");
        final int totalScore = hit(i);
        this.doAddMessage("Player has %d", totalScore);
        if (totalScore > 21)
        {
            lose(i, gameSession);
        }
    }
    
    private int hit(final int playerIndex)
    {
        final int cardNumber = getCardCount(playerIndex);
        final IndexCard newCard = dealNewCard(new IndexPosition(playerIndex, cardNumber));
        newCard.setOwner(_owner);
        newCard.setPublic();
        final int totalScore = getScore(playerIndex);
        return totalScore;
    }
    
    @Override
    public boolean isInPlay()
    {
        return (GamePhases.Playing == _currentPhase);
    }
    
    @Override
    public GameState doReset()
    {
        clearTokens();
        _currentPhase = GamePhases.Null;
        return this;
    }
    
    @Override
    public GameState doStart() throws GameException
    {
        if (GamePhases.Playing != _currentPhase) 
        {
            this.doAddMessage("Clearing table and redealing game.");
        }
        /*
         * R-B-0047 The minimum and maximum bets are posted on the table. The
         * payoff on most R-B-0048 bets is 1:1, meaning that the player wins the
         * same amount as he bets. All bets are fixed at the ante level set at
         * the game beginning.
         */
        if (null != _owner)
        {
            getSession().doUnitWager();
            getSession().withdraw(-getSession().getBalance(), null, "House contribution");
        }
        this.doAddMessage(MessageType.Compact, "New Deal: ");
        playerHandStatus.clear();
        clearTokens();
        getDeck().setReshuffleEnabled(true); // Card counting is part of the fun
        _currentPhase = GamePhases.Playing;
        
        /*
         * R-B-0001 In casino blackjack, the dealer faces one to seven players
         * from behind R-B-0002 a kidney-shaped table. Each player plays his
         * hand independently against R-B-0003 the dealer. At the beginning of
         * each round, the player places a bet in R-B-0004 the "betting box" and
         * receives an initial hand of two cards. The object
         */

        /*
         * R-B-0020 Cards are dealt in three ways, either from one or two
         * hand-held decks, R-B-0021 from a box containing four to eight decks
         * called a "shoe," or from a shuffling R-B-0022 machine. When dealt by
         * hand, the player's two initial cards are face-down, R-B-0023 while
         * the dealer has one face-up card called the "upcard" and one face-down
         * R-B-0024 card called the "hole card." (In European blackjack, the
         * dealer's hole R-B-0025 card is not actually dealt until the players
         * all play their hands.) When R-B-0026 dealt from a shoe, all player
         * cards are normally dealt face-up, with minor R-B-0027 exceptions. It
         * shouldn't matter to the player whether his cards are dealt R-B-0028
         * face-down or face-up since the dealer must play according to
         * predetermined R-B-0029 rules. If the dealer has less than 17, he must
         * hit. If the dealer has
         */
        playerHandStatus.put(0, PlayerHandStatus.Playing);
        for (int i = 0; i < 2; i++)
        {
            int totalScore = 0;
            com.sawdust.engine.view.cards.Card showingCard = null;
            Ranks hackRank = null;
            for (int cardNumber = 0; cardNumber < 2; cardNumber++)
            {
                final IndexCard newCard;
                final Card card;
                IndexPosition cardPosition = new IndexPosition(i, cardNumber);
                if (null != hackRank)
                {
                    Card hackedCard = new Card(hackRank, Suits.Spades, 1);
                    newCard = new IndexCard(++cardIdCounter, null, "VR", false, cardPosition, hackedCard);
                    card = newCard.getCard();
                    add(newCard);
                }
                else
                {
                    newCard = dealNewCard(cardPosition);
                    card = newCard.getCard();
                    if (HACK_FORCE_PAIRS)
                    {
                        hackRank = card.getRank();
                    }
                }
                newCard.setOwner((HAND_PLAYER == i) ? _owner : null);
                if (cardNumber == 0)
                {
                    newCard.setPrivate("VR");
                }
                else
                {
                    newCard.setPublic();
                }
                if(null != card)
                {
                    totalScore += card.getRank().getRank();
                    showingCard = card;
                }
                else
                {
                    LOG.warning("null == card");
                }
            }
            if (HAND_PLAYER == i)
            {
                this.doAddMessage(MessageType.Compact, "Player has %d. ", totalScore);
            }
            else
            {
                if (totalScore == 21)
                {
                    // this.addMessage(MessageType.Compact, "Dealer has %s. ",
                    // showingCard.toString());
                    this.doAddMessage(MessageType.Compact, "Dealer has Blackjack.");
                }
                else
                {
                    this.doAddMessage(MessageType.Compact, "Dealer has %s. ", showingCard.toString());
                    this.doAddMessage(MessageType.Compact, "Dealer does not have Blackjack.");
                }
            }
        }
        return this;
        
    }
    
    @Override
    public GameFrame getView(final Player access) throws GameException
    {
        final GameFrame gwt = super.getView(access);
        gwt.setHeight(300);
        gwt.setWidth(400);
        return gwt;
    }
    
    @Override
    public GameState doUpdate() throws GameException
    {
        return this;
    }
    
    @Override
    public Participant getCurrentPlayer()
    {
        return _owner;
    }

    public void setCurrentPhase(GamePhases _currentPhase)
    {
        this._currentPhase = _currentPhase;
    }

    public GamePhases getCurrentPhase()
    {
        return _currentPhase;
    }

    public int getUpdateTime()
    {
        return 90;
    }
}
