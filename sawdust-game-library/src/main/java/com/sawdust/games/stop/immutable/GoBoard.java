package com.sawdust.games.stop.immutable;

import java.io.Serializable;
import java.util.HashSet;
import java.util.logging.Logger;

public class GoBoard implements Serializable
{
    private static final Logger LOG = Logger.getLogger(GoBoard.class.getName());
    public static final int EMPTY_VALUE = -1;

    final island[] _islands;
    final island[] _open;
    final int _numCols;
    final int _numRows;

    public GoBoard(final int rows, final int cols)
    {
        _numCols = cols;
        _numRows = rows;
        _islands = new island[]{};
        _open = new island[]{ new island(EMPTY_VALUE, rows, cols) };
    }

    public GoBoard(final GoBoard obj, final int player, tokenPosition newPosition)
    {
        _numCols = obj._numCols;
        _numRows = obj._numRows;

        HashSet<island> open = new HashSet<island>();
        island whitespaceSource = null;
        for (island i : obj._open)
        {
            if (i.contains(newPosition))
            {
                whitespaceSource = i;
                for (island i2 : i.remove(newPosition))
                {
                    open.add(i2);
                }
            }
            else
            {
                open.add(i);
            }
        }
        _open = open.toArray(new island[]{});

        HashSet<island> selfJoined = new HashSet<island>();
        HashSet<island> enemyFacing = new HashSet<island>();
        HashSet<island> newIslands = new HashSet<island>();
        for (island i : obj._islands)
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
        newIslands.add(new island(newPosition, selfJoined.toArray(new island[]{})));
        _islands = newIslands.toArray(new island[]{});
    }

    public tokenMove[] getMoves(int player)
    {
        HashSet<tokenMove> moves = new HashSet<tokenMove>();
        for(island i : _open) 
        {
            for(tokenPosition p : i.tokens) moves.add(new tokenMove(player, p));
        }
        return null;
    }

    public GoBoard doMove(tokenMove move)
    {
        return new GoBoard(this, move.player, move.position);
    }

}
