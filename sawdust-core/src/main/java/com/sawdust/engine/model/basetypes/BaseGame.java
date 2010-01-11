package com.sawdust.engine.model.basetypes;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import com.sawdust.engine.controller.Util;
import com.sawdust.engine.controller.entities.Account;
import com.sawdust.engine.controller.entities.GameSession;
import com.sawdust.engine.controller.entities.SessionMember;
import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.AgentFactory;
import com.sawdust.engine.model.GameType;
import com.sawdust.engine.model.players.Agent;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.players.Player;
import com.sawdust.engine.model.state.GameCommand;
import com.sawdust.engine.view.config.GameConfig;
import com.sawdust.engine.view.config.GameModConfig;
import com.sawdust.engine.view.config.PropertyConfig;
import com.sawdust.engine.view.game.GameCanvas;
import com.sawdust.engine.view.game.GameFrame;
import com.sawdust.engine.view.game.Message;
import com.sawdust.engine.view.game.Notification;

public abstract class BaseGame implements GameState
{
    private static final Logger LOG = Logger.getLogger(BaseGame.class.getName());
    public static <T> boolean compareArray(final ArrayList<T> thisList, final ArrayList<T> otherList)
    {
        if ((thisList == null) || (otherList == null))
        {
            if (thisList != null) return false;
            if (otherList != null) return false;
        }
        else
        {
            if (thisList.size() != otherList.size()) return false;
            for (int i = 0; i < thisList.size(); i++)
            {
                final T ca = thisList.get(i);
                final T cb = otherList.get(i);
                if ((ca == null) || (cb == null))
                {
                    if (ca != null) return false;
                    if (cb != null) return false;
                }
                else if (!ca.equals(cb)) return false;
            }
        }
        return true;
    }

    GameState _parentGame = null;
    ArrayList<Message> _newMessages = new ArrayList<Message>();
    GameConfig _config = null;
    Notification _notification = null;
    GameCanvas _canvas = null;
    int _height = 500;
    int _width = 600;
    public int _messageNumber = 0;
    public int _timeOffset = 0;
    public int _versionNumber = 0;

    BaseGame()
    {
        super();
    }

    BaseGame(BaseGame obj)
    {
        _newMessages = (null != obj._newMessages) ? new ArrayList<Message>(obj._newMessages) : null;
        _config = new GameConfig(obj._config);
        _canvas = obj._canvas;
        _notification = obj._notification;
        _height = obj._height;
        _width = obj._width;
        _messageNumber = obj._messageNumber;
        _timeOffset = obj._timeOffset;
        _versionNumber = obj._versionNumber;
    }

    public BaseGame(final GameConfig config)
    {
        _config = config;
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return Util.Copy(this);
    }

    @Deprecated
    public Message doAddMessage(final Message m)
    {
        m.setId(++_messageNumber);
        if (null != _newMessages) _newMessages.add(m);
        return m;
    }

    @Deprecated @Override
    public Message doAddMessage(final Message.MessageType type, final String msg, final Object... params)
    {
        if (null == _newMessages) return new Message("");
        if ((null == params) || (0 == (params).length))
        {
            if (null != msg)
            {
                LOG.info(msg);
                final Message m = new Message(msg);
                m.setType(type);
                m.setId(++_messageNumber);
                if (null != _newMessages) _newMessages.add(m);
                return m;
            }
            else return null;
        }
        else return doAddMessage(type, String.format(msg, params));
    }

    @Deprecated @Override
    public Message doAddMessage(final String msg, final Object... params)
    {
        return doAddMessage(Message.MessageType.Normal, msg, params);
    }

    BaseGame doAddNewModule(final GameModConfig x)
    {
        return this;
    }

    @Override
    public GameState doAddPlayer(final Participant agent) throws GameException
    {
        this.doAddMessage("%s joined the room", getDisplayName(agent));
        return this;
    };

    @Override
    public GameState doAdvanceTime(int milliseconds)
    {
        _timeOffset += milliseconds;
        return this;
    }

    protected void doInitializeModules()
    {
        for (final GameModConfig modConfig : _config.getModules())
        {
            if (modConfig.isEnabled())
            {
                doAddNewModule(modConfig);
            }
        }
    }

    public void doOnPostStartActivity()
    {
        GameSession session = getSession();
        Message message = this.doAddMessage(
                String.format("I am playing %s at Sawdust Games. You can join my game at %s", getGameType().getName(), session
                        .getUrl())).setSocialActivity(true);

        SessionMember owner = session.getOwner();
        String ownerId = "";
        if(null != owner)
        {
            Account account = owner.getAccount();
            ownerId = account.getUserId();
            message.setTo(ownerId);
        }
    }

    @Override
    public GameState doRemoveMember(final Participant email) throws GameException
    {
        this.doAddMessage("%s left the room", getDisplayName(email));
        return this;
    }

    @Override
    public abstract GameState doReset();

    @Override
    public GameState doSaveState() throws GameException
    {
        if (null != this._parentGame)
        {
            _parentGame.doSaveState();
        }
        else
        {
            GameSession session = this.getSession();
            if (null != session) session.setState(this);
        }
        return this;
    }

    @Override
    public abstract GameState doStart() throws GameException;

    @Override
    public abstract GameState doUpdate() throws GameException;

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final BaseGame other = (BaseGame) obj;

        // if (!compareArray(_commands, other._commands)) return false;
        if (!compareArray(_newMessages, other._newMessages)) return false;

        return true;
    }

    @Override
    public List<AgentFactory<? extends Agent<?>>> getAgentFactories()
    {
        return new ArrayList<AgentFactory<? extends Agent<?>>>();
    }

    public String getBasicHtml()
    {
        return "";
    }

    public GameCanvas getCanvas()
    {
        return _canvas;
    }

    @Override
    public GameConfig getConfig()
    {
        return _config;
    }

    @Override
    public abstract Participant getCurrentPlayer();

    @Override
    public abstract String getDisplayName(Participant userId);

    @Override
    public abstract GameType<?> getGameType();

    @Override
    public int getHeight()
    {
        return _height;
    }

    boolean getIsVisible(final Message s, final Player access)
    {
        if (s.getTo().equals(Message.ADMIN)) return (access.getAccount().isAdmin());
        if (s.getTo().equals(Message.ALL)) return true;
        if (s.getTo().equals(access.getUserId())) return true;
        return false;
    }

    @Override
    public String getKeywords()
    {
        return getConfig().getKeywords();
    }

    @Override
    public ArrayList<Message> getMessages()
    {
        if (null == _newMessages) return null;
        return new ArrayList<Message>(_newMessages);
    }

    @Override
    public GameCommand getMove(String commandText, Participant access) throws GameException
    {
        for (GameCommand move : getMoves(access))
        {
            if (move.getCommandText().equals(commandText)) { return move; }
        }
        return null;
    }

    @Override
    public abstract ArrayList<GameCommand> getMoves(Participant access) throws GameException;

    protected Notification getNotification()
    {
        return _notification;
    }

    @Override
    public GameState getParentGame()
    {
        return _parentGame;
    }

    @Override
    public abstract GameSession getSession();

    @Override
    public int getTimeOffset()
    {
        return _timeOffset;
    }

    @Override
    public abstract int getUpdateTime();

    @Override
    public GameFrame getView(final Player access) throws GameException
    {
        final GameFrame returnValue = new GameFrame(_config);
        returnValue.versionNumber = _versionNumber;
        returnValue.timeOffset = _timeOffset;
        returnValue.setHeight(_height);
        returnValue.setWidth(_width);
        returnValue.updateTime = getUpdateTime();
        returnValue.html = getBasicHtml();
        returnValue.canvas = getCanvas();

        returnValue.setNotification(_notification);

        for (final GameCommand s : getMoves(access))
        {
            returnValue.getCommands().add(s.getView());
        }
        if (null != _newMessages)
        {
            for (final Message s : _newMessages)
            {
                if (getIsVisible(s, access))
                {
                    returnValue.addMessage(s);
                }
            }
        }
        return returnValue;
    }

    @Override
    public int getWidth()
    {
        return _width;
    }

    public int hashCode()
    {
        final int prime = 3;
        int result = 1;
        if (null != _newMessages)
        {
            for (final Message c : _newMessages)
            {
                result = prime * result + ((c == null) ? 0 : c.hashCode());
            }
        }
        return result;
    }

    @Override
    public boolean isInPlay()
    {
        return false;
    }

    public BaseGame setCanvas(GameCanvas canvas)
    {
        this._canvas = canvas;
        return this;
    }

    @Override
    public BaseGame setConfig(GameConfig newConfig) throws GameException
    {
        HashMap<String, PropertyConfig> thisProperties = getConfig().getProperties();
        HashMap<String, PropertyConfig> newProperties = newConfig.getProperties();
        String anteString = newProperties.get(GameConfig.ANTE).value;
        int anteInteger = Integer.parseInt(anteString);
        thisProperties.get(GameConfig.ANTE).value = anteString;
        thisProperties.get(GameConfig.ANTE).defaultValue = anteString;
        this.getSession().setUnitWager(anteInteger);
        doSaveState();
        return this;
    }

    @Override
    public BaseGame setHeight(final int height)
    {
        _height = height;
        return this;
    }

    protected BaseGame setNotification(Notification notification)
    {
        this._notification = notification;
        return this;
    }

    public BaseGame setParentGame(GameState _parentGame)
    {
        if (_parentGame == this) throw new RuntimeException("Parent cannot be self!");
        this._parentGame = _parentGame;
        return this;
    }

    public BaseGame setSilent(boolean b)
    {
        if (b)
        {
            _newMessages = null;
        }
        else
        {
            if (null == _newMessages)
            {
                _newMessages = new ArrayList<Message>();
            }
        }
        return this;
    }

    @Override
    public BaseGame setTimeOffset(int timeOffset)
    {
        this._timeOffset = timeOffset;
        return this;
    }

    @Override
    public BaseGame setVersionNumber(int i)
    {
        this._versionNumber = i;
        return this;
    }

    @Override
    public BaseGame setWidth(final int width)
    {
        _width = width;
        return this;
    }

    @Override
    public String toString()
    {
        final StringBuffer sb = new StringBuffer();
        // sb.append(_commands.size() + " commands;");
        if (null != _newMessages) sb.append(_newMessages.size() + " messages;");
        return sb.toString();
    }

}
