package com.sawdust.engine.view.geometry;

import java.io.Serializable;

public class Vector implements Serializable
{
    private int _x;
    private int _y;

    public Vector()
    {
        super();
    }

    public Vector(final int x, final int y)
    {
        super();
        _x = x;
        _y = y;
    }

    public int getX()
    {
        return _x;
    }

    public int getY()
    {
        return _y;
    }

    public Vector scale(final double scaleFactor)
    {
        return new Vector((int) (_x * scaleFactor), (int) (_y * scaleFactor));
    }

}
