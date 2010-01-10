package com.sawdust.engine.view.geometry;

public final class ParametricLine extends ParametricPosition
{
    private Position _start;
    private Vector _vector;

    public ParametricLine()
    {
        super();
    }

    public ParametricLine(final Position start, final Position end, final int count)
    {
        super(0.0, 1.0, count);
        _start = start;
        _vector = start.vectorTo(end);
        setTBounds(0.0, 1.0);
    }

    @Override
    public Position getPositionT(final double t)
    {
        return _start.add(_vector.scale(t));
    }

}
