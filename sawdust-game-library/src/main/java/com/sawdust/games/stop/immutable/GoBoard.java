package com.sawdust.games.stop.immutable;

import java.io.File;
import java.util.HashSet;
import java.util.logging.Logger;

public class GoBoard
{
    private static final Logger LOG = Logger.getLogger(GoBoard.class.getName());
    public static final int EMPTY_VALUE = -1;

    final Board board;

    GoBoard(final Board b)
    {
        board = b;
    }

    public GoBoard(final Board b, final int player, final tokenPosition position)
    {
        board = new Board(b, player, position);
    }

    public tokenMove[] getMoves(int player)
    {
        HashSet<tokenMove> moves = new HashSet<tokenMove>();
        for (island i : board.open)
        {
            for (tokenPosition p : i.tokens)
                moves.add(new tokenMove(player, p));
        }
        return null;
    }

    public GoBoard doMove(tokenMove move)
    {
        return new GoBoard(board, move.player, move.position);
    }

    public void toFile(File out)
    {
        throw new NotImplemented();
    }

    public String toXmlString()
    {
        throw new NotImplemented();
    }

    public static GoBoard fromFile(File in)
    {
        throw new NotImplemented();
    }

    public GoBoard fromXmlString(String str)
    {
        throw new NotImplemented();
    }
}
