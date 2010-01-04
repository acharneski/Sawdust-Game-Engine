package com.sawdust.engine.game.state;

import java.io.Serializable;

import com.sawdust.engine.common.geometry.Position;
import com.sawdust.engine.game.basetypes.TokenGame;
import com.sawdust.engine.game.players.Player;
import com.sawdust.engine.service.debug.GameException;
import com.sawdust.engine.service.debug.GameLogicException;

public class GameLabel implements Serializable
{
    private String _command = null;
    private String _id = null;
    private IndexPosition _position = null;
    private String _text = null;
    private int _width = 200;

    public GameLabel(final String pkey, final IndexPosition labelPos, final String labelText)
    {
        _id = pkey;
        _position = labelPos;
        _text = labelText;
    }

    public String getCommand()
    {
        return _command;
    }

    public String getKey()
    {
        return _id;
    }

    public String getText()
    {
        return _text;
    }

    public int getWidth()
    {
        return _width;
    }

    public GameLabel setCommand(final String command)
    {
        _command = command;
        return this;
    }

    public void setKey(final String key)
    {
        _id = key;
    }

    public void setText(final String text)
    {
        _text = text;
    }

    public GameLabel setWidth(final int width)
    {
        _width = width;
        return this;
    }

    public com.sawdust.engine.common.game.GameLabel toGwt(final Player access, final TokenGame parametricTokenGame) throws GameException
    {
        final com.sawdust.engine.common.game.GameLabel returnValue = new com.sawdust.engine.common.game.GameLabel();
        final Position p1 = parametricTokenGame.getPosition(_position, access);
        if (null == p1) throw new GameLogicException("Cannot find position: " + _position);
        returnValue.position = p1;
        returnValue.text = _text;
        returnValue.command = _command;
        returnValue.key = _id;
        returnValue.width = _width;
        return returnValue;

    }
}
