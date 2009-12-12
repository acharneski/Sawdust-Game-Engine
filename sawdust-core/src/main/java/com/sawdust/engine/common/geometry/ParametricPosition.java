package com.sawdust.engine.common.geometry;

import java.io.Serializable;

public abstract class ParametricPosition implements Serializable
{
    private double _maxT = 1.0;
    private double _minT = 0.0;
    private int _positionCount = 2;

    public ParametricPosition()
    {
    }

    public ParametricPosition(final double maxT, final double minT, final int count)
    {
        super();
        _maxT = maxT;
        _minT = minT;
        _positionCount = count;
    }

    public int getDivisions()
    {
        return _positionCount;
    }

    public double getMaxT()
    {
        return _maxT;
    }

    public double getMinT()
    {
        return _minT;
    }

    public Position getPositionN(final int n)
    {
        return getPositionT(getTn(n));
    }

    public abstract Position getPositionT(double t);

    public double getTn(final int n)
    {
        return _minT + n * ((_maxT - _minT) / _positionCount);
    }

    public void setDivisions(final int positionCount)
    {
        if (0 >= positionCount) throw new IllegalArgumentException("positionCount must be positive");
        _positionCount = positionCount;
    }

    public void setMaxT(final double maxT)
    {
        if (maxT < _minT) throw new IllegalArgumentException("maxT < minT");
        _maxT = maxT;
    }

    public void setMinT(final double minT)
    {
        if (_maxT < minT) throw new IllegalArgumentException("minT > maxT");
        _minT = minT;
    }

    public void setTBounds(final double minT, final double maxT)
    {
        if (maxT < minT) throw new IllegalArgumentException("minT > maxT");
        _minT = minT;
        _maxT = maxT;
    }

}
