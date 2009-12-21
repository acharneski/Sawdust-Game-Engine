/**
 * 
 */
package com.sawdust.client.gwt.util;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sawdust.client.gwt.widgets.GameClientWidget;
import com.sawdust.client.gwt.widgets.mylog;
import com.sawdust.engine.common.CommandResult;

final class CmdCallback<T> implements AsyncCallback<T>
{
    private final String _command;
    private final EventListener _post;
    /**
     * 
     */
    public final CommandExecutor commandExecutor;
    private final mylog LOG;

    public CmdCallback(final CommandExecutor pcommandExecutor, final EventListener post)
    {
        LOG = pcommandExecutor.LOG;
        this.commandExecutor = pcommandExecutor;
        this._command = GameClientWidget.CMD_UPDATE;
        _post = post;
    }

    CmdCallback(final CommandExecutor pcommandExecutor, final String cmd, final EventListener post)
    {
        LOG = pcommandExecutor.LOG;
        this.commandExecutor = pcommandExecutor;
        this._command = cmd;
        _post = post;
    }

    private void onError(final String type, final Object param)
    {
        LOG.debug("onError...");
        this.commandExecutor.onEvent(this.commandExecutor._onError, param, new EventListener()
        {
            public void onEvent(final Object... params)
            {
                LOG.debug("Post-onError onEvent...");
                CmdCallback.this.commandExecutor._isRunningQuery = false;
                CmdCallback.this.commandExecutor.onEvent(CmdCallback.this.commandExecutor._onComplete, params);
                if (null != _post)
                {
                    _post.onEvent(type, param);
                }
            }
        }, type);
    }

    public void onFailure(final Throwable caught)
    {
        LOG.debug("onFailure...");
        if (!GameClientWidget.CMD_UPDATE.equals(_command))
        {
            onError("SYSTEM", caught);
        }
        else
        {
            CmdCallback.this.commandExecutor._isRunningQuery = false;
            CmdCallback.this.commandExecutor.onEvent(CmdCallback.this.commandExecutor._onComplete, caught);
            if (null != _post)
            {
                _post.onEvent(caught);
            }
        }
    }

    public void onSuccess(final T result)
    {
        LOG.debug("onSuccess...");
        if (result instanceof CommandResult)
        {
            this.commandExecutor._isRunningQuery = false;
            final String exception = ((CommandResult) result).getException();
            if ((null == exception) || exception.isEmpty())
            {
                if (null != _post)
                {
                    _post.onEvent(result);
                }
                this.commandExecutor.onEvent(this.commandExecutor._onSuccess, result);
                this.commandExecutor._isRunningQuery = false;
            }
            else
            {
                if (!GameClientWidget.CMD_UPDATE.equals(_command))
                {
                    // Only signal error if the command was not an update
                    onError("APPLICATION", exception);
                }
                else
                {
                    if (null != _post)
                    {
                        _post.onEvent();
                    }
                }
            }
            this.commandExecutor.onEvent(this.commandExecutor._onComplete);
        }
        else
        {
            this.commandExecutor._isRunningQuery = false;
            if (null != _post)
            {
                _post.onEvent();
            }
            this.commandExecutor.onEvent(this.commandExecutor._onComplete);
        }
    }
}
