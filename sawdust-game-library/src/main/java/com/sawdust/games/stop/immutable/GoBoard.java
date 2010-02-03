package com.sawdust.games.stop.immutable;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.sawdust.games.model.Game;
import com.sawdust.games.model.Move;
import com.sawdust.games.model.Player;
import com.sawdust.games.model.ai.GameLost;
import com.sawdust.games.model.ai.GameWon;
import com.sawdust.games.stop.NotImplemented;
import com.sawdust.games.stop.immutable.XmlGoBoard.Score;

public class GoBoard implements Game
{
    public static final int EMPTY_VALUE = -1;

    public final Board board;
    public final boolean lastPlayerPassed;
    final HashMap<GoPlayer,GoScore> scores = new HashMap<GoPlayer, GoScore>();
    final LinkedList<GoPlayer> turns = new LinkedList<GoPlayer>(); 

    GoBoard(final Board b, HashMap<GoPlayer, GoScore> newScores, LinkedList<GoPlayer> turns2, boolean pass)
    {
        lastPlayerPassed = pass;
        board = b;
        scores.putAll(newScores);
        turns.addAll(turns2);
        turns.add(turns.pop());
    }

    public GoBoard()
    {
        lastPlayerPassed = false;
        board = new Board(9, 9);
        for(GoPlayer p : getPlayers()) turns.add(p);
    }

    public GoBoard(XmlGoBoard unmarshal)
    {
        board = Board.unmarshal(unmarshal.board);
        for(Score p : unmarshal.player)
        {
            GoPlayer findPlayer = findPlayer(p.name);
            scores.put(findPlayer, new GoScore(p.prisoners, p.territory));
        }
        // TODO: Restore who's order it is...
        for(GoPlayer p : getPlayers()) turns.add(p);
        lastPlayerPassed = false;
    }

    public GoBoard(Board newBoard, HashMap<GoPlayer, GoScore> newScores)
    {
        lastPlayerPassed = false;
        board = newBoard;
        scores.putAll(newScores);
        for(GoPlayer p : getPlayers()) turns.add(p);
    }
    
    private GoPlayer findPlayer(String name)
    {
        for(GoPlayer p : getPlayers())
        {
            if(p.getName().equals(name)) return p;
        }
        return null;
    }
 
    public BoardMove[] getMoves(Player player)
    {
        HashSet<BoardMove> moves = new HashSet<BoardMove>();
        moves.add(new BoardMove((GoPlayer) player, null, null));
        for (Island o : board.open)
        {
            HashSet<BoardMove> buffer = new HashSet<BoardMove>();
            for (BoardPosition p : o.tokens)
            {
                buffer.add(new BoardMove((GoPlayer) player, p, o));
            }
            if(o.tokens.length == 1)
            {
                HashSet<GoPlayer> surrounding = new HashSet<GoPlayer>();
                for(Island i : board.islands)
                {
                    if(i.isNeigbor(o))
                    {
                        surrounding.add(i.player);
                    }
                }
                if(surrounding.size() == 1)
                {
                    GoPlayer territoryHolder = surrounding.iterator().next();
                    if(!player.equals(territoryHolder)) 
                    {
                        continue; // Skip add to moves
                    }
                }
            }
            moves.addAll(buffer);
        }
        return moves.toArray(new BoardMove[]{});
    }

    public GoBoard doMove(Move gmove) throws GameWon
    {
        BoardMove move = (BoardMove) gmove;
        BoardPosition position = move.position;
        Player player = move.player;
        Board postMoveBoard = board;
        if(null == position ) 
        {
            if(lastPlayerPassed) 
            {
                GoPlayer[] players = getPlayers();
                Player otherPlayer = players[0].equals(player )?players[1]:players[0];
                if(getScore(otherPlayer).getValue() > getScore(player).getValue())
                {
                    throw new GameLost(otherPlayer , player);
                }
                else
                {
                    throw new GameWon(player);
                }
                
            }
        }
        else
        {
            postMoveBoard = new Board(board, (GoPlayer) player, position);
        }

        HashMap<GoPlayer, GoScore> newScores = copyScores();
        HashSet<Island> surrounded = findSurroundedIslands(postMoveBoard, move.player);
        Board postCaptureBoard = captureIslands(postMoveBoard, newScores, surrounded);
        surrounded = findSurroundedIslands(postMoveBoard, null);
        postCaptureBoard = captureIslands(postCaptureBoard, newScores, surrounded);
        recalculateTerritory(newScores, board);
        GoBoard postCapture = new GoBoard(postCaptureBoard, newScores, turns, null == move.position);
        
        return postCapture;
    }

    public HashMap<GoPlayer, GoScore> copyScores()
    {
        HashMap<GoPlayer, GoScore> newScores = new HashMap<GoPlayer, GoScore>();
        for(GoPlayer player : getPlayers()) 
        {
            GoScore score = getScore(player);
            newScores.put(player, new GoScore(score.prisoners, score.territory));
        }
        return newScores;
    }

    public static Board captureIslands(Board postCapture, HashMap<GoPlayer, GoScore> newScores, HashSet<Island> surrounded)
    {
        for(Island i : surrounded)
        {
            GoScore score = newScores.get(i.player);
            int prisoners = score.prisoners;
            for(BoardPosition p : i.tokens)
            {
                postCapture = postCapture.remove(p);
                prisoners++;
            }
            if(null != newScores) newScores.put(i.player, new GoScore(prisoners, score.territory));
        }
        return postCapture;
    }

    public static HashSet<Island> findSurroundedIslands(Board board, GoPlayer exempt)
    {
        HashSet<Island> surrounded = new HashSet<Island>();
        for(Island i : board.islands)
        {
            if(i.player.equals(exempt)) continue;
            boolean hasFreedom = false;
            for(Island o : board.open)
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
        return surrounded;
    }

    private transient HashMap<Island,Player> territory = null;
    public HashMap<Island,Player> findTerritory()
    {
        if(null == territory) 
        {
            territory = recalculateTerritory(null, board);
            
        }
        return territory;
    }

    public static HashMap<Island,Player> recalculateTerritory(HashMap<GoPlayer, GoScore> newScores, Board board)
    {
        HashMap<Island,Player> returnValue = new HashMap<Island,Player>();
        if(null != newScores)
        {
            for(GoPlayer player : newScores.keySet())
            {
                GoScore score = newScores.get(player);
                newScores.put(player, new GoScore(score.prisoners, 0));
            }
        }
        for(Island o : board.open)
        {
            HashSet<GoPlayer> surrounding = new HashSet<GoPlayer>();
            for(Island i : board.islands)
            {
                if(o.isNeigbor(i))
                {
                    surrounding.add(i.player);
                }
            }
            if(surrounding.size() == 1)
            {
                GoPlayer player = surrounding.iterator().next();
                returnValue.put(o, player);
                if(null != newScores)
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
        if(!scores.containsKey(p)) return new GoScore(0, 0);
        return scores.get(p);
    }

    public GoPlayer[] getPlayers()
    {
        return new GoPlayer[]{new GoPlayer(1), new GoPlayer(2)};
    }

    public Board getBoard()
    {
        return board;
    }

    public Player getCurrentPlayer()
    {
        return turns.peek();
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

}
