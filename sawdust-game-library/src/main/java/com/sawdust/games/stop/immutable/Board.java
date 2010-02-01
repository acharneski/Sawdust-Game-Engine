package com.sawdust.games.stop.immutable;

import java.util.HashSet;
import java.util.logging.Logger;

public class Board
{
    private static final Logger LOG = Logger.getLogger(Board.class.getName());
    public static final player EMPTY_VALUE = new player();

    final island[] islands;
    final island[] open;
    final int cols;
    final int rows;

    public Board(final int r, final int c)
    {
        cols = c;
        rows = r;
        islands = new island[]{};
        open = new island[]{ new island(EMPTY_VALUE, r, cols) };
    }

    public Board(final Board obj, final player player, tokenPosition newPosition)
    {
        cols = obj.cols;
        rows = obj.rows;

        HashSet<island> newOpen = new HashSet<island>();
        island whitespaceSource = null;
        for (island i : obj.open)
        {
            if (i.contains(newPosition))
            {
                whitespaceSource = i;
                for (island i2 : i.remove(newPosition))
                {
                    newOpen.add(i2);
                }
            }
            else
            {
                newOpen.add(i);
            }
        }
        open = newOpen.toArray(new island[]{});

        HashSet<island> selfJoined = new HashSet<island>();
        HashSet<island> enemyFacing = new HashSet<island>();
        HashSet<island> newIslands = new HashSet<island>();
        for (island i : obj.islands)
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
            newIslands.add(new island(newPosition, selfJoined.toArray(new island[]{})));
        }
        else if(selfJoined.size() == 1)
        {
            newIslands.add(new island(selfJoined.iterator().next(), newPosition));
        }
        else
        {
            newIslands.add(new island(player, newPosition));
        }
        islands = newIslands.toArray(new island[]{});
    }

    public Board doMove(tokenMove move)
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
        player[][] matrix = from.getMatrix();
        for(int x=0;x<from.rows;x++)
            for(int y=0;y<from.cols;y++)
            {
                temp = temp.doMove(new tokenMove(matrix[x][y], new tokenPosition(x, y)));
            }
        return temp;
    }

    public int islandCount(player p1)
    {
        int cnt = 0;
        for (island i : islands)
        {
            if (i.player == p1)
            {
                cnt++;
            }
        }
        return cnt;
    }
}
