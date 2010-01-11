package com.sawdust.engine.model.state;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class IndexPosition implements Serializable
{
    protected static class SerialForm implements Serializable
    {
        int _cardIndex;
        int _curveIndex;
        Integer _z = null;
        
        protected SerialForm(){}
        protected SerialForm(IndexPosition obj)
        {
            _cardIndex = obj._cardIndex;
            _curveIndex = obj._curveIndex;
            _z = obj._z;
        }
        private Object readResolve()
        {
            return new IndexPosition(this);
        }
    }

    public final int _cardIndex;
    public final int _curveIndex;
    public final Integer _z;

    public IndexPosition(final int curve, final int card)
    {
        super();
        _curveIndex = curve;
        _cardIndex = card;
        _z = 0;
    }

    public IndexPosition(int i, int j, int z)
    {
        _curveIndex = i;
        _cardIndex = j;
        _z = z;
    }

    public IndexPosition(SerialForm obj)
    {
        _cardIndex = obj._cardIndex;
        _curveIndex = obj._curveIndex;
        _z = obj._z;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final IndexPosition other = (IndexPosition) obj;
        if (_cardIndex != other._cardIndex) return false;
        if (_curveIndex != other._curveIndex) return false;
        return true;
    }

    public int getCardIndex()
    {
        return _cardIndex;
    }

    public int getCurveIndex()
    {
        return _curveIndex;
    }

    public Integer getZ()
    {
        return (null==_z)?null:new Integer(_z);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + _cardIndex;
        result = prime * result + _curveIndex;
        return result;
    }

    public boolean is2dAdjacentTo(IndexPosition indexPosition)
    {
        int dx = _cardIndex - indexPosition._cardIndex;
        int dy = _curveIndex - indexPosition._curveIndex;
        if(dx>1) return false;
        if(dx<-1) return false;
        if(dy>1) return false;
        if(dy<-1) return false;
        if(dx==0 && dy==0) return false;
        return true;
    }

    public IndexPosition setZ(Integer z)
    {
        SerialForm serialForm = new SerialForm(this);
        serialForm._z = z;
        return new IndexPosition(serialForm);
    }

    private Object writeReplace()
    {
        return new SerialForm(this);
    }

    private void readObject(ObjectInputStream s) throws  IOException, ClassNotFoundException
    {
        throw new NotSerializableException();
    }

}
