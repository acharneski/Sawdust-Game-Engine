/**
 * 
 */
package com.sawdust.games.stop.immutable;

class tokenPosition implements Comparable<tokenPosition>
{
    final int x;
    final int y;

    public tokenPosition(int x, int y)
    {
        super();
        this.x = x;
        this.y = y;
    }

    public boolean isNeigbor(tokenPosition t)
    {
        boolean a = x == t.x;
        boolean b = y == t.y;
        if (a && b) return false;
        if (!a && !b) return false;
        if (b && (x + 1 == t.x || x - 1 == t.x)) return true;
        if (a && (y + 1 == t.y || y - 1 == t.y)) return true;
        return false;
    }

    @Override
    public int hashCode()
    {
        final int prime = 103;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        tokenPosition other = (tokenPosition) obj;
        if (x != other.x) return false;
        if (y != other.y) return false;
        return true;
    }

    @Override
    public int compareTo(tokenPosition o)
    {
        if (x < o.x) return -1;
        if (y < o.y) return -1;
        if (x > o.x) return 1;
        if (y > o.y) return 1;
        return 0;
    }
}