package com.sawdust.engine.view.geometry;

public final class Position implements IPosition
{
    private int _x;
    private int _y;
    private int _z = 0;

    public Position()
    {
        super();
    }

    public Position(final int x, final int y)
    {
        super();
        _x = x;
        _y = y;
    }

    public Position add(final Vector v)
    {
        return new Position(v.getX() + getX(), v.getY() + getY());
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final Position other = (Position) obj;
        if (_x != other._x) return false;
        if (_y != other._y) return false;
        if (_z != other._z) return false;
        return true;
    }

    public double getDistance(final int x, final int y)
    {
        final int dy = y - _y;
        final int dx = x - _x;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public int getX()
    {
        return _x;
    }

    public int getY()
    {
        return _y;
    }

    public int getZ()
    {
        return _z;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + _x;
        result = prime * result + _y;
        result = prime * result + _z;
        return result;
    }

    public void setX(final int x)
    {
        _x = x;
    }

    public void setY(final int y)
    {
        _y = y;
    }

    public Position setZ(final int z)
    {
        _z = z;
        return this;
    }

    @Override
    public String toString()
    {
        return "[X=" + _x + ";Y=" + _y + ";Z=" + _z + "]";
    }

    public Vector vectorTo(final Position to)
    {
        return new Vector(to.getX() - getX(), to.getY() - getY());
    }

}
