package com.sawdust.engine.model.state;

import java.io.Serializable;

public class IndexPosition implements Serializable
{
    private int _cardIndex;
    private int _curveIndex;
    private Integer _z = null;

    protected IndexPosition()
    {
        super();
    }

    public IndexPosition(final int curve, final int card)
    {
        super();
        _curveIndex = curve;
        _cardIndex = card;
    }

    public IndexPosition(int i, int j, int z)
    {
        this(i,j);
        _z = z;
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

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + _cardIndex;
        result = prime * result + _curveIndex;
        return result;
    }

    public void setCardIndex(final int cardIndex)
    {
        _cardIndex = cardIndex;
    }

    public void setCurveIndex(final int curveIndex)
    {
        _curveIndex = curveIndex;
    }

    public void setZ(Integer _z)
    {
        this._z = _z;
    }

    public Integer getZ()
    {
        return (null==_z)?null:new Integer(_z);
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

}
