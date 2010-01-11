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
    private GameState _parentGame = null; // If the game is encapsulated

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

    static <T> T randomMember(final T[] values)
    {
        if (0 == values.length) return null;
        final double i = Math.floor(Math.random() * values.length);
        return values[(int) i];
    }

    // protected ArrayList<GameCommand> _commands = new ArrayList<GameCommand>();
    protected ArrayList<Message> _newMessages = new ArrayList<Message>();
    protected GameConfig _config = null;
    protected Notification _notification = null;
    protected int _height = 500;
    protected int _width = 600;

    public int messageNumber = 0;
    public int _timeOffset = 0;
    public int _versionNumber = 0;
    private GameCanvas _canvas = null;

    protected BaseGame()
    {
        super();
    }

    protected BaseGame(BaseGame obj)
    {
        // _commands = new ArrayList<GameCommand>(obj._commands);
        _newMessages = (null != obj._newMessages) ? new ArrayList<Message>(obj._newMessages) : null;
        _config = new GameConfig(obj._config);
        _canvas = obj._canvas; // Should copy this, but it's intended to be immutable
        _notification = obj._notification;
        _height = obj._height;
        _width = obj._width;
        messageNumber = obj.messageNumber;
        _timeOffset = obj._timeOffset;
        _versionNumber = obj._versionNumber;
    }

    /*
     * (non-Javadoc)
     * @see com.sawdust.engine.game.Game#getCurrentPlayer()
     */
    public abstract Participant getCurrentPlayer();

    public BaseGame(final GameConfig config)
    {
        _config = config;
    }

    /*
     * (non-Javadoc)
     * @see com.sawdust.engine.game.Game#addMember(com.sawdust.engine.game.players.Participant)
     */
    public GameState doAddPlayer(final Participant agent) throws GameException
    {
        this.doAddMessage("%s joined the room", getDisplayName(agent));
        return this;
    }

    public Message addMessage(final Message m)
    {
        m.setId(++messageNumber);
        if (null != _newMessages) _newMessages.add(m);
        return m;
    }

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
                m.setId(++messageNumber);
                if (null != _newMessages) _newMessages.add(m);
                return m;
            }
            else return null;
        }
        else return doAddMessage(type, String.format(msg, params));
    }

    /*
     * (non-Javadoc)
     * @see com.sawdust.engine.game.Game#addMessage(java.lang.String, java.lang.Object)
     */
    public Message doAddMessage(final String msg, final Object... params)
    {
        return doAddMessage(Message.MessageType.Normal, msg, params);
    }

    protected void addNewModule(final GameModConfig x)
    {
    };

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return Util.Copy(this);
    }

    /*
     * (non-Javadoc)
     * @see com.sawdust.engine.game.Game#displayName(com.sawdust.engine.game.players.Participant)
     */
    public abstract String getDisplayName(Participant userId);

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
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

    /*
     * (non-Javadoc)
     * @see com.sawdust.engine.game.Game#getAgentFactories()
     */
    public List<AgentFactory<? extends Agent<?>>> getAgentFactories()
    {
        return new ArrayList<AgentFactory<? extends Agent<?>>>();
    }

    /*
     * (non-Javadoc)
     * @see com.sawdust.engine.game.Game#getConfig()
     */
    public GameConfig getConfig()
    {
        return _config;
    }

    /*
     * (non-Javadoc)
     * @see com.sawdust.engine.game.Game#getGameType()
     */
    public abstract GameType<?> getGameType();

    /*
     * (non-Javadoc)
     * @see com.sawdust.engine.game.Game#getHeight()
     */
    public int getHeight()
    {
        return _height;
    }

    /*
     * (non-Javadoc)
     * @see com.sawdust.engine.game.Game#getKeywords()
     */
    public String getKeywords()
    {
        return getConfig().getKeywords();
    }

    /*
     * (non-Javadoc)
     * @see com.sawdust.engine.game.Game#getMove(java.lang.String, com.sawdust.engine.game.players.Participant)
     */
    public GameCommand getMove(String commandText, Participant access) throws GameException
    {
        for (GameCommand move : getMoves(access))
        {
            if (move.getCommandText().equals(commandText)) { return move; }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see com.sawdust.engine.game.Game#getMoves(com.sawdust.engine.game.players.Participant)
     */
    public abstract ArrayList<GameCommand> getMoves(Participant access) throws GameException;

    /*
     * (non-Javadoc)
     * @see com.sawdust.engine.game.Game#getNewMessages()
     */
    public ArrayList<Message> getMessages()
    {
        if (null == _newMessages) return null;
        return new ArrayList<Message>(_newMessages);
    }

    protected Notification getNotification()
    {
        return _notification;
    }

    /*
     * (non-Javadoc)
     * @see com.sawdust.engine.game.Game#getSession()
     */
    public abstract GameSession getSession();

    /*
     * (non-Javadoc)
     * @see com.sawdust.engine.game.Game#getWidth()
     */
    public int getWidth()
    {
        return _width;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 3;
        int result = 1;
        // if (null != _commands)
        // {
        // for (final GameCommand c : _commands)
        // {
        // result = prime * result + ((c == null) ? 0 : c.hashCode());
        // }
        // }
        if (null != _newMessages)
        {
            for (final Message c : _newMessages)
            {
                result = prime * result + ((c == null) ? 0 : c.hashCode());
            }
        }
        return result;
    }

    protected void initializeModules()
    {
        for (final GameModConfig modConfig : _config.getModules())
        {
            if (modConfig.isEnabled())
            {
                addNewModule(modConfig);
            }
        }
    }

    public boolean isInPlay()
    {
        return false;
    }

    public GameState doRemoveMember(final Participant email) throws GameException
    {
        this.doAddMessage("%s left the room", getDisplayName(email));
        return this;
    }

    public abstract GameState doReset();

    public GameState setHeight(final int height)
    {
        _height = height;
        return this;
    }

    protected void setNotification(Notification notification)
    {
        this._notification = notification;
    }

    public GameState setSilent(boolean b)
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

    /*
     * (non-Javadoc)
     * @see com.sawdust.engine.game.Game#setWidth(int)
     */
    public GameState setWidth(final int width)
    {
        _width = width;
        return this;
    }

    /*
     * (non-Javadoc)
     * @see com.sawdust.engine.game.Game#start()
     */
    public abstract GameState doStart() throws GameException;

    /*
     * (non-Javadoc)
     * @see com.sawdust.engine.game.Game#toGwt(com.sawdust.engine.game.players.Player)
     */
    public GameFrame getView(final Player access) throws GameException
    {
        final GameFrame returnValue = new GameFrame(_config);
        returnValue.versionNumber = _versionNumber;
        returnValue.timeOffset = _timeOffset;
        returnValue.setHeight(_height);
        returnValue.setWidth(_width);
        returnValue.updateTime = getUpdateTime();
        returnValue.html = renderBasicHtml();
        returnValue.canvas = getCanvas();

        returnValue.setNotification(_notification);

        for (final GameCommand s : getMoves(access))
        {
            returnValue.getCommands().add(s.toGwt());
        }
        if (null != _newMessages)
        {
            for (final Message s : _newMessages)
            {
                if (visible(s, access))
                {
                    returnValue.addMessage(s);
                }
            }
        }
        return returnValue;
    }

    public abstract int getUpdateTime();

    public String renderBasicHtml()
    {
        return "";
    }

    @Override
    public String toString()
    {
        final StringBuffer sb = new StringBuffer();
        // sb.append(_commands.size() + " commands;");
        if (null != _newMessages) sb.append(_newMessages.size() + " messages;");
        return sb.toString();
    }

    /*
     * (non-Javadoc)
     * @see com.sawdust.engine.game.Game#update()
     */
    public abstract GameState doUpdate() throws GameException;

    private boolean visible(final Message s, final Player access)
    {
        if (s.getTo().equals(Message.ADMIN)) return (access.loadAccount().isAdmin());
        if (s.getTo().equals(Message.ALL)) return true;
        if (s.getTo().equals(access.getUserId())) return true;
        return false;
    }

    @Override
    public int getTimeOffset()
    {
        return _timeOffset;
    }

    @Override
    public GameState setTimeOffset(int timeOffset)
    {
        this._timeOffset = timeOffset;
        return this;
    }

    @Override
    public GameState setVersionNumber(int i)
    {
        this._versionNumber = i;
        return this;
    }

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

    public GameState setParentGame(GameState _parentGame)
    {
        if (_parentGame == this) throw new RuntimeException("Parent cannot be self!");
        this._parentGame = _parentGame;
        return this;
    }

    public GameState getParentGame()
    {
        return _parentGame;
    }

    public GameState doAdvanceTime(int milliseconds)
    {
        _timeOffset += milliseconds;
        return this;
    }

    public void setCanvas(GameCanvas canvas)
    {
        this._canvas = canvas;
    }

    public GameCanvas getCanvas()
    {
        return _canvas;
    }

    public void postStartActivity()
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
    public GameState setConfig(GameConfig newConfig) throws GameException
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

}
