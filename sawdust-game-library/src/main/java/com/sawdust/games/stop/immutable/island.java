/**
 * 
 */
package com.sawdust.games.stop.immutable;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

class island
{
    final int player;
    final tokenPosition[] tokens;

    public island(final int p, final tokenPosition... t)
    {
        super();
        tokens = t;
        player = p;
    }

    public island(final int p, final int rows, final int cols)
    {
        super();
        player = p;
        
        tokens = new tokenPosition[rows*cols];
        int pos = 0;
        for(int x=0;x<rows;x++) for(int y=0;y<cols;y++) tokens[pos++] = new tokenPosition(x, y);
    }

    public island(final tokenPosition join, final island... sourceIslands)
    {
        super();
        int size = 1;
        for(island i : sourceIslands) size += i.tokens.length;
        tokens = new tokenPosition[size];
        int pos = 0;
        tokens[pos++] = join;
        for(island i : sourceIslands) for(tokenPosition p : i.tokens) tokens[pos++] = p;
        player = sourceIslands[0].player;
    }

    public island[] remove(final tokenPosition t)
    {
        assert (this.contains(t));
        TreeSet<tokenPosition> positions = new TreeSet<tokenPosition>();
        for (tokenPosition p : tokens)
            positions.add(p);
        positions.remove(t);
        return buildIslands(this.player, positions);
    }

    public static island[] buildIslands(final int player, Set<tokenPosition> positions)
    {
        TreeSet<island> newIslands = new TreeSet<island>();
        island isl = null;
        while (!positions.isEmpty())
        {
            int anythingChanged = 0;
            while (!positions.isEmpty())
            {
                TreeSet<tokenPosition> newP = new TreeSet<tokenPosition>();
                for (tokenPosition p : positions)
                {
                    if (null == isl)
                    {
                        isl = new island(player, p);
                    }
                    else if (isl.isNeigbor(p))
                    {
                        newP.add(p);
                    }
                }
                positions.removeAll(newP);
                if (!newP.isEmpty())
                {
                    isl = new island(isl, newP.toArray(new tokenPosition[] {}));
                    anythingChanged++;
                    break;
                }
            }
            if (0 == anythingChanged)
            {
                newIslands.add(isl);
                isl = null;
            }
        }
        island[] array = newIslands.toArray(new island[] {});
        return array;
    }

    public island(final island i, final tokenPosition... t)
    {
        super();
        int oldLength = i.tokens.length;
        int newLength = oldLength + t.length;
        player = i.player;
        tokens = Arrays.copyOf(i.tokens, newLength);
        for (int j = 0; j < t.length; j++)
        {
            tokens[oldLength + j] = t[j];
            assert (i.isNeigbor(t[j]));
        }
    }

    public boolean isNeigbor(tokenPosition t)
    {
        boolean isNieghbor = false;
        boolean isInside = false;
        for (int j = 0; j < tokens.length; j++)
        {
            tokenPosition token = tokens[j];
            if (token.isNeigbor(t)) isNieghbor = true;
            if (token.equals(t)) isInside = true;
        }
        return isNieghbor && !isInside;
    }

    public boolean contains(tokenPosition t)
    {
        boolean isInside = false;
        for (int j = 0; j < tokens.length; j++)
        {
            tokenPosition token = tokens[j];
            if (token.equals(t)) isInside = true;
        }
        return isInside;
    }

    @Override
    public int hashCode()
    {
        int result = 0;
        for(tokenPosition t : tokens) result ^= t.hashCode();
        result = result * player;
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        island other = (island) obj;
        if (player != other.player) return false;
        if (tokens.length != other.tokens.length) return false;
        for(tokenPosition t : other.tokens) if(!contains(t)) return false;
        return true;
    }
    
}