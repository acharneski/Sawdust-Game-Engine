package com.sawdust.games.go.test;

import org.junit.Test;

import com.sawdust.games.go.controller.GoBoard;
import com.sawdust.games.go.model.BoardMove;
import com.sawdust.games.go.model.BoardPosition;
import com.sawdust.games.go.model.GoPlayer;
import com.sawdust.games.go.model.Island;

public class GoBoardTest
{

    public GoBoardTest()
    {
    }

    @Test
    public void testSingleMove() throws Exception
    {
        GoPlayer p1 = new GoPlayer(1);
        GoPlayer p2 = new GoPlayer(2);

        GoBoard board = GoBoard.fromXmlString("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><xmlGoBoard><board cols=\"9\" rows=\"9\">" +
            "0 0 0 0 0 0 0 0 0 \n" +
            "0 0 0 0 0 0 0 0 0 \n" +
            "0 0 0 0 0 0 0 0 0 \n" +
            "0 0 0 0 0 0 0 0 0 \n" +
            "0 0 0 0 0 0 0 0 0 \n" +
            "0 0 0 0 0 0 0 0 0 \n" +
            "0 0 0 0 0 0 0 0 0 \n" +
            "0 0 0 0 0 0 0 0 0 \n" +
            "0 0 0 0 0 0 0 0 0 \n" +
            "</board><player prisoners=\"0\" territory=\"0\">Player 1</player><player prisoners=\"0\" territory=\"0\">Player 2</player></xmlGoBoard>");

        assert(board.board.islands.length == 0);
        BoardMove move = new BoardMove(p1, BoardPosition.Get(1,2), null);
        board = board.doMove(move);
        System.out.println(board.toXmlString());
        assert(board.board.islands.length == 1);
        assert(0 == board.getScore(p1).prisoners);
        assert(0 == board.getScore(p2).prisoners);
    }

    @Test
    public void testSuicide() throws Exception
    {
        GoPlayer p1 = new GoPlayer(1);
        GoPlayer p2 = new GoPlayer(2);

        GoBoard board = GoBoard.fromXmlString("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><xmlGoBoard><board cols=\"9\" rows=\"9\">" +
            "0 2 0 0 0 0 0 0 0 \n" +
            "2 0 2 0 0 0 0 0 0 \n" +
            "0 2 0 0 0 0 0 0 0 \n" +
            "0 0 0 0 0 0 0 0 0 \n" +
            "0 0 0 0 0 0 0 0 0 \n" +
            "0 0 0 0 0 0 0 0 0 \n" +
            "0 0 0 0 0 0 0 0 0 \n" +
            "0 0 0 0 0 0 0 0 0 \n" +
            "0 0 0 0 0 0 0 0 0 \n" +
            "</board><player prisoners=\"0\" territory=\"0\">Player 1</player><player prisoners=\"0\" territory=\"0\">Player 2</player></xmlGoBoard>");

        assert(board.board.islands.length == 4);
        BoardMove move = new BoardMove(p1, BoardPosition.Get(1,1), null);
        board = board.doMove(move);
        System.out.println(board.toXmlString());
        assert(board.board.islands.length == 4);
        for(Island i : board.board.islands) assert(i.player.value == 2);
        assert(1 == board.getScore(p1).prisoners);
        assert(0 == board.getScore(p2).prisoners);
    }

    @Test
    public void testSimpleCapture() throws Exception
    {
        GoPlayer p1 = new GoPlayer(1);
        GoPlayer p2 = new GoPlayer(2);

        GoBoard board = GoBoard.fromXmlString("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><xmlGoBoard><board cols=\"9\" rows=\"9\">" +
            "1 2 1 0 0 0 0 0 0 \n" +
            "0 0 0 0 0 0 0 0 0 \n" +
            "0 0 0 0 0 0 0 0 0 \n" +
            "0 0 0 0 0 0 0 0 0 \n" +
            "0 0 0 0 0 0 0 0 0 \n" +
            "0 0 0 0 0 0 0 0 0 \n" +
            "0 0 0 0 0 0 0 0 0 \n" +
            "0 0 0 0 0 0 0 0 0 \n" +
            "0 0 0 0 0 0 0 0 0 \n" +
            "</board><player prisoners=\"0\" territory=\"0\">Player 1</player><player prisoners=\"0\" territory=\"0\">Player 2</player></xmlGoBoard>");

        assert(board.board.islands.length == 3);
        assert(0 == board.getScore(p1).prisoners);
        assert(0 == board.getScore(p2).prisoners);
        BoardMove move = new BoardMove(p1, BoardPosition.Get(1,1), null);
        board = board.doMove(move);
        System.out.println(board.toXmlString());
        assert(board.board.islands.length == 3);
        for(Island i : board.board.islands) assert(i.player.value == 1);
        assert(0 == board.getScore(p1).prisoners);
        assert(1 == board.getScore(p2).prisoners);
    }

    @Test
    public void testRaceCapture() throws Exception
    {
        GoPlayer p1 = new GoPlayer(1);
        GoPlayer p2 = new GoPlayer(2);

        GoBoard board = GoBoard.fromXmlString("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><xmlGoBoard><board cols=\"9\" rows=\"9\">" +
            "0 1 0 0 0 0 0 0 0 \n" +
            "1 2 1 0 0 0 0 0 0 \n" +
            "2 0 2 0 0 0 0 0 0 \n" +
            "0 2 0 0 0 0 0 0 0 \n" +
            "0 0 0 0 0 0 0 0 0 \n" +
            "0 0 0 0 0 0 0 0 0 \n" +
            "0 0 0 0 0 0 0 0 0 \n" +
            "0 0 0 0 0 0 0 0 0 \n" +
            "0 0 0 0 0 0 0 0 0 \n" +
            "</board><player prisoners=\"0\" territory=\"0\">Player 1</player><player prisoners=\"0\" territory=\"0\">Player 2</player></xmlGoBoard>");

        assert(board.board.islands.length == 7);
        BoardMove move = new BoardMove(p1, BoardPosition.Get(2,1), null);
        board = board.doMove(move);
        System.out.println(board.toXmlString());
        assert(board.board.islands.length == 7);
        assert(0 == board.getScore(p1).prisoners);
        assert(1 == board.getScore(p2).prisoners);
    }

    @Test
    public void testIslandJoin() throws Exception
    {
        GoPlayer p1 = new GoPlayer(1);
        GoPlayer p2 = new GoPlayer(2);

        GoBoard board = GoBoard.fromXmlString("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><xmlGoBoard><board cols=\"9\" rows=\"9\">" +
            "0 1 0 0 0 0 0 0 0 \n" +
            "1 0 1 0 0 0 0 0 0 \n" +
            "0 0 0 0 0 0 0 0 0 \n" +
            "0 0 0 0 0 0 0 0 0 \n" +
            "0 0 0 0 0 0 0 0 0 \n" +
            "0 0 0 0 0 0 0 0 0 \n" +
            "0 0 0 0 0 0 0 0 0 \n" +
            "0 0 0 0 0 0 0 0 0 \n" +
            "0 0 0 0 0 0 0 0 0 \n" +
            "</board><player prisoners=\"0\" territory=\"0\">Player 1</player><player prisoners=\"0\" territory=\"0\">Player 2</player></xmlGoBoard>");

        assert(board.board.islands.length == 3);
        BoardMove move = new BoardMove(p1, BoardPosition.Get(1,1), null);
        board = board.doMove(move);
        System.out.println(board.toXmlString());
        assert(board.board.islands.length == 1);
        assert(0 == board.getScore(p1).prisoners);
        assert(0 == board.getScore(p2).prisoners);
    }
}
