package com.sawdust.games.stop.immutable;

import java.util.HashSet;


public class Board
{
    public static final GoPlayer EMPTY_VALUE = new GoPlayer();

    final Island[] islands;
    final Island[] open;
    final int cols;
    final int rows;

    public Board(final int r, final int c)
    {
        cols = c;
        rows = r;
        islands = new Island[]{};
        open = new Island[]{ new Island(EMPTY_VALUE, r, cols) };
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
            targetPool.add(new Island(newPosition, selfJoined.toArray(new Island[]{})));
        }
        else if(selfJoined.size() == 1)
        {
            targetPool.add(new Island(selfJoined.iterator().next(), newPosition));
        }
        else
        {
            targetPool.add(new Island(player, newPosition));
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
                temp = temp.doMove(new BoardMove(matrix[x][y], new BoardPosition(x, y)));
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
}
