package com.sawdust.client.gwt.util;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;
import com.sawdust.client.gwt.widgets.mylog;
import com.sawdust.common.gwt.SawdustGameService;
import com.sawdust.common.gwt.SawdustGameServiceAsync;
import com.sawdust.engine.common.AccessToken;
import com.sawdust.engine.common.CommandResult;

public class CommandExecutor
{

    public void onEvent(final ArrayList<EventListener> event, final Object... params)
    {
        if(0 == event.size())
        {
            LOG.debug("onEvent had no listeners");
        }
        for (final EventListener listener : event)
        {
            if (null != listener)
            {
                listener.onEvent(params);
            }
            else
            {
                LOG.debug("onEvent had a null listener");
            }
        }
    }

    private AccessToken _accessKey = null;
    private final ArrayList<Command> _commandQueue = new ArrayList<Command>();

    private final SawdustGameServiceAsync _gameService = GWT.create(SawdustGameService.class);

    boolean _isRunningQuery = false;
    int _locks = 0;
    
    public boolean isLocked()
    {
        assert(0 <= _locks) : "Negative lock index";
        return (0 < _locks);
    }
    public int incrementLock()
    {
        assert(0 <= _locks) : "Negative lock index";
        return ++_locks;
    }
    public void decrementLock(int i)
    {
        assert(0 < _locks) : "Negative lock index";
        assert(_locks == i) : "Out-of-sequence unlock";
        _locks--;
    }
    

    final ArrayList<EventListener> _onComplete = new ArrayList<EventListener>();
    final ArrayList<EventListener> _onError = new ArrayList<EventListener>();
    private final ArrayList<EventListener> _onPostSend = new ArrayList<EventListener>();
    private final ArrayList<EventListener> _onPreSend = new ArrayList<EventListener>();
    final ArrayList<EventListener> _onSuccess = new ArrayList<EventListener>();
    mylog LOG;

    public CommandExecutor(final mylog log)
    {
        super();
        LOG = log;
        _onComplete.add(new EventListener()
        {
            public void onEvent(final Object... params)
            {
                doQueue();
            }
        });
        _onSuccess.add(new EventListener()
        {
            public void onEvent(final Object... params)
            {
                final CommandResult result = (CommandResult) params[0];
                if (result._bankBalance > 0)
                {
                    final RootPanel rootPanel = RootPanel.get("bank");
                    if (null != rootPanel)
                    {
                        rootPanel.getElement().setInnerHTML(Integer.toString(result._bankBalance));
                    }
                }
            }
        });
    }

    public CommandExecutor(final mylog log, final EventListener initSuccess, final EventListener initError, final EventListener initPostSend, final EventListener initPreSend)
    {
        this(log);
        _onError.add(initError);
        _onSuccess.add(initSuccess);
        _onPostSend.add(initPostSend);
        _onPreSend.add(initPreSend);
    }

    public void doCommand(final String cmd, final EventListener post)
    {
        queueCommand(cmd, post);
        if (!_isRunningQuery)
        {
            doQueue();
        }
    }

    private void doQueue()
    {
        if (!_commandQueue.isEmpty())
        {
            _isRunningQuery = true;
            final ArrayList<Command> queue = (ArrayList<Command>) _commandQueue.clone();
            _commandQueue.clear();
            final ArrayList<String> cmds = new ArrayList<String>();
            for (final Command c : queue)
            {
                LOG.debug("Batching command: " + c.command);
                cmds.add(c.command);
            }
            onEvent(_onPreSend);
            _gameService.gameCmds(_accessKey, cmds, new CmdCallback<CommandResult>(this, "BATCH", new EventListener()
            {
                public void onEvent(final Object... params)
                {
                    for (final Command c : queue)
                    {
                        if (null == c)
                        {
                            continue;
                        }
                        if (null == c.onComplete)
                        {
                            continue;
                        }
                        c.onComplete.onEvent(params);
                    }
                }
            }));
            onEvent(_onPostSend);
        }
        else
        {
            LOG.debug("Empty queue at " + new Date().toLocaleString());
        }
    }

    public void doUpdate(final int versionNumber, final EventListener post)
    {
        if (!_isRunningQuery)
        {
            _isRunningQuery = true;
            if (_commandQueue.isEmpty())
            {
                LOG.debug("doUpdate : Command queue is empty");
                onEvent(_onPreSend);
                _gameService.getGameUpdate(_accessKey, versionNumber, new CmdCallback<CommandResult>(this, post));
                onEvent(_onPostSend);
            }
            else
            {
                LOG.debug("doUpdate: Executing queue");
                doQueue();
            }
        }
        else
        {
            LOG.debug("doUpdate: Currently in query; ignored");
        }
    }

    public void doLoad(final EventListener post)
    {
        if (!_isRunningQuery)
        {
            _isRunningQuery = true;
            LOG.debug("doLoad");
            onEvent(_onPreSend);
            _gameService.getState(_accessKey, new CmdCallback<CommandResult>(this, post));
            onEvent(_onPostSend);
        }
        else
        {
            LOG.debug("doLoad: Currently in query; ignored");
        }
    }

    public void getGame(final EventListener post)
    {
        onEvent(_onPreSend);
        if (!_isRunningQuery)
        {
            _isRunningQuery = true;
        }
        _gameService.getState(_accessKey, new CmdCallback<CommandResult>(this, "", post));
        onEvent(_onPostSend);
    }

    public void queueCommand(final String cmd, final EventListener post)
    {
        LOG.debug("Queing command: " + cmd);
        _commandQueue.add(new Command(cmd, post));
    }

    public void setAccessKey(final AccessToken accessToken)
    {
        _accessKey = accessToken;

    }

    public ArrayList<Command> getCommandQueue()
    {
        return _commandQueue;
    }
}
