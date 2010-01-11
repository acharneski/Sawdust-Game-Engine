package com.sawdust.engine.model.basetypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.controller.exceptions.GameLogicException;
import com.sawdust.engine.controller.exceptions.InputException;
import com.sawdust.engine.model.AgentFactory;
import com.sawdust.engine.model.players.Agent;
import com.sawdust.engine.model.players.MultiPlayer;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.players.Player;
import com.sawdust.engine.model.players.PlayerManager;
import com.sawdust.engine.model.state.GameCommand;
import com.sawdust.engine.model.state.GameLabel;
import com.sawdust.engine.model.state.IndexCard;
import com.sawdust.engine.model.state.IndexPosition;
import com.sawdust.engine.view.cards.Card;
import com.sawdust.engine.view.cards.CardDeck;
import com.sawdust.engine.view.config.GameConfig;
import com.sawdust.engine.view.game.GameFrame;
import com.sawdust.engine.view.game.Message;
import com.sawdust.engine.view.game.Notification;
import com.sawdust.engine.view.geometry.Position;

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
        getSession().setMinimumPlayers(nPlayers);
    }

    protected MultiPlayerCardGame(final int nPlayers, final GameConfig config)
    {
        super(config);
        if (config.getProperties().containsKey(GameConfig.RANDOM_SEED))
        {
            final String seed = config.getProperties().get(GameConfig.RANDOM_SEED).value;
            this.doAddMessage("Setting seed: %s", seed).setTo(Message.ADMIN);
            getDeck().setSeed(seed);
        }
        _mplayerManager = new MultiPlayer(nPlayers);
        getSession().setMinimumPlayers(nPlayers);
    }

    @Override
    public MultiPlayerCardGame doAddPlayer(final Participant agent) throws GameException
    {
        if (agent instanceof Player)
        {
            _displayFilter.put(agent, ((Player) agent).loadAccount().getName());
        }
        else
        {
            _displayFilter.put(agent, ((Agent<?>) agent).getId());
        }
        GameState newGame = super.doAddPlayer(agent);
        _mplayerManager.addMember(this, agent);
        return (MultiPlayerCardGame) newGame;
    }

    @Override @Deprecated
    public IndexCard doDealNewCard(final IndexPosition indexPosition)
    {
        final Card dealNewCard = getDeck().dealNewCard();
        if (null == dealNewCard) return null;
        final IndexCard newCard = new IndexCard(++cardIdCounter, null, "VR", false, indexPosition, dealNewCard);
        doAddToken(newCard);
        return newCard;
    }

    public MultiPlayerCardGame doForceMove(final Participant participant) throws GameException
    {
        try
        {
            _mplayerManager.doForceMove(this, participant);
        }
        catch (GameException e)
        {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public MultiPlayerCardGame doRemoveMember(final Participant email) throws GameException
    {
        _mplayerManager.removeMember(this, email);
        return (MultiPlayerCardGame) super.doRemoveMember(email);
    }

    @Override
    public MultiPlayerCardGame doUpdate() throws GameException
    {
        _mplayerManager.update(this);
        return this;
    }

    public Agent<?> getAgent(final String playerID)
    {
        return _mplayerManager.getAgent(playerID);
    }

    @Override
    public List<AgentFactory<? extends Agent<?>>> getAgentFactories()
    {
        return new ArrayList<AgentFactory<? extends Agent<?>>>();
    }

    protected ArrayList<GameLabel> getLobbyLabels(final Player access) throws InputException
    {
        return _mplayerManager.getLobbyLabels(this, access);
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

    public int getUpdateTime()
    {
        return _mplayerManager.isSinglePlayer()?90:5;
    }

    @Override
    public GameFrame getView(Player access) throws GameException
    {
        final GameFrame returnValue = super.getView(access);
        if(!getPlayerManager().isMember(access))
        {
            if(this.isInPlay())
            {
                Notification notification = new Notification();
                notification.notifyText = "You are currently observing a game in progress.";
                notification.add("Join Table", "Join Game");
                returnValue.setNotification(notification);
            }
            else if(this.getSession().getActivePlayers() > 0)
            {
                Notification notification = new Notification();
                notification.notifyText = "This game is currently forming.";
                notification.add("Join Table", "Join Game");
                returnValue.setNotification(notification);
            }
            else
            {
                Notification notification = new Notification();
                notification.notifyText = "This is a potentially stale game.";
                notification.add("Join Table", "Join Game");
                returnValue.setNotification(notification);
            }
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

    public MultiPlayerCardGame setPlayerManager(final PlayerManager playerManager)
    {
        _mplayerManager.setPlayerManager(playerManager);
        return this;
    }

    public MultiPlayerCardGame setTimeoutAgent(final Agent<?> timeoutAgent)
    {
        _mplayerManager.setTimeoutAgent(timeoutAgent);
        return this;
    }
}
