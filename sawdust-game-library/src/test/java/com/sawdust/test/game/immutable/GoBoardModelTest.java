package com.sawdust.test.game.immutable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.junit.Test;

import com.sawdust.games.stop.immutable.Board;
import com.sawdust.games.stop.immutable.XmlBoard;
import com.sawdust.games.stop.immutable.player;
import com.sawdust.games.stop.immutable.tokenMove;
import com.sawdust.games.stop.immutable.tokenPosition;

public class GoBoardModelTest
{
    private JAXBContext context;

    public GoBoardModelTest()
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

    @Test
    public void testGo() throws Exception
    {
        Board board = new Board(8, 8);
        player p1 = new player(1);
        player p2 = new player(2);
        
        board = board.doMove(new tokenMove(p1, new tokenPosition(2, 2)));
        int islandCount = board.islandCount(p1);
        echoTest(board);
        System.out.println(String.format("Player 1 has %d islands", islandCount));
        assert(islandCount == 1);

        board = board.doMove(new tokenMove(p1, new tokenPosition(2, 3)));
        islandCount = board.islandCount(p1);
        echoTest(board);
        System.out.println(String.format("Player 1 has %d islands", islandCount));
        assert(islandCount == 1);

        board = board.doMove(new tokenMove(p2, new tokenPosition(3, 3)));
        islandCount = board.islandCount(p2);
        echoTest(board);
        System.out.println(String.format("Player 2 has %d islands", islandCount));
        assert(islandCount == 1);

        board = board.doMove(new tokenMove(p1, new tokenPosition(2, 5)));
        islandCount = board.islandCount(p1);
        echoTest(board);
        System.out.println(String.format("Player 1 has %d islands", islandCount));
        assert(islandCount == 2);

        board = board.doMove(new tokenMove(p1, new tokenPosition(3, 5)));
        islandCount = board.islandCount(p1);
        echoTest(board);
        System.out.println(String.format("Player 1 has %d islands", islandCount));
        assert(islandCount == 2);

        board = board.doMove(new tokenMove(p2, new tokenPosition(3, 6)));
        islandCount = board.islandCount(p2);
        echoTest(board);
        System.out.println(String.format("Player 2 has %d islands", islandCount));
        assert(islandCount == 2);

        board = board.doMove(new tokenMove(p1, new tokenPosition(2, 4)));
        islandCount = board.islandCount(p1);
        echoTest(board);
        System.out.println(String.format("Player 1 has %d islands", islandCount));
        assert(islandCount == 1);
    }

    private void echoTest(Board board) throws JAXBException
    {
        String data = asString(board);
        System.out.println(data);
        String echo = asString(fromString(data));
        assert(echo.equals(data));
    }

    private void toFile(Board board, String pathname) throws JAXBException, IOException
    {
        XmlBoard xmlObj = board.getXmlObj();
        File file = new File(pathname);
        context.createMarshaller().marshal(xmlObj, new FileWriter(file));
    }

    private Board fromFile(String pathname) throws JAXBException, IOException
    {
        File file = new File(pathname);
        XmlBoard unmarshal = (XmlBoard) context.createUnmarshaller().unmarshal(new FileReader(file));
        return Board.unmarshal(unmarshal);
    }

    private Board fromString(String data) throws JAXBException
    {
        StringReader buffer = new StringReader(data);
        return Board.unmarshal((XmlBoard) context.createUnmarshaller().unmarshal(buffer));
    }

    private String asString(Board board) throws JAXBException
    {
        XmlBoard xmlObj = board.getXmlObj();
        StringWriter buffer = new StringWriter();
        context.createMarshaller().marshal(xmlObj, buffer);
        return buffer.toString();
    }
}
