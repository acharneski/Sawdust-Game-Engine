/**
 * 
 */
package com.sawdust.games.stop.immutable;

import java.util.HashMap;
import java.util.HashSet;

public class BoardPosition implements Comparable<BoardPosition>
{
    final int x;
    final int y;

    private BoardPosition(int x, int y)
    {
        super();
        this.x = x;
        this.y = y;
    }

    static HashMap<BoardPosition,BoardPosition> objectCache = new HashMap<BoardPosition,BoardPosition>();
    public static BoardPosition Get(int x, int y)
    {
        BoardPosition key = new BoardPosition(x, y);
        if(objectCache.containsKey(key )) return objectCache.get(key);
        objectCache.put(key,key);
        return key;
    }

    private final HashMap<BoardPosition,Boolean> nearby = new HashMap<BoardPosition, Boolean>();
    public boolean isNeigbor(BoardPosition t)
    {
        if(nearby.containsKey(t)) return nearby.get(t);
        boolean a = x == t.x;
        boolean b = y == t.y;
        Boolean retVal = null;
        if (a && b) 
        {
            retVal = false;
        }
        else if (!a && !b) 
        {
            retVal = false;
        }
        else if (b && (x + 1 == t.x || x - 1 == t.x)) 
        {
            retVal = true;
        }
        else if (a && (y + 1 == t.y || y - 1 == t.y))
        {
            retVal = true;
        }
        else
        {
            retVal = false;
        }
        nearby.put(t, retVal);
        return retVal;
    }

    transient int hashCode = 0;
    @Override
    public int hashCode()
    {
        if(hashCode != 0) return hashCode;
        final int prime = 103;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        hashCode = result;
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        BoardPosition other = (BoardPosition) obj;
        if (x != other.x) return false;
        if (y != other.y) return false;
        return true;
    }

    @Override
    public int compareTo(BoardPosition o)
    {
        if (x < o.x) return -1;
        if (x > o.x) return 1;
        if (y < o.y) return -1;
        if (y > o.y) return 1;
        return 0;
    }

    @Override
    public String toString()
    {
        return "BoardPosition [x=" + x + ", y=" + y + "]";
    }

}