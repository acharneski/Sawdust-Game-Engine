package com.sawdust.engine.model.state;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.basetypes.TokenGame;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.players.Player;
import com.sawdust.engine.view.geometry.Position;

public class Token implements Serializable
{
    protected static class SerialForm implements Serializable
    {
        HashMap<IndexPosition, String> _moveCommands;
        String _art = "";
        String _text = "";
        int _id = 0;
        boolean _movable = false;
        Participant _owner = null;
        IndexPosition _position = null;
        String imageLibraryId = null;
        String baseImageId = null;
        String toggleImageId = null;
        String toggleCommand = null;

        
        protected SerialForm(){}
        protected SerialForm(Token obj)
        {
            _moveCommands = obj._moveCommands;
            _art = obj._art;
            _text = obj._text;
            _id = obj._id;
            _movable = obj._movable;
            _owner = obj._owner;
            _position = obj._position;
            imageLibraryId = obj.imageLibraryId;
            baseImageId = obj.baseImageId;
            toggleImageId = obj.toggleImageId;
            toggleCommand = obj.toggleCommand;
        }
        private Object readResolve()
        {
            return new Token(this);
        }
    }

    private static final Logger LOG = Logger.getLogger(Token.class.getName());
    
    final HashMap<IndexPosition, String> _moveCommands = new HashMap<IndexPosition, String>();
    
    String _art = "";
    int _id = 0;
    boolean _movable = false;
    Participant _owner = null;
    IndexPosition _position = null;
    public String _text = "";
    public String imageLibraryId = null;
    public String baseImageId = null;
    public String toggleImageId = null;
    public String toggleCommand = null;
    
    public Token()
    {
        super();
    }
    
    public Token(final int id, final String libararyId, final String art, final Participant player, final String publicArt, final boolean movable, final IndexPosition position)
    {
       imageLibraryId = libararyId;
        _position = position;
        _id = id;
        _art = art;
        _owner = player;
        baseImageId = publicArt;
        _movable = movable;
    }

    public Token(SerialForm obj)
    {
        _moveCommands.putAll(obj._moveCommands);
        _art = obj._art;
        _text = obj._text;
        _id = obj._id;
        _movable = obj._movable;
        _owner = obj._owner;
        _position = obj._position;
        imageLibraryId = obj.imageLibraryId;
        baseImageId = obj.baseImageId;
        toggleImageId = obj.toggleImageId;
        toggleCommand = obj.toggleCommand;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final Token other = (Token) obj;
        if (_art == null)
        {
            if (other._art != null) return false;
        }
        else if (!_art.equals(other._art)) return false;
        if (_id != other._id) return false;
        if ((null != baseImageId) || (null != other.baseImageId))
        {
            if ((null == baseImageId) || (null == other.baseImageId))
            {
                return false;
            }
            if (baseImageId.equals(other.baseImageId)) return false;
        }
        if (_movable != other._movable) return false;
        if (_owner == null)
        {
            if (other._owner != null) return false;
        }
        else if (!_owner.equals(other._owner)) return false;
        if (_position == null)
        {
            if (other._position != null) return false;
        }
        else if (!_position.equals(other._position)) return false;
        if (_moveCommands == null)
        {
            if (other._moveCommands != null) return false;
        }
        else if (!_moveCommands.equals(other._moveCommands)) return false;
        return true;
    }

    public String getArt()
    {
        return _art;
    }

    public int getId()
    {
        return _id;
    }

    public HashMap<IndexPosition, String> getMoveCommands()
    {
        return _moveCommands;
    }

    public Participant getOwner()
    {
        return _owner;
    }

    public IndexPosition getPosition()
    {
        return _position;
    }

    public String getText()
    {
        return _text;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_art == null) ? 0 : _art.hashCode());
        result = prime * result + _id;
        result = prime * result + (_movable ? 1231 : 1237);
        result = prime * result + ((baseImageId == null) ? 0 : baseImageId.hashCode());
        result = prime * result + ((_owner == null) ? 0 : _owner.hashCode());
        result = prime * result + ((_position == null) ? 0 : _position.hashCode());
        result = prime * result + ((_moveCommands == null) ? 0 : _moveCommands.hashCode());
        return result;
    }

    public boolean isMovable()
    {
        return _movable;
    }

    public boolean isPublic()
    {
        return (null == baseImageId);
    }

    private void readObject(ObjectInputStream s) throws  IOException, ClassNotFoundException
    {
        throw new NotSerializableException();
    }

    public void setArt(final String art)
    {
        _art = art;

    }

    public void setMovable(final boolean movable)
    {
        _movable = movable;
    }

    public Token setOwner(final Participant playerEmail)
    {
        _owner = playerEmail;
        return this;
    }

    public void setPosition(final IndexPosition position)
    {
        if(null == position) throw new NullPointerException();
        _position = position;
    }

    public void setPrivate(final String string)
    {
        baseImageId = string;
    }

    public void setPublic()
    {
        baseImageId = null;
    }

    public void setText(String _text)
    {
        this._text = _text;
    }

    public com.sawdust.engine.view.game.Token toGwt(final Player access, final TokenGame parametricTokenGame) throws GameException
    {
        final com.sawdust.engine.view.game.Token returnValue = new com.sawdust.engine.view.game.Token(_id, imageLibraryId, _art);
        returnValue.setToggleImageId(toggleImageId);
        returnValue.setToggleCommand(toggleCommand);
        returnValue.setText(getText());
        returnValue.setPosition(parametricTokenGame.getPosition(_position, access));
        if(null != _position.getZ()) returnValue.getPosition().setZ(_position.getZ());
        final boolean isOwner = (null != _owner) && access.equals(_owner);
        LOG.finer(String.format("%s ?= %s == %b", access, _owner, isOwner));
        returnValue.setMovable(false);
        if (isOwner)
        {
            if (_movable)
            {
                returnValue.setMovable(true);
                for (final Entry<IndexPosition, String> entry : _moveCommands.entrySet())
                {
                    final Position p1 = parametricTokenGame.getPosition(entry.getKey(), access);
                    final com.sawdust.engine.view.geometry.Position p = p1;
                    returnValue.getMoveCommands().put(p, entry.getValue());
                }
            }
        }
        else
        {
            if (!isPublic())
            {
                if ((null == baseImageId) || baseImageId.isEmpty()) 
                {
                    LOG.finer(String.format("Fully Hidden!"));
                    return null;
                }
                else
                {
                    LOG.finer(String.format("Public Art: %s", baseImageId));
                    returnValue.setBaseImageId(baseImageId);
                    returnValue.setText("");
                }
            }
        }
        LOG.finer(String.format("Final Art: %s", returnValue.getBaseImageId()));
        return returnValue;
    }

    private Object writeReplace()
    {
        return new SerialForm(this);
    }

}
