package com.sawdust.engine.model.players;

import java.io.Serializable;
import java.util.ArrayList;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.controller.exceptions.GameLogicException;

public class PlayerManager implements Serializable
{
    int _currentPlayerIndex = -1;
    int _maxSize;
    ArrayList<Participant> _members = new ArrayList<Participant>();

    public PlayerManager()
    {
        super();
    }

    public PlayerManager(final int count)
    {
        super();
        _maxSize = count;
    }

    public PlayerManager doAddMember(final Participant email) throws GameException
    {
        if (isFull()) throw new GameLogicException("This game is full");
        _members.add(email);
        return this;
    }

    public PlayerManager doDropMember(final Participant email) throws GameException
    {
        if (_members.contains(email))
        {
            _members.remove(email);
        }
        return this;
    }

    public Participant getCurrentPlayer()
    {
        if (_currentPlayerIndex < 0) return null;
        return _members.get(_currentPlayerIndex);
    }

    public int getCurrentPlayerIndex()
    {
        return _currentPlayerIndex;
    }

    public int getMemberCount()
    {
        return _members.size();
    }

    public Participant getNextPlayer()
    {
        return _members.get((_currentPlayerIndex + 1) % _maxSize);
    }

    public int getPlayerCount()
    {
        return _members.size();
    }

    public Participant getPlayerFromIndex(final String playerID)
    {
        for (int i = 0; i < _members.size(); i++)
        {
            final Participant player = _members.get(i);
            if (player.getId().equals(playerID)) return player;
        }
        return null;
    }

    public int getPlayerIndex(final Participant owner) throws GameException
    {
        for (int i = 0; i < _members.size(); i++)
        {
            final Participant player = _members.get(i);
            if (player.equals(owner)) return i;
        }
        return -1;
    }

    public Participant getPlayerName(final int player)
    {
        return _members.get(player);
    }

    public ArrayList<Participant> getPlayers()
    {
        return _members;
    }

    public void getResetCurrentPlayer()
    {
        _currentPlayerIndex = 0;
    }

    public Participant gotoNextPlayer()
    {
        _currentPlayerIndex = (_currentPlayerIndex + 1) % _maxSize;
        return _members.get(_currentPlayerIndex);
    }

    public boolean isCurrentPlayer(final Participant email)
    {
        return (email.equals(_members.get(_currentPlayerIndex)));
    }

    public boolean isFull()
    {
        return (_members.size() >= _maxSize);
    }

    public boolean isMember(final Participant email)
    {
        for (final Participant s : _members)
        {
            if (email.equals(s)) return true;
        }
        return false;
    }

    @Deprecated
    public PlayerManager setCurrentPlayer(final int playerIndex)
    {
        _currentPlayerIndex = playerIndex;
        return this;
    }

    public PlayerManager setCurrentPlayer(final Participant player) throws GameException
    {
        _currentPlayerIndex = this.getPlayerIndex(player);
        return this;
    }
}
