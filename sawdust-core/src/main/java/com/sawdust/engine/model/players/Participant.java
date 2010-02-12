package com.sawdust.engine.model.players;

import java.io.Serializable;

public class Participant implements Serializable
{
    protected String _id;

    protected Participant()
    {
    }

    public Participant(final String id)
    {
        super();
        _id = id;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final Participant other = (Participant) obj;
        if (_id == null)
        {
            if (other._id != null) return false;
        }
        else if (!_id.equals(other._id)) return false;
        return true;
    }

    public String getId()
    {
        return _id;
    }

    @Override
    public int hashCode()
    {
        return _id.hashCode();
    }

}
