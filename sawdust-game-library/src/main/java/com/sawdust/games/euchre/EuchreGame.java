package com.sawdust.games.euchre;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.sawdust.engine.controller.entities.GameSession;
import com.sawdust.engine.controller.entities.GameSession.SessionStatus;
import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.controller.exceptions.GameLogicException;
import com.sawdust.engine.controller.exceptions.InputException;
import com.sawdust.engine.controller.exceptions.SawdustSystemError;
import com.sawdust.engine.model.AgentFactory;
import com.sawdust.engine.model.ComparableList;
import com.sawdust.engine.model.GameModification;
import com.sawdust.engine.model.GameType;
import com.sawdust.engine.model.basetypes.GameState;
import com.sawdust.engine.model.basetypes.MultiPlayerCardGame;
import com.sawdust.engine.model.players.Agent;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.players.Player;
import com.sawdust.engine.model.state.GameCommand;
import com.sawdust.engine.model.state.GameLabel;
import com.sawdust.engine.model.state.IndexCard;
import com.sawdust.engine.model.state.IndexPosition;
import com.sawdust.engine.model.state.Token;
import com.sawdust.engine.view.cards.Card;
import com.sawdust.engine.view.cards.CardDeck;
import com.sawdust.engine.view.cards.Ranks;
import com.sawdust.engine.view.cards.Suits;
import com.sawdust.engine.view.config.GameConfig;
import com.sawdust.engine.view.config.GameModConfig;
import com.sawdust.engine.view.geometry.Position;
import com.sawdust.games.euchre.ai.Normal1;
import com.sawdust.games.euchre.ai.Stupid1;
import com.sawdust.games.euchre.mod.Mod78;

public abstract class EuchreGame extends MultiPlayerCardGame
{
    public static final GamePhase COMPLETE = CompletePhase.INSTANCE;
    public static final GamePhase DEALING = DealingPhase.INSTANCE;
    public static final GamePhase FORMING = FormingPhase.INSTANCE;
    public static final GamePhase INITIAL_MAKING = InitialMakingPhase.INSTANCE;
    public static final int NUMBER_OF_CARDS = 5;
    public static final int NUMBER_OF_PLAYERS = 4;

    public static final int NUMBER_OF_POINTS = 5;
    public static final GamePhase OPEN_MAKING = OpenMakingPhase.INSTANCE;
    public static final GamePhase PLAYING = PlayingPhase.INSTANCE;

    private GamePhase _currentPhase = FORMING;
    protected EuchreLayout _layout;
    protected Participant _maker = null;
    protected int _roundCardCount = 0;
    protected int _roundNumber = 0;
    protected int _totalPts = 0;
    protected Participant _roundStartPlayer = null;
    
    private HashMap<Integer, TeamStatus> _teamStatus = new HashMap<Integer, TeamStatus>();
    public TeamStatus getTeamStatus(int n)
    {
        if(!_teamStatus.containsKey(n)) _teamStatus.put(n, new TeamStatus());
        return _teamStatus.get(n);
    }
    
    protected Suits _trumpSuit = Suits.Hearts;
    protected IndexCard _winningCard = null;
    private int _pointGoal = 5;

    protected EuchreGame()
    {
        super(NUMBER_OF_PLAYERS);
        _layout = new EuchreLayout(NUMBER_OF_PLAYERS, NUMBER_OF_CARDS);
        CardDeck deck = getDeck();
        setDeck(new EuchreDeck());
        getDeck().setSeed(deck.getSeed());
    }

    public EuchreGame(final GameConfig config)
    {
        super(NUMBER_OF_PLAYERS, config);
        _layout = new EuchreLayout(NUMBER_OF_PLAYERS, NUMBER_OF_CARDS);
        CardDeck deck = getDeck();
        setDeck(new EuchreDeck());
        getDeck().setSeed(deck.getSeed());
        
        setTimeoutAgent(new Stupid1("Timeout"));
        initializeModules();
    }

    @Override
    protected void addNewModule(final GameModConfig x)
    {
        final GameModification<EuchreGame> newModule = Mod78.getNewModule(x);
        if (null != newModule)
        {
            newModule.apply(this);
            return;
        }
    }

    protected void clearPlayedCards()
    {
        for (final Token card : getCurveCards(EuchreLayout.POS_IN_PLAY))
        {
            if (card.getPosition().getCardIndex() > NUMBER_OF_CARDS)
            {
                continue;
            }
            removeToken(card);
            getDeck().discard(((IndexCard) card).getCard());
        }
        _roundCardCount = 0;
    }

    public void doCommand(final EuchreCommand cmd, final Object... params) throws GameException
    {
        getCurrentPhase().doCommand(this, cmd, params);
    }

    @Override
    public List<AgentFactory<? extends Agent<?>>> getAgentFactories()
    {
        final List<AgentFactory<? extends Agent<?>>> agentFactories = super.getAgentFactories();
        agentFactories.add(new AgentFactory<Stupid1>()
        {

            @Override
            public Stupid1 getAgent(final String id)
            {
                return new Stupid1(id);
            }

            @Override
            public String getName()
            {
                return "Easy";
            }
        });
        agentFactories.add(new AgentFactory<Normal1>()
        {

            @Override
            public Normal1 getAgent(final String id)
            {
                return new Normal1(id);
            }

            @Override
            public String getName()
            {
                return "Normal";
            }
        });
        return agentFactories;
    }

    public ComparableList<Integer> getCardSortOrder(final IndexCard cardToPlay)
    {
        /*
         * R-E-0045: When a suit is named trump, any card of that suit outranks any card of a R-E-0046: non-trump suit. The highest ranking
         * card in euchre is the jack of the trump suit R-E-0047: and is referred to as the right bower, or simply the right. Next highest
         * is the R-E-0048: other jack of the same color, the left bower. The right and left may also be R-E-0049: known as the "jack" and
         * the "jick", the "right bauer" and "left bauer," or R-E-0050: "jack" and "off jack" respectively. Remaining cards of the trump
         * suit rank from R-E-0051: high to low as A, K, Q, 10, and 9.
         */
        final ComparableList<Integer> returnValue = new ComparableList<Integer>();
        final Suits leadingSuit = getLeadingSuit();

        boolean isTrumpSuit = _trumpSuit == cardToPlay.getCard().getSuit();
        final boolean isLeadingSuit = leadingSuit == cardToPlay.getCard().getSuit();
        final boolean isJack = Ranks.Jack == cardToPlay.getCard().getRank();
        final boolean isTrumpColor = _trumpSuit.color().equals(cardToPlay.getCard().getSuit().color());
        if (isJack && isTrumpColor)
        {
            isTrumpSuit = true;
        }

        if (isTrumpSuit)
        {
            returnValue.add(1);
        }
        else
        {
            if (isLeadingSuit)
            {
                returnValue.add(2);
            }
            else
            {
                returnValue.add(3);
            }
        }

        if (isJack)
        {
            if (isTrumpSuit)
            {
                returnValue.add(1);
            }
            else
            {
                if (isTrumpColor)
                {
                    returnValue.add(2);
                }
                else
                {
                    returnValue.add(3);
                }
            }
        }
        else
        {
            returnValue.add(3);
        }

        final ArrayList<Suits> suitOrder = getSuitOrder();
        returnValue.add(suitOrder.indexOf(cardToPlay.getCard().getSuit()));
        final ArrayList<Ranks> rankOrder = getRankOrder();
        returnValue.add(rankOrder.indexOf(cardToPlay.getCard().getRank()));
        return returnValue;
    }

    public GamePhase getCurrentPhase()
    {
        return _currentPhase;
    }

    public Participant getCurrentPlayer()
    {
        return getPlayerManager().getCurrentPlayer();
    }

    public int getCurrentWinningTeam() throws GameException
    {
        if (null == _winningCard) return -1;
        return getTeamNumber(_winningCard.getOwner());
    }

    public Suits getEffectiveSuit(final Card card)
    {
        if (null == card) return null;
        final Suits suit = card.getSuit();
        final boolean isJack = card.getRank().equals(Ranks.Jack);
        final boolean isCardTrumpColor = suit.color().equals(_trumpSuit.color());
        if (isCardTrumpColor && isJack) return _trumpSuit;
        return suit;
    }

    @Override
    public GameType<EuchreGame> getGameType()
    {
        return EuchreGameType.INSTANCE;
    }

    @Override
    public int getUpdateTime()
    {
        if (getSession().getStatus() == SessionStatus.Inviting) return 15;
        return super.getUpdateTime();
   }

    @Override
    public Collection<GameLabel> getLabels(final Player access)
    {
        try
        {
            if (getSession().getStatus() == SessionStatus.Inviting) return setupLobbyLabels(access);
            else return getCurrentPhase().setupLabels(this, access);
        }
        catch (final GameException e)
        {
            throw new SawdustSystemError(e);
        }
    }

    public Suits getLeadingSuit()
    {
        final IndexCard leadingCard = (IndexCard) getToken(new IndexPosition(EuchreLayout.POS_IN_PLAY, 0));
        if (null == leadingCard) return Suits.Null;
        final Suits leadingSuit = getEffectiveSuit(leadingCard.getCard());
        return leadingSuit;
    }

    @Override
    public ArrayList<GameCommand> getMoves(final Participant access) throws GameException
    {
        final ArrayList<GameCommand> returnValue = new ArrayList<GameCommand>();
        returnValue.addAll(super.getMoves(access));
        returnValue.addAll(getCurrentPhase().getMoves(access, this));
        return returnValue;
    }

    protected Collection<GameLabel> getPlayerLabels()
    {
        final ArrayList<GameLabel> returnValue = new ArrayList<GameLabel>();
        for (int playerIndex = 0; playerIndex < getPlayerManager().getPlayerCount(); playerIndex++)
        {
            final Participant player = getPlayerManager().playerName(playerIndex);
            returnValue.add(new GameLabel("PlayerLabel " + playerIndex, new IndexPosition(EuchreLayout.POS_PLAYER_LABEL, playerIndex), getDisplayName(player)));

            try
            {
                returnValue.add(new GameLabel("TeamLabel " + playerIndex, new IndexPosition(EuchreLayout.POS_TEAM_LABEL, playerIndex), "Team "
                        + getTeamNumber(player)));
            }
            catch (final GameException e)
            {
            }
        }
        return returnValue;
    }

    @Override
    public Position getPosition(final IndexPosition key, final Player access) throws GameException
    {
        final Position position = _layout.getPosition(getPlayerManager(), key, access);
        if (null != position) return position;
        return super.getPosition(key, access);
    }

    protected ArrayList<Ranks> getRankOrder()
    {
        /*
         * R-E-0045: When a suit is named trump, any card of that suit outranks any card of a R-E-0046: non-trump suit. The highest ranking
         * card in euchre is the jack of the trump suit R-E-0047: and is referred to as the right bower, or simply the right. Next highest
         * is the R-E-0048: other jack of the same color, the left bower. The right and left may also be R-E-0049: known as the "jack" and
         * the "jick", the "right bauer" and "left bauer," or R-E-0050: "jack" and "off jack" respectively. Remaining cards of the trump
         * suit rank from R-E-0051: high to low as A, K, Q, 10, and 9. R-E-0052: R-E-0053: In non-trump suits (except for the next suit),
         * the jacks are not special, and R-E-0054: the cards of those suits rank from high to low as A, K, Q, J, 10, and 9.
         */
        final ArrayList<Ranks> rankOrder = new ArrayList<Ranks>();
        rankOrder.add(Ranks.Ace);
        rankOrder.add(Ranks.King);
        rankOrder.add(Ranks.Queen);
        rankOrder.add(Ranks.Jack);
        rankOrder.add(Ranks.Ten);
        rankOrder.add(Ranks.Nine);
        rankOrder.add(Ranks.Eight);
        rankOrder.add(Ranks.Seven);
        return rankOrder;
    }

    protected ArrayList<Suits> getSuitOrder()
    {
        final ArrayList<Suits> returnValue = new ArrayList<Suits>();
        if (null != _winningCard)
        {
            switch (_trumpSuit)
            {
            case Spades:
                returnValue.add(Suits.Spades);
                returnValue.add(Suits.Clubs);
                returnValue.add(Suits.Hearts);
                returnValue.add(Suits.Diamonds);
                break;
            case Clubs:
                returnValue.add(Suits.Clubs);
                returnValue.add(Suits.Spades);
                returnValue.add(Suits.Hearts);
                returnValue.add(Suits.Diamonds);
                break;
            case Hearts:
                returnValue.add(Suits.Hearts);
                returnValue.add(Suits.Diamonds);
                returnValue.add(Suits.Spades);
                returnValue.add(Suits.Clubs);
                break;
            case Diamonds:
                returnValue.add(Suits.Diamonds);
                returnValue.add(Suits.Hearts);
                returnValue.add(Suits.Spades);
                returnValue.add(Suits.Clubs);
                break;
            }
        }
        return returnValue;
    }

    public int getTeamNumber(final Participant winningPlayer) throws GameException
    {
        return 1 + (getPlayerManager().findPlayer(winningPlayer) % 2);
    }

    @Override
    public boolean isInPlay()
    {
        if (!getPlayerManager().isFull()) return false;
        if (getCurrentPhase().equals(com.sawdust.games.euchre.EuchreGame.COMPLETE)) return false;
        if (getCurrentPhase().equals(com.sawdust.games.euchre.EuchreGame.DEALING)) return false;
        if (getCurrentPhase().equals(com.sawdust.games.euchre.EuchreGame.FORMING)) return false;
        return true;
    }

    public boolean isWinningCard(final IndexCard cardToPlay)
    {
        /*
         * R-E-0150: The player who played the highest trump wins the trick. If no trump were played, R-E-0151: the highest card of the suit
         * led wins the trick. Players who play neither the R-E-0152: suit led nor trump cannot win the trick. The player that won the trick
         * collects R-E-0153: the played cards from the table and then leads the next trick. TODO: Fix case so other suits cannot win *
         */
        if (null == _winningCard) return true;
        if (null == cardToPlay) return false;
        final int compareTo = getCardSortOrder(cardToPlay).compareTo(getCardSortOrder(_winningCard));
        return (compareTo < 0);
    }

    protected void payToTeam(final int teamNumber) throws GameException
    {
        final GameSession session = getSession();
        final int award = session.getBalance() / 2;
        for (final Player member : session.getPlayers())
        {
            final int teamNumber2 = getTeamNumber(member);
            if (teamNumber2 == teamNumber)
            {
                session.withdraw(award, member.loadAccount(), "Pay to Winning Team");
            }
        }
    }

    public boolean playerCanLead(final int player)
    {
        final Suits leadingSuit = getLeadingSuit();
        for (final Token token : getCurveCards(player))
        {
            final Card card = ((IndexCard) token).getCard();
            if ((getEffectiveSuit(card) == leadingSuit)) return true;
        }
        return false;
    }

    @Override
    public GameState doReset()
    {
        clearTokens();
        setCurrentPhase(EuchreGame.DEALING);
        return this;
    }

    public void setCurrentPhase(final GamePhase pcurrentPhase)
    {
        _currentPhase = pcurrentPhase;
    }

    @Override
    public GameState doStart() throws GameException
    {
        final GameSession session = getSession();
        session.doUnitWager();
        for (final Participant p : getPlayerManager().getPlayers())
        {
            if (p instanceof Agent<?>)
            {
                session.withdraw(-session.getUnitWager(), null, "Agent Ante Up");
            }
        }
        final int numPlayers = EuchreGame.NUMBER_OF_PLAYERS;
        final Collection<Participant> members = getPlayerManager().getPlayers();
        if (members.size() != numPlayers) throw new GameLogicException("Exactly 4 players are required to play");
        
        session.setStatus(SessionStatus.Playing, this);
        doCommand(EuchreCommand.Deal);
        return this;
    }


    public void clearTeamStatuses()
    {
        _teamStatus.clear();
    }

    public HashMap<Integer, TeamStatus> getTeamStatuses()
    {
        return _teamStatus;
    }

    public void setPointGoal(int _pointGoal)
    {
        this._pointGoal = _pointGoal;
    }

    public int getPointGoal()
    {
        return _pointGoal;
    }

    public List<Participant> getTeam(int affectedTeam)
    {
        
        ArrayList<Participant> arrayList = new ArrayList<Participant>();
        for(int i=0;i<4;i++)
        {
            Participant p = getPlayerManager().getPlayers().get(i);
            int team = 1 + (i % 2);
            if(team == affectedTeam)
            {
                arrayList.add(p);
            }
        }
        return arrayList;
    }
}
