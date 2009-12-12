package com.sawdust.engine.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sawdust.engine.common.game.GameState;
import com.sawdust.engine.common.GameException;

public class CommandResult implements Serializable
{
    public int _bankBalance = 0;
    private String _exception = null;
    private ArrayList<GameState> _stateSequence = new ArrayList<GameState>();

    public CommandResult()
    {
        super();
    }

    public CommandResult(final GameException exception)
    {
        super();
        _exception = exception.getMessage();
    }

    public CommandResult(final GameState state)
    {
        super();
        addState(state);
    }

    public void addState(final GameState state)
    {
        _stateSequence.add(state);
    }

    public String getException()
    {
        return _exception;
    }

    public List<GameState> getStateFrames()
    {
        return _stateSequence;
    }

    private void setStateSequence(ArrayList<GameState> pstateSequence)
    {
        this._stateSequence = pstateSequence;
    }

    private ArrayList<GameState> getStateSequence()
    {
        return _stateSequence;
    }
}
