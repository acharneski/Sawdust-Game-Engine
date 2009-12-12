package com.sawdust.engine.game.state;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.sawdust.engine.common.geometry.Position;
import com.sawdust.engine.game.TokenGame;
import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.game.players.Player;
import com.sawdust.engine.service.debug.GameException;

public class Token implements Serializable
{
    private static final Logger LOG = Logger.getLogger(Token.class.getName());
    protected String _art = "";
    private String _text = "";
    protected int _id = 0;
    protected boolean _movable = false;
    private final HashMap<IndexPosition, String> _moveCommands = new HashMap<IndexPosition, String>();
    protected Participant _owner = null;
    private IndexPosition _position = null;
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
        if (baseImageId.equals(other.baseImageId)) return false;
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

    /**
     * @return the _art
     */
    public String getArt()
    {
        return _art;
    }

    /**
     * @return the _id
     */
    public int getId()
    {
        return _id;
    }

    public HashMap<IndexPosition, String> getMoveCommands()
    {
        return _moveCommands;
    }

    /**
     * @return the _owner
     */
    public Participant getOwner()
    {
        return _owner;
    }

    public IndexPosition getPosition()
    {
        return _position;
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

    /**
     * @return the _movable
     */
    public boolean isMovable()
    {
        return _movable;
    }

    /**
     * @return the _isPublic
     */
    public boolean isPublic()
    {
        return (null == baseImageId);
    }

    public void setArt(final String art)
    {
        _art = art;

    }

    /**
     * @param _movable
     *            the _movable to set
     */
    public void setMovable(final boolean movable)
    {
        _movable = movable;
    }

    /**
     * @param _owner
     *            the _owner to set
     * @return 
     */
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

    public com.sawdust.engine.common.game.Token toGwt(final Player access, final TokenGame parametricTokenGame) throws GameException
    {
        final com.sawdust.engine.common.game.Token returnValue = new com.sawdust.engine.common.game.Token(_id, imageLibraryId, _art);
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
                    final com.sawdust.engine.common.geometry.Position p = p1;
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

    public void setText(String _text)
    {
        this._text = _text;
    }

    public String getText()
    {
        return _text;
    }

}
