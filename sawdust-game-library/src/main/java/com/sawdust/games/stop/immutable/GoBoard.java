package com.sawdust.games.stop.immutable;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class GoBoard
{
    private static final Logger LOG = Logger.getLogger(GoBoard.class.getName());
    public static final int EMPTY_VALUE = -1;

    final Board board;

    GoBoard(final Board b)
    {
        board = b;
    }

    public GoBoard(final Board b, final Player player, final TokenPosition position)
    {
        board = new Board(b, player, position);
    }

    public GoBoard()
    {
        board = new Board(9, 9);
    }

    public GoBoard(XmlGoBoard unmarshal)
    {
        board = Board.unmarshal(unmarshal.board);
    }

    public TokenMove[] getMoves(Player player)
    {
        HashSet<TokenMove> moves = new HashSet<TokenMove>();
        for (Island i : board.open)
        {
            for (TokenPosition p : i.tokens)
                moves.add(new TokenMove(player, p));
        }
        return moves.toArray(new TokenMove[]{});
    }

    public GoBoard doMove(TokenMove move)
    {
        GoBoard postMove = new GoBoard(board, move.player, move.position);
        HashSet<Island> surrounded = new HashSet<Island>();
        Board postCapture = postMove.board;
        for(Island i : postCapture.islands)
        {
            boolean hasFreedom = false;
            for(Island o : postCapture.open)
            {
                if(i.isNeigbor(o))
                {
                    hasFreedom = true;
                    break;
                }
            }
            if(!hasFreedom)
            {
                surrounded.add(i);
            }
        }
        for(Island i : surrounded)
        {
            for(TokenPosition p : i.tokens)
            {
                postCapture = postCapture.remove(p);
            }
            System.out.println("Island Captured: " + i.tokens.length);
        }
        return new GoBoard(postCapture);
    }

    public void toFile(File out)
    {
        throw new NotImplemented();
    }

    public String toXmlString()
    {
        StringWriter buffer = new StringWriter();
        try
        {
            JAXBContext.newInstance(XmlGoBoard.class).createMarshaller().marshal(new XmlGoBoard(this), buffer);
        }
        catch (JAXBException e)
        {
            throw new RuntimeException(e);
        }
        return buffer.toString();
    }

    public static GoBoard fromFile(File in)
    {
        throw new NotImplemented();
    }

    public static GoBoard fromXmlString(String str)
    {
        StringReader buffer = new StringReader(str);
        try
        {
            return new GoBoard((XmlGoBoard) JAXBContext.newInstance(XmlGoBoard.class).createUnmarshaller().unmarshal(buffer));
        }
        catch (JAXBException e)
        {
            throw new RuntimeException(e);
        }
    }

    public int islandCount(Player p1)
    {
        return board.islandCount(p1);
    }
}
