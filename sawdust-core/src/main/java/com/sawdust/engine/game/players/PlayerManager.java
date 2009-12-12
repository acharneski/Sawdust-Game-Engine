package com.sawdust.engine.game.players;

import java.io.Serializable;
import java.util.ArrayList;

import com.sawdust.engine.service.debug.GameException;
import com.sawdust.engine.service.debug.GameLogicException;

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

    public void addMember(final Participant email) throws GameException
    {
        if (isFull()) throw new GameLogicException("This game is full");
        _members.add(email);
    }

    public void dropMember(final Participant email) throws GameException
    {
        if (_members.contains(email))
        {
            _members.remove(email);
        }
    }

    public int findPlayer(final Participant owner) throws GameException
    {
        for (int i = 0; i < _members.size(); i++)
        {
            final Participant player = _members.get(i);
            if (player.equals(owner)) return i;
        }
        return -1;
    }

    public Participant findPlayer(final String playerID)
    {
        for (int i = 0; i < _members.size(); i++)
        {
            final Participant player = _members.get(i);
            if (player.getId().equals(playerID)) return player;
        }
        return null;
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

    public Participant getNextPlayer()
    {
        return _members.get((_currentPlayerIndex + 1) % _maxSize);
    }

    public int getPlayerCount()
    {
        return _members.size();
    }

    public ArrayList<Participant> getPlayers()
    {
        return _members;
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

    public int memberCount()
    {
        return _members.size();
    }

    public Participant playerName(final int player)
    {
        return _members.get(player);
    }

    public void resetCurrentPlayer()
    {
        _currentPlayerIndex = 0;
    }

    @Deprecated
    public void setCurrentPlayer(final int playerIndex)
    {
        _currentPlayerIndex = playerIndex;
    }

    public void setCurrentPlayer(final Participant player) throws GameException
    {
        _currentPlayerIndex = this.findPlayer(player);
    }
}
