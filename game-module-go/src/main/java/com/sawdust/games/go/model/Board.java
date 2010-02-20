package com.sawdust.games.go.model;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.sawdust.games.go.view.XmlBoard;
import com.sawdust.games.go.view.XmlGoBoard;


public class Board
{
    public static final GoPlayer EMPTY_VALUE = new GoPlayer();

    public final Island[] islands;
    public final Island[] open;
    public final int cols;
    public final int rows;

    public Board(final int r, final int c)
    {
        cols = c;
        rows = r;
        islands = new Island[]{};
        open = new Island[]{ Island.Get(EMPTY_VALUE, r, cols) };
    }

    public Board(final Board obj, final GoPlayer player, BoardPosition newPosition)
    {
        cols = obj.cols;
        rows = obj.rows;

        HashSet<Island> newOpen = new HashSet<Island>();
        HashSet<Island> selfJoined = new HashSet<Island>();
        HashSet<Island> enemyFacing = new HashSet<Island>();
        HashSet<Island> newIslands = new HashSet<Island>();

        for (Island i : obj.open)
        {
            if (!player.isNull() && i.contains(newPosition))
            {
                for (Island i2 : i.split(newPosition)) newOpen.add(i2);
            }
            else if (player.isNull() && i.isNeigbor(newPosition))
            {
                selfJoined.add(i);
            }
            else
            {
                newOpen.add(i);
            }
        }

        for (Island i : obj.islands)
        {
            if(player.isNull() && i.contains(newPosition))
            {
                for(Island toAdd : i.split(newPosition)) newIslands.add(toAdd);
            }
            else if (!player.isNull() && i.isNeigbor(newPosition))
            {
                if (i.player.equals(player))
                {
                    selfJoined.add(i);
                }
                else
                {
                    enemyFacing.add(i);
                    newIslands.add(i);
                }
            }
            else
            {
                newIslands.add(i);
            }
        }

        HashSet<Island> targetPool = (player.isNull())?newOpen:newIslands;
        if(selfJoined.size() > 1)
        {
            targetPool.add(Island.Get(newPosition, selfJoined.toArray(new Island[]{})));
        }
        else if(selfJoined.size() == 1)
        {
            targetPool.add(Island.Get(selfJoined.iterator().next(), newPosition));
        }
        else
        {
            targetPool.add(Island.Get(player, newPosition));
        }
        islands = newIslands.toArray(new Island[]{});
        open = newOpen.toArray(new Island[]{});
    }

    public Board doMove(BoardMove move)
    {
        return new Board(this, move.player, move.position);
    }

    public XmlBoard getXmlObj()
    {
        return new XmlBoard(this);
    }

    public static Board unmarshal(XmlBoard from)
    {
        if(null == from) return null;
        Board temp = new Board(from.rows, from.cols);
        GoPlayer[][] matrix = from.getMatrix();
        for(int x=0;x<from.rows;x++)
            for(int y=0;y<from.cols;y++)
            {
                GoPlayer player = matrix[x][y];
                if(!player.isNull()) temp = temp.doMove(new BoardMove(player, BoardPosition.Get(x, y), null));
            }
        return temp;
    }

    public int islandCount(GoPlayer p1)
    {
        int cnt = 0;
        for (Island i : islands)
        {
            if (i.player == p1)
            {
                cnt++;
            }
        }
        return cnt;
    }

    public Board remove(BoardPosition p)
    {
        return new Board(this, new GoPlayer(), p);
    }


    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + cols;
        result = prime * result + rows;
        for (Island x : islands)
            result ^= x.hashCode();
        for (Island x : open)
            result ^= x.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Board other = (Board) obj;
        if (cols != other.cols) return false;
        if (rows != other.rows) return false;
        if (!unorderedEquals(islands, other.islands)) return false;
        if (!unorderedEquals(open, other.open)) return false;
        return true;
    }

    private <T> boolean unorderedEquals(T[] a, T[] b)
    {
        for(T i : a)
        {
            boolean found = false;
            for(T j : b)
            {
                if(j.equals(i)) 
                {
                    found = true;
                    break;
                }
            }
            if(!found) return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return toXmlString();
    }

    public String toXmlString()
    {
        StringWriter buffer = new StringWriter();
        try
        {
            JAXBContext.newInstance(XmlBoard.class).createMarshaller().marshal(new XmlBoard(this), buffer);
        }
        catch (JAXBException e)
        {
            throw new RuntimeException(e);
        }
        return buffer.toString();
    }

}