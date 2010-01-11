package com.sawdust.games.poker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import com.sawdust.engine.controller.Util;
import com.sawdust.engine.controller.entities.Account;
import com.sawdust.engine.controller.entities.GameSession;
import com.sawdust.engine.controller.entities.GameSession.SessionStatus;
import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.controller.exceptions.GameLogicException;
import com.sawdust.engine.controller.exceptions.InputException;
import com.sawdust.engine.model.AgentFactory;
import com.sawdust.engine.model.GameType;
import com.sawdust.engine.model.basetypes.GameState;
import com.sawdust.engine.model.basetypes.MultiPlayerCardGame;
import com.sawdust.engine.model.players.Agent;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.players.Player;
import com.sawdust.engine.model.state.CommandResult;
import com.sawdust.engine.model.state.GameCommand;
import com.sawdust.engine.model.state.GameLabel;
import com.sawdust.engine.model.state.IndexCard;
import com.sawdust.engine.model.state.IndexPosition;
import com.sawdust.engine.model.state.Token;
import com.sawdust.engine.view.config.GameConfig;
import com.sawdust.engine.view.config.PropertyConfig;
import com.sawdust.engine.view.game.ActivityEvent;
import com.sawdust.engine.view.game.Message.MessageType;
import com.sawdust.engine.view.geometry.ParametricLine;
import com.sawdust.engine.view.geometry.ParametricPosition;
import com.sawdust.engine.view.geometry.Position;
import com.sawdust.engine.view.geometry.Vector;
import com.sawdust.games.poker.ai.Regular1;
import com.sawdust.games.poker.ai.Stupid1;

public abstract class PokerGame extends MultiPlayerCardGame
{
	public enum GamePhase
    {
        Null, 
        Bidding, 
        Drawing, 
        Showing,
        Complete 
    }

    public enum PlayerState
    {
        Bidding, Complete, Folded, Ready
    }

    private static final int CURVE_CARD_BUTTONS = -5;

    private static final int CURVE_CMD_BUTTONS = -4;
    private static final Logger LOG = Logger.getLogger(PokerGame.class.getName());
    private static final int NUMBER_OF_CARDS = 5;
    private static final Vector OFFSET_CARD_BUTTON = new Vector(0, 100);;
    private static final Vector OFFSET_COMMAND = new Vector(0, 30);

    private static final Vector OFFSET_OPPONENT_CARD = new Vector(20, 20);
    private static final Vector OFFSET_OPPONENTS = new Vector(150, 0);
    private static final Vector OFFSET_PLAYER_CARD = new Vector(100, 0);
    private static final Vector OFFSET_PLAYERLABEL = new Vector(0, -20);
    protected static final int POS_PLAYER_LABEL = -3;
    private static final Position POSITION_COMMAND = new Position(275, 340);
    private static final Position POSITION_PLAYER = new Position(100, 230);
    private static final Position POSITION_PRIMARY = new Position(50, 30);

    private static int getNumberOfPlayers(final GameConfig config)
    {
        final PropertyConfig propertyConfig = config.getProperties().get(GameConfig.NUM_PLAYERS);
        if (null != propertyConfig) return propertyConfig.getInteger();
        return 2;
    }

    private final HashMap<Participant, Integer> _currentBets = new HashMap<Participant, Integer>();
    private GamePhase _currentPhase = GamePhase.Null;
    private Vector _handVector;
    private ParametricPosition _mainHand = null;
    private final ArrayList<ParametricPosition> _playerHands = new ArrayList<ParametricPosition>();
    private final HashMap<Participant, PlayerState> _playerStates = new HashMap<Participant, PlayerState>()
    {

        @Override
        public void clear()
        {
            LOG.fine("_playerStates cleared");
            super.clear();
        }

        @Override
        public PlayerState put(final Participant key, final PlayerState value)
        {
            System.out.println(String.format("_playerStates set: %s = %s", key.getId(), value.toString()));
            return super.put(key, value);
        }

    };

    private int _roundBet;

    public int NUMBER_OF_PLAYERS;;

    protected PokerGame()
    {
        NUMBER_OF_PLAYERS = 2;
    }

    public PokerGame(final GameConfig config)
    {
        super(getNumberOfPlayers(config), config);
        NUMBER_OF_PLAYERS = getNumberOfPlayers(config);
        setTimeoutAgent(new Stupid1("Timeout"));
        initPositions(NUMBER_OF_PLAYERS, NUMBER_OF_CARDS);
    }

    protected PokerGame(final int n)
    {
        super(n);
        NUMBER_OF_PLAYERS = n;
        initPositions(NUMBER_OF_PLAYERS, NUMBER_OF_CARDS);
    }

    private void declareWinner(final GameSession gameSession) throws GameException
    {
        setCurrentPhase(GamePhase.Complete);
        final ArrayList<PokerHand> hands = new ArrayList<PokerHand>();
        for (int i = 0; i < NUMBER_OF_PLAYERS; i++)
        {
            final Participant playerName = getPlayerManager().playerName(i);
            if (_playerStates.get(playerName) == PlayerState.Complete)
            {
                final ArrayList<IndexCard> playerCards = new ArrayList<IndexCard>();
                for (final Token card : getCurveCards(i))
                {
                    playerCards.add((IndexCard) card);
                }
                final PokerHand playerHand = PokerHandPattern.FindHighest(playerCards);
                this.doAddMessage("%s has a %s", getDisplayName(playerName), playerHand.getName());
                playerHand.setOwner(playerName);
                hands.add(playerHand);
                for (final IndexCard t : playerCards)
                {
                    t.setPublic();
                }
            }
        }
        final PokerHand winningHand = PokerHand.GetHighest(hands);
        final Participant winner = winningHand.getOwner();
        this.doAddMessage("<strong>%s won with %s</strong>", getDisplayName(winner), winningHand.getName());

        String displayName = getDisplayName(winner);
        if(winner instanceof Player)
        {
            final ArrayList<Player> winners = new ArrayList<Player>();
            winners.add((Player) winner);
            gameSession.doSplitWagerPool(winners);
            String type = "Win/Go";
            String event = String.format("I won a game of Poker with a !", winningHand.getName());
            ((Player)winner).logActivity(new ActivityEvent(type,event));
        }
        for (int i = 0; i < NUMBER_OF_PLAYERS; i++)
        {
            final Participant otherPlayer = getPlayerManager().playerName(i);
            String opponentName = getDisplayName(otherPlayer);
            if(otherPlayer instanceof Player)
            {
                String type = "Lose/Go";
                String event = String.format("I lost a game of Poker to %s!", displayName);
                ((Player)otherPlayer).logActivity(new ActivityEvent(type,event));
            }
        }
    }

    public void doBet(final Participant player, final int bet) throws GameException
    {
        /*
         * R-P-0001 In casino play the first betting round begins with the player to the left R-P-0002 of the big blind, and subsequent
         * rounds begin with the player to the dealer's R-P-0003 left. Home games typically use an ante; the first betting round begins
         * R-P-0004 with the player to the dealer's left, and the second round begins with R-P-0005 the player who opened the first round.
         * In this version, the running bet is started at the level of the game ante and betting goes from Player 1 onward. Each player can
         * raise, see the current bet level, or fold.
         */
        if (getCurrentPhase() != GamePhase.Bidding) throw new GameLogicException("Invalid state: " + getCurrentPhase());
        if (bet < _roundBet) throw new GameLogicException(String.format("Cannot bet %d, current bet is %d", bet, _roundBet));
        if (bet < _roundBet) throw new GameLogicException(String.format("Cannot bet %d, current bet is %d", bet, _roundBet));
        if (!getPlayerManager().isCurrentPlayer(player)) throw new GameLogicException("It is not your turn");
        if (_playerStates.get(player) != PlayerState.Bidding) throw new GameLogicException("Invalid player state");
        // user.loadSession().deposit(user.loadAccount(), bet);

        final int raiseAmount = bet - _roundBet;
        int currentBet = 0;
        if (_currentBets.containsKey(player))
        {
            currentBet = _currentBets.get(player);
        }
        final int payAmount = bet - currentBet;

        if (payAmount > 0)
        {
            if (player instanceof Player)
            {
                final Account a = ((Player) player).loadAccount();
                final int balance = a.getBalance();
                if ((balance < payAmount) && (raiseAmount <= 0))
                {
                    this.doAddMessage(MessageType.Compact, "(All in: %d) ", balance);
                    a.withdraw(balance, getSession(), "All in");
                    _currentBets.put(player, balance + currentBet);
                }
                else
                {
                    a.withdraw(payAmount, getSession(), "Bet");
                    _currentBets.put(player, bet);
                }
            }
            else
            {
                getSession().withdraw(-payAmount, null, "Non-player bet");
                _currentBets.put(player, bet);
            }
        }

        if (raiseAmount > 0)
        {
            for (final Participant eachPlayer : getPlayerManager().getPlayers())
            {
                if (_playerStates.get(eachPlayer) == PlayerState.Ready)
                {
                    _playerStates.put(eachPlayer, PlayerState.Bidding);
                }
            }
            this.doAddMessage(MessageType.Compact, "%s raises the bet to %d", getDisplayName(player), bet);
            this.doAddMessage("");
            _roundBet = bet;
        }
        else
        {
            this.doAddMessage(MessageType.Compact, "%s calls", getDisplayName(player));
            this.doAddMessage("");
        }
        _playerStates.put(player, PlayerState.Ready);

        Participant nextPlayer = getPlayerManager().gotoNextPlayer();
        while (_playerStates.get(nextPlayer) == PlayerState.Folded)
        {
            nextPlayer = getPlayerManager().gotoNextPlayer();
        }

        if (_playerStates.get(nextPlayer) == PlayerState.Ready)
        {
            this.doAddMessage("<strong>Betting is complete. Please draw new cards.</strong>");
            setCurrentPhase(GamePhase.Drawing);
        }
        else
        {
            this.doAddMessage(MessageType.Compact, "%s's turn: ", getDisplayName(nextPlayer));
        }
    }

    public void doDraw(final Participant player) throws GameException
    {
        /*
         * R-P-0011 If more than one player remains after the first round, the "draw" phase R-P-0012 begins. Each player specifies how many
         * of their cards they wish to replace R-P-0013 and discards them. The deck is retrieved, and each player is dealt in R-P-0014 turn
         * from the deck the same number of cards they discarded so that each R-P-0015 player again has five cards. R-P-0016 A second
         * "after the draw" betting round occurs beginning with the player R-P-0017 to the dealer's left or else beginning with the player
         * who opened the R-P-0018 first round (the latter is common when antes are used instead of blinds). R-P-0019 This is followed by a
         * showdown if more than one player remains, in which R-P-0020 the player with the best hand wins the pot. There is no second-round
         * betting before the showdown
         */
        if (getCurrentPhase() != GamePhase.Drawing) throw new GameLogicException("Invalid state: " + getCurrentPhase());
        if (_playerStates.get(player) != PlayerState.Ready) throw new GameLogicException("Invalid player state");
        _playerStates.put(player, PlayerState.Complete);

        int numberOfCards = 0;
        final int playerIndex = getPlayerManager().findPlayer(player);
        for (int cardSlot = 0; cardSlot < NUMBER_OF_CARDS; cardSlot++)
        {
            IndexCard t = (IndexCard) getToken(new IndexPosition(playerIndex, cardSlot));
            if (null == t)
            {
                numberOfCards++;
                t = doDealNewCard(new IndexPosition(playerIndex, cardSlot));
                t.setOwner(player);
                t.setPrivate("VR");
                // t.getMoveCommands().put(new
                // IndexPosition(NUMBER_OF_PLAYERS,0), "Discard " + cardSlot);
                t.setMovable(true);
            }
        }
        this.doAddMessage("Player %s draws %d cards", getDisplayName(player), numberOfCards);

        if (isEveryoneDone())
        {
            declareWinner(getSession());

        }
        else
        {
            while (getPlayerState(getPlayerManager().getCurrentPlayer()) != PlayerState.Ready)
            {
                getPlayerManager().gotoNextPlayer();
            }
        }
    }

    public void doFold(final Participant user) throws GameException
    {
        if (getCurrentPhase() != GamePhase.Bidding) throw new GameLogicException("Invalid state: " + getCurrentPhase());
        if (!getPlayerManager().isCurrentPlayer(user)) throw new GameLogicException("It is not your turn");
        final PlayerState playerState = _playerStates.get(user);
        boolean isBidding = playerState != PlayerState.Bidding;
        isBidding |= playerState == PlayerState.Ready;
        if (isBidding) throw new GameLogicException("Invalid player state: " + playerState);
        _playerStates.put(user, PlayerState.Folded);
        this.doAddMessage(MessageType.Compact, "%s folds.", getDisplayName(user));
        this.doAddMessage("");
        Participant nextPlayer = getPlayerManager().gotoNextPlayer();
        while (getPlayerState(nextPlayer) == PlayerState.Folded)
        {
            nextPlayer = getPlayerManager().gotoNextPlayer();
        }
        if (_playerStates.get(nextPlayer) == PlayerState.Ready)
        {
            this.doAddMessage("<strong>Betting is complete. Please draw new cards.</strong>");
            setCurrentPhase(GamePhase.Drawing);
        }
        else
        {
            this.doAddMessage(MessageType.Compact, "%s's turn: ", getDisplayName(nextPlayer));
        }
    }

    public void dropCards(final Participant email, final ArrayList<Integer> cardIndex) throws GameException
    {
        /*
         * R-P-0011 If more than one player remains after the first round, the "draw" phase R-P-0012 begins. Each player specifies how many
         * of their cards they wish to replace R-P-0013 and discards them. The deck is retrieved, and each player is dealt in R-P-0014 turn
         * from the deck the same number of cards they discarded so that each R-P-0015 player again has five cards.
         */
        if (getCurrentPhase() != GamePhase.Drawing) throw new GameLogicException("Invalid state: " + getCurrentPhase());
        final int player = getPlayerManager().findPlayer(email);
        int cardCount = 0;
        for (final Integer cardSlot : cardIndex)
        {
            final Token card = getToken(new IndexPosition(player, cardSlot));
            if (null == card)
            {
                this.doAddMessage("Warning: No card found at index %d", cardSlot);
                continue;
            }
            doRemoveToken(card);
            getDeck().discard(((IndexCard) card).getCard());
            cardCount++;
        }
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
        agentFactories.add(new AgentFactory<Regular1>()
        {

            @Override
            public Regular1 getAgent(final String id)
            {
                return new Regular1(id);
            }

            @Override
            public String getName()
            {
                return "Normal";
            }
        });
        return agentFactories;
    }

    public int getCurrentBet()
    {
        return _roundBet;
    }

    public GamePhase getCurrentPhase()
    {
        return _currentPhase;
    }

    @Override
    public GameType<PokerGame> getGameType()
    {
        return PokerGameType.INSTANCE;
    }

    @Override
    public Collection<GameLabel> getLabels(final Player access) throws GameException
    {
        final int bankAvailible = access.loadAccount().getBalance();
        final ArrayList<GameLabel> returnValue = new ArrayList<GameLabel>();

        if (GamePhase.Drawing == _currentPhase)
        {
            returnValue.addAll(getPlayerLabels());
            GameLabel l;
            int labelNumber = 0;

            final int playerIndex = getPlayerManager().findPlayer(access);
            for (int cardNumber = 0; cardNumber < NUMBER_OF_CARDS; cardNumber++)
            {
                final IndexCard t = (IndexCard) getToken(new IndexPosition(playerIndex, cardNumber));
                if (null != t)
                {
                    l = new GameLabel("DISCARD " + cardNumber, new IndexPosition(CURVE_CARD_BUTTONS, cardNumber), "Discard");
                    l.setCommand("Discard " + t.getCard().toString());
                    returnValue.add(l);
                }
            }

            labelNumber++;
            l = new GameLabel("STAY", new IndexPosition(CURVE_CMD_BUTTONS, labelNumber++), "Draw Cards");
            l.setCommand("Draw Cards");
            returnValue.add(l);
        }
        else if (GamePhase.Bidding == _currentPhase)
        {
            returnValue.addAll(getPlayerLabels());
            GameLabel l;
            int labelNumber = 0;
            int currentBet = 0;
            if (_currentBets.containsKey(access))
            {
                currentBet = _currentBets.get(access);
            }

            l = new GameLabel("POOL", new IndexPosition(CURVE_CMD_BUTTONS, labelNumber++), String.format("Current Wager: %d", _roundBet));
            returnValue.add(l);

            final int requiredBet = _roundBet - currentBet;
            final int maxRaise = bankAvailible - requiredBet;
            int ante = getSession().getUnitWager();

            l = new GameLabel("SEE", new IndexPosition(CURVE_CMD_BUTTONS, labelNumber++), String.format("Call", requiredBet));
            l.setCommand("Call");
            returnValue.add(l);

            final int betAmt = _roundBet + ante;
            if (bankAvailible >= betAmt)
            {
                l = new GameLabel("RAISE1", new IndexPosition(CURVE_CMD_BUTTONS, labelNumber++), String.format("Raise %d", ante, ante + _roundBet - currentBet));
                l.setCommand("Raise " + ante);
                returnValue.add(l);
                ante *= 5;
                if (ante < maxRaise)
                {
                    l = new GameLabel("RAISE5", new IndexPosition(CURVE_CMD_BUTTONS, labelNumber++), String.format("Raise %d", ante));
                    l.setCommand("Raise " + ante);
                    returnValue.add(l);
                }
                else if (bankAvailible > betAmt)
                {
                    l = new GameLabel("ALL IN", new IndexPosition(CURVE_CMD_BUTTONS, labelNumber++), "Go All In");
                    l.setCommand("Go All In");
                    returnValue.add(l);
                }
            }

            l = new GameLabel("FOLD", new IndexPosition(CURVE_CMD_BUTTONS, labelNumber++), "Fold");
            l.setCommand("Fold");
            returnValue.add(l);
        }
        else if (GamePhase.Complete == _currentPhase)
        {
            returnValue.addAll(getPlayerLabels());
            GameLabel l;
            int labelNumber = 0;

            l = new GameLabel("DEAL", new IndexPosition(CURVE_CMD_BUTTONS, labelNumber++), String.format("Deal Again"));
            l.setCommand("Deal");
            returnValue.add(l);
        }
        else if (GamePhase.Null == _currentPhase)
        {
            try
            {
                returnValue.addAll(getLobbyLabels(access));
            }
            catch (final InputException e)
            {
            	LOG.fine(Util.getFullString(e));
            }
        }

        return returnValue;
    }

    @Override
    public int getUpdateTime()
    {
        if (GamePhase.Null == _currentPhase) return 15;
        return super.getUpdateTime();
    }

    @Override
    public ArrayList<GameCommand> getMoves(final Participant access) throws GameException
    {
        final ArrayList<GameCommand> returnValue = new ArrayList<GameCommand>();
        returnValue.addAll(super.getMoves(access));

        if (GamePhase.Drawing == _currentPhase)
        {
            final int playerIndex = getPlayerManager().findPlayer(access);
            for (int cardNumber = 0; cardNumber < NUMBER_OF_CARDS; cardNumber++)
            {
                final IndexCard t = (IndexCard) getToken(new IndexPosition(playerIndex, cardNumber));
                if (null != t)
                {
                    final String cardNumberStr = Integer.toString(cardNumber);

                    returnValue.add(new GameCommand() {
						
						@Override
						public String getHelpText() {
                            return null;
						}
						
						@Override
						public String getCommandText() {
                            return "Discard " + t.getCard().toString();
						}
						
						@Override
						public CommandResult doCommand(Participant p, String commandText) throws GameException {
                            com.sawdust.games.poker.Commands.Drop_Cards.doCommand((Player)p, getSession(), cardNumberStr);
                            return new CommandResult<PokerGame>(PokerGame.this);
						}
					});
                }
            }
            {
                returnValue.add(new GameCommand() {
					
					@Override
					public String getHelpText() {
						return null;
					}
					
					@Override
					public String getCommandText() {
                        return "Draw Cards";
					}
					
					@Override
					public CommandResult doCommand(Participant p, String commandText) throws GameException {
                        com.sawdust.games.poker.Commands.Draw_Cards.doCommand((Player) p, getSession(), "");
                        return new CommandResult<PokerGame>(PokerGame.this);
					}
				});
            }
        }
        else if (GamePhase.Bidding == _currentPhase)
        {
            int currentBet = 0;
            if (_currentBets.containsKey(access))
            {
                currentBet = _currentBets.get(access);
            }

            final int requiredBet = _roundBet - currentBet;
            final int balance = (access instanceof Player) ? ((Player) access).loadAccount().getBalance() : -1;
            final int maxRaise = balance - requiredBet;
            int ante = getSession().getUnitWager();

            if (_roundBet > 0)
            {
                final String betAmt = Integer.toString(_roundBet);
                returnValue.add(new GameCommand() {
					
					@Override
					public String getHelpText() {
						return null;
					}
					
					@Override
					public String getCommandText() {
                        return "Call";
					}
					
					@Override
					public CommandResult doCommand(Participant p, String commandText) throws GameException {
                        com.sawdust.games.poker.Commands.Bet.doCommand((Player) p, getSession(), betAmt);
                        return new CommandResult<PokerGame>(PokerGame.this);
					}
				});
            }

            {
                final String betAmt = Integer.toString(_roundBet + ante);
                final String cmdText = String.format("Raise %d", ante);
                returnValue.add(new GameCommand() {
					
					@Override
					public String getHelpText() {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public String getCommandText() {
                        return cmdText;
					}
					
					@Override
					public CommandResult doCommand(Participant p, String commandText) throws GameException {
                        com.sawdust.games.poker.Commands.Bet.doCommand((Player) p, getSession(), betAmt);
                        return new CommandResult<PokerGame>(PokerGame.this);
					}
				});
            }

            ante *= 5;
            if (ante > maxRaise)
            {
                {
                    final String betAmt = Integer.toString(_roundBet + maxRaise);
                    returnValue.add(new GameCommand() {
						
						@Override
						public String getHelpText() {
							return null;
						}
						
						@Override
						public String getCommandText() {
                            return "Go All In";
						}
						
						@Override
						public CommandResult doCommand(Participant p, String commandText) throws GameException {
                            com.sawdust.games.poker.Commands.Bet.doCommand(p, getSession(), betAmt);
                            return new CommandResult<PokerGame>(PokerGame.this);
						}
					});
                }
            }
            else
            {
                {
                    final String betAmt = Integer.toString(_roundBet + ante);
                    // final String cmdLabel =
                    // String.format("Raise %d (Wager %d)", ante, ante +
                    // _roundBet - currentBet);
                    final String cmd = String.format("Raise %d", ante);
                    returnValue.add(new GameCommand() {
						
						@Override
						public String getHelpText() {
							return null;
						}
						
						@Override
						public String getCommandText() {
                            return cmd;
						}
						
						@Override
						public CommandResult doCommand(Participant p, String commandText) throws GameException {
                            com.sawdust.games.poker.Commands.Bet.doCommand((Player) p, getSession(), betAmt);
                            return new CommandResult<PokerGame>(PokerGame.this);
						}
					});
                }
            }

            {
                returnValue.add(new GameCommand() {
					
					@Override
					public String getHelpText() {
						return null;
					}
					
					@Override
					public String getCommandText() {
                        return "Fold";
					}
					
					@Override
					public CommandResult doCommand(Participant p, String commandText) throws GameException {
                        com.sawdust.games.poker.Commands.Fold.doCommand((Player) p, getSession(), "");
                        return new CommandResult<PokerGame>(PokerGame.this);
					}
				});
            }
        }
        else
        {
            // Do nothing
        }
        return returnValue;
    }

    protected Collection<GameLabel> getPlayerLabels()
    {
        final ArrayList<GameLabel> returnValue = new ArrayList<GameLabel>();
        for (int playerIndex = 0; playerIndex < getPlayerManager().getPlayerCount(); playerIndex++)
        {
            final Participant player = getPlayerManager().playerName(playerIndex);
            returnValue.add(new GameLabel("PlayerLabel " + playerIndex, new IndexPosition(POS_PLAYER_LABEL, playerIndex), getDisplayName(player)));
        }
        return returnValue;
    }

    public PlayerState getPlayerState(final Participant player)
    {
        return _playerStates.get(player);
    }

    @Override
    public Position getPosition(final IndexPosition key, final Player access) throws GameException
    {
        final int curveIndex = key.getCurveIndex();
        final int findPlayer = getPlayerManager().findPlayer(access);
        if (curveIndex == findPlayer) return _mainHand.getPositionN(key.getCardIndex());
        else if ((curveIndex >= 0) && (curveIndex < NUMBER_OF_PLAYERS))
        {
            final int playerIndex = findPlayer;
            int relativePlayer = (curveIndex - playerIndex) % NUMBER_OF_PLAYERS;
            relativePlayer--;
            if (relativePlayer < 0)
            {
                relativePlayer += NUMBER_OF_PLAYERS; // Java
            }
            // does
            // modulus
            // INCORRECTLY
            final Position positionN = _playerHands.get(relativePlayer).getPositionN(key.getCardIndex());
            positionN.setZ(key.getCardIndex());
            return positionN;
        }
        else if (curveIndex == CURVE_CARD_BUTTONS) return _mainHand.getPositionN(key.getCardIndex()).add(OFFSET_CARD_BUTTON);
        else if (curveIndex == CURVE_CMD_BUTTONS) return POSITION_COMMAND.add(OFFSET_COMMAND.scale(key.getCardIndex()));
        else if (curveIndex == POS_PLAYER_LABEL)
        {
            int relativePlayer = (key.getCardIndex() - findPlayer) % NUMBER_OF_PLAYERS;
            relativePlayer--;
            if (relativePlayer < 0)
            {
                relativePlayer += NUMBER_OF_PLAYERS; // Java
            }
            // does
            // modulus
            // INCORRECTLY
            if (findPlayer == key.getCardIndex()) return _mainHand.getPositionN(0).add(OFFSET_PLAYERLABEL);
            else return _playerHands.get(relativePlayer).getPositionN(0).add(OFFSET_PLAYERLABEL);
        }
        else return super.getPosition(key, access);
    }

    protected void initPositions(final int playerCount, final int cardCount)
    {
        _handVector = OFFSET_OPPONENT_CARD.scale(cardCount);
        Position currentPosition = POSITION_PRIMARY;
        for (int i = 0; i < playerCount; i++)
        {
            _playerHands.add(new ParametricLine(currentPosition, currentPosition.add(_handVector), cardCount));
            currentPosition = currentPosition.add(OFFSET_OPPONENTS);
        }
        _mainHand = new ParametricLine(POSITION_PLAYER, POSITION_PLAYER.add(OFFSET_PLAYER_CARD.scale(cardCount)), cardCount);
    }

    private boolean isEveryoneDone()
    {
        boolean isEveryoneDone = true;
        for (final Participant player : getPlayerManager().getPlayers())
        {
            if (_playerStates.get(player) == PlayerState.Ready)
            {
                isEveryoneDone = false;
                break;
            }
        }
        return isEveryoneDone;
    }

    @Override
    public boolean isInPlay()
    {
        if (!getPlayerManager().isFull()) return false;
        if (getCurrentPhase().equals(com.sawdust.games.poker.PokerGame.GamePhase.Complete)) return false;
        if (getCurrentPhase().equals(com.sawdust.games.poker.PokerGame.GamePhase.Null)) return false;
        return true;
    }

    @Override
    public GameState doReset()
    {
        doClearTokens();
        _currentPhase = GamePhase.Null;
        return this;
    }

    protected void setCurrentPhase(final GamePhase currentPhase)
    {
        LOG.info(String.format("Game state changed from %s to %s", _currentPhase.toString(), currentPhase.toString()));
        _currentPhase = currentPhase;
    }

    @Override
    public GameState doStart() throws GameException
    {
        boolean isntComplete = getCurrentPhase() != GamePhase.Complete;
        boolean isntNull = getCurrentPhase() != GamePhase.Null;
        if (isntNull && isntComplete) throw new GameLogicException("Invalid state: " + getCurrentPhase());
        doClearTokens();
        getDeck().setReshuffleEnabled(true);
        for (int player = 0; player < NUMBER_OF_PLAYERS; player++)
        {
            final Participant thisPlayer = getPlayerManager().playerName(player);
            this.doAddMessage(MessageType.Compact, "%s's hand: ", getDisplayName(thisPlayer)).setTo(thisPlayer.getId());
            for (int cardSlot = 0; cardSlot < NUMBER_OF_CARDS; cardSlot++)
            {
                final IndexCard t = doDealNewCard(new IndexPosition(player, cardSlot));
                t.setOwner(thisPlayer);
                t.setPrivate("VR");
                t.setMovable(true);
                this.doAddMessage(MessageType.Compact, "(%s) ", t.getCard()).setTo(thisPlayer.getId());
            }
            this.doAddMessage("");
        }
        _playerStates.clear();
        for (final Participant player : getPlayerManager().getPlayers())
        {
            _playerStates.put(player, PlayerState.Bidding);
        }
        setCurrentPhase(GamePhase.Bidding);
        getSession().withdraw(getSession().getBalance(), null, "New Game");
        _roundBet = getSession().getUnitWager();
        _currentBets.clear();
        getPlayerManager().setCurrentPlayer(0);
        final Participant nextPlayer = getPlayerManager().gotoNextPlayer();
        this.doAddMessage(MessageType.Compact, "%s's turn: ", getDisplayName(nextPlayer));
        return this;
    }

    @Override
    public PokerGame doUpdate() throws GameException
    {
        super.doUpdate();
        if (getCurrentPhase() == GamePhase.Drawing)
        {
            for (final Participant p : getPlayerManager().getPlayers())
            {
                if ((_playerStates.get(p) == PlayerState.Ready) && (p instanceof Agent<?>))
                {
                    ((Agent<PokerGame>) p).getMove(this, p).doCommand(p, null);
                }
            }
        }
        return this;
    }

	@Override
	public Participant getCurrentPlayer() {
		return getPlayerManager().getCurrentPlayer();
	}
}
