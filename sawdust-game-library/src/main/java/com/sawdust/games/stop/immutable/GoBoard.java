package com.sawdust.games.stop.immutable;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.sawdust.games.stop.immutable.XmlGoBoard.Score;

public class GoBoard
{
    private static final Logger LOG = Logger.getLogger(GoBoard.class.getName());
    public static final int EMPTY_VALUE = -1;

    final Board board;
    
    final HashMap<Player,GoScore> scores = new HashMap<Player, GoScore>();

    GoBoard(final Board b)
    {
        board = b;
    }

    public GoBoard(final GoBoard b, final Player player, final TokenPosition position)
    {
        board = new Board(b.board, player, position);
        scores.putAll(b.scores);
    }

    public GoBoard()
    {
        board = new Board(9, 9);
    }

    public GoBoard(XmlGoBoard unmarshal)
    {
        board = Board.unmarshal(unmarshal.board);
        for(Score p : unmarshal.player)
        {
            Player findPlayer = findPlayer(p.name);
            scores.put(findPlayer, new GoScore(p.prisoners, p.territory));
        }
    }

    private Player findPlayer(String name)
    {
        for(Player p : getPlayers())
        {
            if(p.getName().equals(name)) return p;
        }
        return null;
    }

    public GoBoard(Board newBoard, HashMap<Player, GoScore> newScores)
    {
        board = newBoard;
        scores.putAll(newScores);
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
        GoBoard postMove = new GoBoard(this, move.player, move.position);
        HashMap<Player, GoScore> hashMap = new HashMap<Player, GoScore>();
        for(Player p1 : getPlayers()) 
        {
            GoScore score = getScore(p1);
            hashMap.put(p1, new GoScore(score.prisoners, 0));
        }
        HashMap<Player, GoScore> newScores = hashMap;
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
        for(Island i : postCapture.open)
        {
            HashSet<Player> surrounding = new HashSet<Player>();
            for(Island o : postCapture.islands)
            {
                if(i.isNeigbor(o))
                {
                    surrounding.add(o.player);
                }
            }
            if(surrounding.size() == 1)
            {
                Player player = surrounding.iterator().next();
                GoScore score = newScores.get(player);
                newScores.put(player, new GoScore(score.prisoners, score.territory + i.tokens.length));
            }
        }
        for(Island i : surrounded)
        {
            GoScore score = newScores.get(i.player);
            int prisoners = score.prisoners;
            for(TokenPosition p : i.tokens)
            {
                postCapture = postCapture.remove(p);
                prisoners++;
            }
            newScores.put(i.player, new GoScore(prisoners, score.territory));
            System.out.println("Island Captured: " + i.tokens.length);
        }
        return new GoBoard(postCapture, newScores);
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

    public GoScore getScore(Player p)
    {
        if(!scores.containsKey(p)) return new GoScore(0, 0);
        return scores.get(p);
    }

    public Player[] getPlayers()
    {
        return new Player[]{new Player(1), new Player(2)};
    }
}
