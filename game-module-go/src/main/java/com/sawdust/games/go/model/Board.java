package com.sawdust.games.go.model;

import java.util.Arrays;
import java.util.HashSet;

import com.sawdust.games.go.view.XmlBoard;


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
                for (Island i2 : i.remove(newPosition))
                {
                    newOpen.add(i2);
                }
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
                Island[] split = i.remove(newPosition);
                for(Island toAdd : split) newIslands.add(toAdd);
            }
            else if (!player.isNull() && i.isNeigbor(newPosition))
            {
                if (i.player == player)
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
            targetPool.add(new Island(selfJoined.iterator().next(), newPosition));
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
        result = prime * result + Arrays.hashCode(islands);
        result = prime * result + Arrays.hashCode(open);
        result = prime * result + rows;
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
        if (!Arrays.equals(islands, other.islands)) return false;
        if (!Arrays.equals(open, other.open)) return false;
        if (rows != other.rows) return false;
        return true;
    }


}
