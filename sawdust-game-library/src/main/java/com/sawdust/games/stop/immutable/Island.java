/**
 * 
 */
package com.sawdust.games.stop.immutable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;


class Island
{
    final GoPlayer player;
    final BoardPosition[] tokens;

    public Island(final GoPlayer p, final BoardPosition... t)
    {
        super();
        tokens = t;
        player = p;
    }

    public Island(final com.sawdust.games.stop.immutable.GoPlayer p, final int rows, final int cols)
    {
        super();
        player = p;
        
        tokens = new BoardPosition[rows*cols];
        int pos = 0;
        for(int x=0;x<rows;x++) for(int y=0;y<cols;y++) tokens[pos++] = new BoardPosition(x, y);
    }

    public Island(final BoardPosition join, final Island... sourceIslands)
    {
        super();
        int size = 1;
        for(Island i : sourceIslands) size += i.tokens.length;
        tokens = new BoardPosition[size];
        int pos = 0;
        tokens[pos++] = join;
        for(Island i : sourceIslands) for(BoardPosition p : i.tokens) tokens[pos++] = p;
        player = sourceIslands[0].player;
    }

    public Island[] remove(final BoardPosition t)
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
                        isl = new Island(player2, p);
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
                assert(null != isl);
                if (!newP.isEmpty())
                {
                    positions.removeAll(newP);
                    isl = new Island(isl, newP.toArray(new BoardPosition[] {}));
                    break;
                }
                else if(0 == anythingChanged)
                {
                    break; // No more adjacent pieces 
                }
            }
            assert(null != isl);
            newIslands.add(isl);
        }
        Island[] array = newIslands.toArray(new Island[] {});
        return array;
    }

    public Island(final Island i, final BoardPosition... t)
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

    public boolean isNeigbor(BoardPosition t)
    {
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

    @Override
    public int hashCode()
    {
        int result = 0;
        for(BoardPosition t : tokens) result ^= t.hashCode();
        result = result * player.hashCode();
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
        for(BoardPosition t : other.tokens) if(!contains(t)) return false;
        return true;
    }


    public boolean isNeigbor(Island o)
    {
        for(BoardPosition p : o.tokens)
        {
            if(isNeigbor(p)) return true;
        }
        return false;
    }
    
}