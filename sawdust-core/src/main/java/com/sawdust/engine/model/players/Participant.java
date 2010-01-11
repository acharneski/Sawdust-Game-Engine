package com.sawdust.engine.model.players;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;

import com.sawdust.engine.model.state.IndexPosition;

public class Participant implements Serializable
{
    protected static class SerialForm implements Serializable
    {
        String _id;
        
        protected SerialForm(){}
        protected SerialForm(Participant obj)
        {
            _id = obj._id;
        }
        private Object readResolve()
        {
            return new Participant(this);
        }
    }

    private void readObject(ObjectInputStream s) throws  IOException, ClassNotFoundException
    {
        throw new NotSerializableException();
    }

    private Object writeReplace()
    {
        return new SerialForm(this);
    }

    protected String _id;

    protected Participant(SerialForm obj)
    {
        _id = obj._id;
    }

    protected Participant(final String id)
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
