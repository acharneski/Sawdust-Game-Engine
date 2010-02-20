package com.sawdust.games.go.controller;

import java.io.File;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.sawdust.engine.NotImplemented;
import com.sawdust.games.go.model.Board;
import com.sawdust.games.go.model.BoardMove;
import com.sawdust.games.go.model.BoardPosition;
import com.sawdust.games.go.model.Game;
import com.sawdust.games.go.model.GoPlayer;
import com.sawdust.games.go.model.GoScore;
import com.sawdust.games.go.model.Island;
import com.sawdust.games.go.model.Move;
import com.sawdust.games.go.model.Player;
import com.sawdust.games.go.view.XmlGoBoard;
import com.sawdust.games.go.view.XmlGoBoard.Score;

public class GoBoard implements Game, Serializable
{
    private static final Logger LOG = Logger.getLogger(GoBoard.class.getName());
    private static final long serialVersionUID = 6777412046819678208L;
    protected static class SerialForm implements Serializable
    {
        private static final long serialVersionUID = GoBoard.serialVersionUID;
        String xml = null;
        protected SerialForm(){}
        protected SerialForm(GoBoard obj)
        {
            xml = obj.toXmlString();
        }
        private Object readResolve()
        {
            LOG.fine("Read: " + this.xml);
            return GoBoard.fromXmlString(this.xml);
        }
    }
    
    private Object writeReplace()
    {
        SerialForm serialForm = new SerialForm(this);
        LOG.fine("Write: " + serialForm.xml);
        return serialForm;
    }
    
    public static final int EMPTY_VALUE = -1;

    public final Board board;
    public final Board lastboard;
    public final boolean lastPlayerPassed;
    final HashMap<GoPlayer, GoScore> scores = new HashMap<GoPlayer, GoScore>();
    final LinkedList<GoPlayer> playerOrder = new LinkedList<GoPlayer>();
    final Player winner;

    GoBoard(final Board b, HashMap<GoPlayer, GoScore> newScores, LinkedList<GoPlayer> turns2, boolean pass, Board lastboard)
    {
        lastPlayerPassed = pass;
        this.lastboard = lastboard;
        winner = null;
        board = b;
        scores.putAll(newScores);
        playerOrder.addAll(turns2);
        playerOrder.add(playerOrder.pop());
    }

    public GoBoard(int rows, int cols)
    {
        lastPlayerPassed = false;
        lastboard = null;
        winner = null;
        board = new Board(rows, cols);
        for (GoPlayer p : getPlayers())
            playerOrder.add(p);
    }

    public GoBoard(XmlGoBoard unmarshal)
    {
        board = Board.unmarshal(unmarshal.board);
        lastboard = Board.unmarshal(unmarshal.lastboard);
        GoPlayer w = null;
        for (Score p : unmarshal.player)
        {
            GoPlayer findPlayer = findPlayer(p.name);
            if(p.winner) 
            {
                assert(null == w);
                w = findPlayer;
            }
            GoScore value = new GoScore(p.prisoners, p.territory);
            scores.put(findPlayer, value);
        }
        winner = w;
        // TODO: Restore who's order it is...
        for (GoPlayer p : getPlayers())
            playerOrder.add(p);
        lastPlayerPassed = false;
    }

    public GoBoard(GoBoard goBoard, Player winningPlayer)
    {
        lastPlayerPassed = false;
        winner = winningPlayer;
        board = goBoard.board;
        lastboard = goBoard.lastboard;
        scores.putAll(goBoard.scores);
    }

    private GoPlayer findPlayer(String name)
    {
        for (GoPlayer p : getPlayers())
        {
            if (p.getName().equals(name)) return p;
        }
        return null;
    }

    public BoardMove[] getMoves(Player player)
    {
        HashSet<BoardMove> moves = new HashSet<BoardMove>();
        if(null == winner)
        {
            moves.add(new BoardMove((GoPlayer) player, null, null));
            for (Island o : board.open)
            {
                HashSet<BoardMove> buffer = new HashSet<BoardMove>();
                for (BoardPosition p : o.tokens)
                {
                    buffer.add(new BoardMove((GoPlayer) player, p, o));
                }
                BoardMove tryMove = buffer.iterator().next();
                GoBoard postMove = this.doMove(tryMove);
                if(postMove.board.equals(this.board)) continue;
                if(postMove.board.equals(this.lastboard)) continue;
                moves.addAll(buffer);
            }
        }
        return moves.toArray(new BoardMove[] {});
    }

    public GoBoard doMove(Move gmove)
    {
        if(null != winner) return this;
        BoardMove move = (BoardMove) gmove;
        BoardPosition position = move.position;
        Player player = move.player;
        Board postMoveBoard = board;
        HashMap<GoPlayer, GoScore> newScores = copyScores();
        if (null == position)
        {
            if (lastPlayerPassed)
            {
                GoPlayer[] players = getPlayers();
                Player otherPlayer = players[0].equals(player) ? players[1] : players[0];
                if (getScore(otherPlayer).getValue() > getScore(player).getValue())
                {
                    return new GoBoard(this, otherPlayer);
                }
                else
                {
                    return new GoBoard(this, player);
                }
            }
        }
        else
        {
            postMoveBoard = new Board(board, (GoPlayer) player, position);
        }

        HashSet<Island> surrounded = findSurroundedIslands(postMoveBoard, move.player);
        Board postCaptureBoard = captureIslands(postMoveBoard, newScores, surrounded);
        surrounded = findSurroundedIslands(postCaptureBoard, null);
        postCaptureBoard = captureIslands(postCaptureBoard, newScores, surrounded);
        recalculateTerritory(newScores, board);
        GoBoard postCapture = new GoBoard(postCaptureBoard, newScores, playerOrder, null == move.position, board);

        return postCapture;
    }

    public HashMap<GoPlayer, GoScore> copyScores()
    {
        HashMap<GoPlayer, GoScore> newScores = new HashMap<GoPlayer, GoScore>();
        for (GoPlayer player : getPlayers())
        {
            GoScore score = getScore(player);
            newScores.put(player, new GoScore(score.prisoners, score.territory));
        }
        return newScores;
    }

    public static Board captureIslands(Board postCapture, HashMap<GoPlayer, GoScore> newScores, HashSet<Island> surrounded)
    {
        for (Island i : surrounded)
        {
            GoScore score = newScores.get(i.player);
            int prisoners = score.prisoners;
            for (BoardPosition p : i.tokens)
            {
                postCapture = postCapture.remove(p);
                prisoners++;
            }
            if (null != newScores) newScores.put(i.player, new GoScore(prisoners, score.territory));
        }
        return postCapture;
    }

    public static HashSet<Island> findSurroundedIslands(Board board, GoPlayer exempt)
    {
        HashSet<Island> surrounded = new HashSet<Island>();
        for (Island i : board.islands)
        {
            if (i.player.equals(exempt)) continue;
            boolean hasFreedom = false;
            for (Island o : board.open)
            {
                if (i.isNeigbor(o))
                {
                    hasFreedom = true;
                    break;
                }
            }
            if (!hasFreedom)
            {
                surrounded.add(i);
            }
        }
        return surrounded;
    }

    private transient HashMap<Island, Player> territory = null;

    public HashMap<Island, Player> findTerritory()
    {
        if (null == territory)
        {
            territory = recalculateTerritory(null, board);
        }
        return territory;
    }

    public static HashMap<Island, Player> recalculateTerritory(HashMap<GoPlayer, GoScore> newScores, Board board)
    {
        HashMap<Island, Player> returnValue = new HashMap<Island, Player>();
        if (null != newScores)
        {
            for (GoPlayer player : newScores.keySet())
            {
                GoScore score = newScores.get(player);
                newScores.put(player, new GoScore(score.prisoners, 0));
            }
        }
        for (Island o : board.open)
        {
            HashSet<GoPlayer> surrounding = new HashSet<GoPlayer>();
            for (Island i : board.islands)
            {
                if (o.isNeigbor(i))
                {
                    surrounding.add(i.player);
                }
            }
            if (surrounding.size() == 1)
            {
                GoPlayer player = surrounding.iterator().next();
                returnValue.put(o, player);
                if (null != newScores)
                {
                    GoScore score = newScores.get(player);
                    newScores.put(player, new GoScore(score.prisoners, score.territory + o.tokens.length));
                }
            }
        }
        return returnValue;
    }

    public void toFile(File out)
    {
        throw new NotImplemented();
    }

    @Override
    public String toString()
    {
        return toXmlString();
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

    public int islandCount(GoPlayer p1)
    {
        return board.islandCount(p1);
    }

    public GoScore getScore(Player p)
    {
        if (!scores.containsKey(p)) return new GoScore(0, 0);
        return scores.get(p);
    }

    public GoPlayer[] getPlayers()
    {
        return new GoPlayer[]
        { new GoPlayer(1), new GoPlayer(2) };
    }

    public Board getBoard()
    {
        return board;
    }

    public Player getCurrentPlayer()
    {
        return playerOrder.peek();
    }

    @Override
    public int hashCode()
    {
        throw new NotImplemented();
    }

    @Override
    public boolean equals(Object obj)
    {
        throw new NotImplemented();
    }

    @Override
    public Player getWinner()
    {
        return winner;
    }

}
