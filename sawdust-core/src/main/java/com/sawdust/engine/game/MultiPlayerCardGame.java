package com.sawdust.engine.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sawdust.engine.common.cards.Card;
import com.sawdust.engine.common.cards.CardDeck;
import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.common.game.GameState;
import com.sawdust.engine.common.game.Message;
import com.sawdust.engine.common.game.Notification;
import com.sawdust.engine.common.geometry.Position;
import com.sawdust.engine.game.players.Agent;
import com.sawdust.engine.game.players.MultiPlayer;
import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.game.players.Player;
import com.sawdust.engine.game.players.PlayerManager;
import com.sawdust.engine.game.state.GameCommand;
import com.sawdust.engine.game.state.GameLabel;
import com.sawdust.engine.game.state.IndexCard;
import com.sawdust.engine.game.state.IndexPosition;
import com.sawdust.engine.service.debug.GameException;
import com.sawdust.engine.service.debug.GameLogicException;
import com.sawdust.engine.service.debug.InputException;

public abstract class MultiPlayerCardGame extends IndexCardGame implements MultiPlayerGame
{
    protected static final int IDX_LOBBY_LABEL_PUBLIC = 3;
    protected static final int IDX_LOBBY_LABEL_STATUS = 0;
    protected static final int IDX_LOBBY_LABEL_TIP_INVITE = 4;
    protected static final int POS_LOBBY_LABEL = -10;
    protected static final int POS_MEMBER_LIST = -11;

    protected MultiPlayer _mplayerManager;

    protected MultiPlayerCardGame()
    {
        _mplayerManager = null;
    }

    protected MultiPlayerCardGame(final int nPlayers)
    {
        super();
        _mplayerManager = new MultiPlayer(nPlayers);
        getSession().setRequiredPlayers(nPlayers);
    }

    protected MultiPlayerCardGame(final int nPlayers, final GameConfig config)
    {
        super(config);
        if (config.getProperties().containsKey(GameConfig.RANDOM_SEED))
        {
            final String seed = config.getProperties().get(GameConfig.RANDOM_SEED).value;
            this.addMessage("Setting seed: %s", seed).setTo(Message.ADMIN);
            getDeck().setSeed(seed);
        }
        _mplayerManager = new MultiPlayer(nPlayers);
        getSession().setRequiredPlayers(nPlayers);
    }

    @Override
    public void addMember(final Participant agent) throws GameException
    {
        if (agent instanceof Player)
        {
            _displayFilter.put(agent, ((Player) agent).loadAccount().getName());
        }
        else
        {
            _displayFilter.put(agent, ((Agent<?>) agent).getId());
        }
        super.addMember(agent);
        _mplayerManager.addMember(this, agent);
    }

    @Override
    public IndexCard dealNewCard(final IndexPosition indexPosition)
    {
        final Card dealNewCard = getDeck().dealNewCard();
        if (null == dealNewCard) return null;
        final IndexCard newCard = new IndexCard(++cardIdCounter, null, "VR", false, indexPosition, dealNewCard);
        add(newCard);
        return newCard;
    }

    public void doForceMove(final Participant participant) throws GameException
    {
        try
        {
            _mplayerManager.doForceMove(this, participant);
        }
        catch (GameException e)
        {
            e.printStackTrace();
        }
    }

    public Agent<?> getAgent(final String playerID)
    {
        return _mplayerManager.getAgent(playerID);
    }

    @Override
    public List<AgentFactory<?>> getAgentFactories()
    {
        return new ArrayList<AgentFactory<?>>();
    }

    @Override
    public ArrayList<GameCommand> getMoves(final Participant access) throws GameException
    {
        return _mplayerManager.getMoves(this, access);
    }

    public PlayerManager getPlayerManager()
    {
        return _mplayerManager.getPlayerManager();
    }

    @Override
    public Position getPosition(final IndexPosition key, final Player access) throws GameException
    {
        final Position position = _mplayerManager.getPosition(key, access);
        if (null != position) return position;
        throw new GameLogicException("Unknown curve index: " + key.getCurveIndex());
    }

    public Agent<?> getTimeoutAgent()
    {
        return _mplayerManager.getTimeoutAgent();
    }

    @Override
    public void removeMember(final Participant email) throws GameException
    {
        super.removeMember(email);
        _mplayerManager.removeMember(this, email);
    }

    public void setPlayerManager(final PlayerManager playerManager)
    {
        _mplayerManager.setPlayerManager(playerManager);
    }

    public void setTimeoutAgent(final Agent<?> timeoutAgent)
    {
        _mplayerManager.setTimeoutAgent(timeoutAgent);
    }

    protected ArrayList<GameLabel> setupLobbyLabels(final Player access) throws InputException
    {
        return _mplayerManager.setupLobbyLabels(this, access);
    }

    @Override
    public void update() throws GameException
    {
        _mplayerManager.update(this);
    }

    @Override
    public GameState toGwt(Player access) throws GameException
    {
        final GameState returnValue = super.toGwt(access);
        if(!getPlayerManager().isMember(access))
        {
            Notification notification = new Notification();
            notification.notifyText = "You are currently observing this game.";
            notification.add("Join Table", "Join Game");
            returnValue.setNotification(notification);
        }
        else if(!isInPlay())
        {
            Notification notification = new Notification();
            notification.notifyText = "No game is currently in progress";
            notification.add("Leave Table", "Leave Game");
            returnValue.setNotification(notification);
        }
        return returnValue;
    }

    public int getUpdateTime()
    {
        return _mplayerManager.isSinglePlayer()?90:5;
    }
}
