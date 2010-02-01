package com.sawdust.games.stop.immutable;

import java.util.HashSet;
import java.util.logging.Logger;

public class Board
{
    private static final Logger LOG = Logger.getLogger(Board.class.getName());
    public static final Player EMPTY_VALUE = new Player();

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

    public Board(final Board obj, final Player player, TokenPosition newPosition)
    {
        cols = obj.cols;
        rows = obj.rows;

        HashSet<Island> newOpen = new HashSet<Island>();
        Island whitespaceSource = null;
        for (Island i : obj.open)
        {
            if (i.contains(newPosition))
            {
                whitespaceSource = i;
                for (Island i2 : i.remove(newPosition))
                {
                    newOpen.add(i2);
                }
            }
            else
            {
                newOpen.add(i);
            }
        }
        open = newOpen.toArray(new Island[]{});

        HashSet<Island> selfJoined = new HashSet<Island>();
        HashSet<Island> enemyFacing = new HashSet<Island>();
        HashSet<Island> newIslands = new HashSet<Island>();
        for (Island i : obj.islands)
        {
            if (i.isNeigbor(newPosition))
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
        if(selfJoined.size() > 1)
        {
            newIslands.add(new Island(newPosition, selfJoined.toArray(new Island[]{})));
        }
        else if(selfJoined.size() == 1)
        {
            newIslands.add(new Island(selfJoined.iterator().next(), newPosition));
        }
        else
        {
            newIslands.add(new Island(player, newPosition));
        }
        islands = newIslands.toArray(new Island[]{});
    }

    public Board doMove(TokenMove move)
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
        Player[][] matrix = from.getMatrix();
        for(int x=0;x<from.rows;x++)
            for(int y=0;y<from.cols;y++)
            {
                temp = temp.doMove(new TokenMove(matrix[x][y], new TokenPosition(x, y)));
            }
        return temp;
    }

    public int islandCount(Player p1)
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
}
