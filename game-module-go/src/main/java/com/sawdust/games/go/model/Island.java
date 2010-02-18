/**
 * 
 */
package com.sawdust.games.go.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;


public class Island
{
    public final GoPlayer player;
    public final BoardPosition[] tokens;

    static HashMap<Island, Island> objectCache = new HashMap<Island, Island>();

    public static Island Get(final GoPlayer p, final BoardPosition... t)
    {
        Island island = new Island(p, t);
        if (objectCache.containsKey(island)) return objectCache.get(island);
        objectCache.put(island, island);
        return island;
    }

    private Island(final GoPlayer p, final BoardPosition... t)
    {
        super();
        if (null == t) throw new NullPointerException();
        for (BoardPosition tok : t)
            if (null == tok) throw new NullPointerException();
        tokens = t;
        player = p;
    }

    public static Island Get(final com.sawdust.games.go.model.GoPlayer p, final int rows, final int cols)
    {
        Island island = new Island(p, rows, cols);
        if (objectCache.containsKey(island)) return objectCache.get(island);
        objectCache.put(island, island);
        return island;
    }

    private Island(final com.sawdust.games.go.model.GoPlayer p, final int rows, final int cols)
    {
        super();
        player = p;

        tokens = new BoardPosition[rows * cols];
        int pos = 0;
        for (int x = 0; x < rows; x++)
            for (int y = 0; y < cols; y++)
                tokens[pos++] = BoardPosition.Get(x, y);
    }

    public static Island Get(final BoardPosition join, final Island... sourceIslands)
    {
        Island island = new Island(join, sourceIslands);
        if (objectCache.containsKey(island)) return objectCache.get(island);
        objectCache.put(island, island);
        return island;
    }

    private Island(final BoardPosition join, final Island... sourceIslands)
    {
        super();
        if (null == join) throw new NullPointerException();
        int size = 1;
        for (Island i : sourceIslands)
            size += i.tokens.length;
        tokens = new BoardPosition[size];
        int pos = 0;
        tokens[pos++] = join;
        for (Island i : sourceIslands)
            for (BoardPosition p : i.tokens)
                tokens[pos++] = p;
        player = sourceIslands[0].player;
    }

    public static Island Get( Island i, final BoardPosition... t)
    {
        Island island = new Island(i,t);
        if (objectCache.containsKey(island)) return objectCache.get(island);
        objectCache.put(island, island);
        return island;
    }

    private Island(final Island i, final BoardPosition... t)
    {
        super();
        int oldLength = i.tokens.length;
        int newLength = oldLength + t.length;
        player = i.player;
        tokens = Arrays.copyOf(i.tokens, newLength);
        for (int j = 0; j < t.length; j++)
        {
            assert (null != t[j]);
            tokens[oldLength + j] = t[j];
            assert (i.isNeigbor(t[j]));
        }
    }

    public Island[] split(final BoardPosition t)
    {
        assert (this.contains(t));
        TreeSet<BoardPosition> positions = new TreeSet<BoardPosition>();
        for (BoardPosition p : tokens)
            positions.add(p);
        positions.remove(t);
        return buildIslands(this.player, positions);
    }

    public static Island[] buildIslands(final GoPlayer player2, Set<BoardPosition> positions)
    {
        HashSet<Island> newIslands = new HashSet<Island>();
        while (!positions.isEmpty())
        {
            Island isl = null;
            while (!positions.isEmpty())
            {
                int anythingChanged = 0;
                TreeSet<BoardPosition> newP = new TreeSet<BoardPosition>();
                for (BoardPosition p : positions)
                {
                    if (null == isl)
                    {
                        isl = Island.Get(player2, p);
                        positions.remove(p);
                        anythingChanged++;
                        break;
                    }
                    else if (isl.isNeigbor(p))
                    {
                        newP.add(p);
                        anythingChanged++;
                    }
                }
                assert (null != isl);
                if (!newP.isEmpty())
                {
                    positions.removeAll(newP);
                    isl = Island.Get(isl, newP.toArray(new BoardPosition[] {}));
                }
                else if (0 == anythingChanged)
                {
                    break; // No more adjacent pieces
                }
            }
            if(null != isl)
            {
                newIslands.add(isl);
                isl = null;
            }
        }
        Island[] array = newIslands.toArray(new Island[] {});
        return array;
    }

    public boolean isNeigbor(BoardPosition t)
    {
        if (null == t) return false;
        boolean isNieghbor = false;
        boolean isInside = false;
        for (int j = 0; j < tokens.length; j++)
        {
            BoardPosition token = tokens[j];
            if (token.isNeigbor(t)) isNieghbor = true;
            if (token.equals(t)) isInside = true;
        }
        return isNieghbor && !isInside;
    }

    public boolean contains(BoardPosition t)
    {
        boolean isInside = false;
        for (int j = 0; j < tokens.length; j++)
        {
            BoardPosition token = tokens[j];
            if (token.equals(t)) isInside = true;
        }
        return isInside;
    }

    transient int hashCode = 0;
    @Override
    public int hashCode()
    {
        if(hashCode != 0) return hashCode;
        int result = 0;
        for (BoardPosition t : tokens)
            result ^= t.hashCode();
        result = result * player.hashCode();
        hashCode = result;
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Island other = (Island) obj;
        if (player != other.player) return false;
        if (tokens.length != other.tokens.length) return false;
        for (BoardPosition t : other.tokens)
            if (!contains(t)) return false;
        return true;
    }

    private final HashMap<Island, Boolean> nearby = new HashMap<Island, Boolean>();
    public boolean isNeigbor(Island o)
    {
        if(nearby.containsKey(o)) return nearby.get(o);
        boolean returnValue = false;
        for (BoardPosition p : o.tokens)
        {
            if (isNeigbor(p))
            {
                returnValue = true;
            }
        }
        nearby.put(o, returnValue);
        return returnValue;
    }

}