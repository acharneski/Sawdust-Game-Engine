package com.sawdust.games.go.test;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.junit.Test;

import com.sawdust.engine.controller.Util;
import com.sawdust.games.go.controller.GoBoard;
import com.sawdust.games.go.model.Board;
import com.sawdust.games.go.model.BoardMove;
import com.sawdust.games.go.model.BoardPosition;
import com.sawdust.games.go.model.GoPlayer;
import com.sawdust.games.go.view.XmlBoard;

public class GoBoardModelLab
{
    private static JAXBContext context;

    static
    {
        try
        {
            context = JAXBContext.newInstance(XmlBoard.class);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public GoBoardModelLab()
    {
    }

    @Test
    public void testGo() throws Exception
    {
        GoBoard board = new GoBoard(9,9);
        GoPlayer p1 = new GoPlayer(1);
        GoPlayer p2 = new GoPlayer(2);

        
        boolean continueGame = true;
        while (continueGame)
        {
            for(GoPlayer p : new GoPlayer[]{p1,p2})
            {
                BoardMove move = Util.randomMember(board.getMoves(p));
                if(null == move) 
                {
                    continueGame = false;
                    break;
                }
                board = board.doMove(move);
                int islandCount = board.islandCount(p1);
                String data = board.toXmlString();
                System.out.println(data);
                String echo = GoBoard.fromXmlString(data).toXmlString();
                assert (echo.equals(data));
                System.out.println(String.format("Player %s has %d islands", p, islandCount));
            }
        }
    }

    @Test
    public void testBoard() throws Exception
    {
        Board board = new Board(8, 8);
        GoPlayer p1 = new GoPlayer(1);
        GoPlayer p2 = new GoPlayer(2);

        board = board.doMove(new BoardMove(p1, BoardPosition.Get(2, 2), null));
        int islandCount = board.islandCount(p1);
        echoTest(board);
        System.out.println(String.format("Player 1 has %d islands", islandCount));
        assert (islandCount == 1);

        board = board.doMove(new BoardMove(p1, BoardPosition.Get(2, 3), null));
        islandCount = board.islandCount(p1);
        echoTest(board);
        System.out.println(String.format("Player 1 has %d islands", islandCount));
        assert (islandCount == 1);

        board = board.doMove(new BoardMove(p2, BoardPosition.Get(3, 3), null));
        islandCount = board.islandCount(p2);
        echoTest(board);
        System.out.println(String.format("Player 2 has %d islands", islandCount));
        assert (islandCount == 1);

        board = board.doMove(new BoardMove(p1, BoardPosition.Get(2, 5), null));
        islandCount = board.islandCount(p1);
        echoTest(board);
        System.out.println(String.format("Player 1 has %d islands", islandCount));
        assert (islandCount == 2);

        board = board.doMove(new BoardMove(p1, BoardPosition.Get(3, 5), null));
        islandCount = board.islandCount(p1);
        echoTest(board);
        System.out.println(String.format("Player 1 has %d islands", islandCount));
        assert (islandCount == 2);

        board = board.doMove(new BoardMove(p2, BoardPosition.Get(3, 6), null));
        islandCount = board.islandCount(p2);
        echoTest(board);
        System.out.println(String.format("Player 2 has %d islands", islandCount));
        assert (islandCount == 2);

        board = board.doMove(new BoardMove(p1, BoardPosition.Get(2, 4), null));
        islandCount = board.islandCount(p1);
        echoTest(board);
        System.out.println(String.format("Player 1 has %d islands", islandCount));
        assert (islandCount == 1);
    }

    public static void echoTest(Board board) throws JAXBException
    {
        String data = asString(board);
        System.out.println(data);
        String echo = asString(fromString(data));
        assert (echo.equals(data));
    }

    public static void toFile(Board board, String pathname) throws JAXBException, IOException
    {
        XmlBoard xmlObj = board.getXmlObj();
        File file = new File(pathname);
        context.createMarshaller().marshal(xmlObj, new FileWriter(file));
    }

    public static Board fromFile(String pathname) throws JAXBException, IOException
    {
        File file = new File(pathname);
        XmlBoard unmarshal = (XmlBoard) context.createUnmarshaller().unmarshal(new FileReader(file));
        return Board.unmarshal(unmarshal);
    }

    public static Board fromString(String data) throws JAXBException
    {
        StringReader buffer = new StringReader(data);
        return Board.unmarshal((XmlBoard) context.createUnmarshaller().unmarshal(buffer));
    }

    public static String asString(Board board) throws JAXBException
    {
        XmlBoard xmlObj = board.getXmlObj();
        StringWriter buffer = new StringWriter();
        context.createMarshaller().marshal(xmlObj, buffer);
        return buffer.toString();
    }
}