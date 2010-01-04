package com.sawdust.engine.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sawdust.engine.common.game.GameFrame;
import com.sawdust.engine.common.GameException;

public class CommandResult implements Serializable
{
    public int _bankBalance = 0;
    private String _exception = null;
    private ArrayList<GameFrame> _stateSequence = new ArrayList<GameFrame>();

    public CommandResult()
    {
        super();
    }

    public CommandResult(final GameException exception)
    {
        super();
        _exception = exception.getMessage();
    }

    public CommandResult(final GameFrame state)
    {
        super();
        addState(state);
    }

    public void addState(final GameFrame state)
    {
        _stateSequence.add(state);
    }

    public String getException()
    {
        return _exception;
    }

    public List<GameFrame> getStateFrames()
    {
        return _stateSequence;
    }

    private void setStateSequence(ArrayList<GameFrame> pstateSequence)
    {
        this._stateSequence = pstateSequence;
    }

    private ArrayList<GameFrame> getStateSequence()
    {
        return _stateSequence;
    }
}
