package com.sawdust.test.game.immutable;

import java.util.Date;
import java.util.HashMap;

import org.junit.Test;

import com.sawdust.games.model.Agent;
import com.sawdust.games.model.Game;
import com.sawdust.games.model.Move;
import com.sawdust.games.model.Player;
import com.sawdust.games.model.ai.GameLost;
import com.sawdust.games.model.ai.GameWon;

public class GameModelTest
{
    
    @Test
    public void testCallPerformance() throws Exception
    {
        Game game = new com.sawdust.games.stop.immutable.GoBoard();
        HashMap<Player,Agent> agents = new HashMap<Player, Agent>();
        Player[] players = game.getPlayers();
        assert(2 == players.length);
        agents.put(players[0], new RandomAgent());
        agents.put(players[1], new RandomAgent());
        try
        {
            while(true)
            {
                for(Player p : players)
                {
                    Move move = agents.get(p).selectMove(p, game, DateUtil.future(10000));
                    if(null == move)
                    {
                        throw new GameLost((p.equals(players[0])?players[1]:players[0]), p);
                    }
                    System.out.println(move.toString());
                    game = game.doMove(move);
                    System.out.println(((com.sawdust.games.stop.immutable.GoBoard)game).toXmlString());
                }
            }
        }
        catch (GameWon e)
        {
            System.err.println(e.toString());
        }
    }
    
    @Test
    public void testRandomVsBasicSearch() throws Exception
    {
        Game game = new com.sawdust.games.stop.immutable.GoBoard();
        HashMap<Player,Agent> agents = new HashMap<Player, Agent>();
        Player[] players = game.getPlayers();
        assert(2 == players.length);
        agents.put(players[0], new com.sawdust.games.stop.immutable.GoSearchAgent(5,5));
        agents.put(players[1], new RandomAgent());
        GameWon end = fight(game, agents, 5000000);
        System.err.println(end);
    }
    
    @Test
    public void lab1() throws Exception
    {
        Game game = new com.sawdust.games.stop.immutable.GoBoard();
        HashMap<Player,Agent> agents = new HashMap<Player, Agent>();
        Player[] players = game.getPlayers();
        assert(2 == players.length);
        agents.put(players[0], new com.sawdust.games.stop.immutable.GoSearchAgent(2,10));
        agents.put(players[1], new com.sawdust.games.stop.immutable.GoSearchAgent(3,5));
        GameWon end = fight(game, agents, 500);
        System.err.println(end);
    }

    private GameWon fight(Game game, HashMap<Player, Agent> agents, int timePerMoveMs)
    {
        Player[] players = game.getPlayers();
        try
        {
            while(true)
            {
                for(Player p : players)
                {
                    Date startTime = new Date();
                    Move move = agents.get(p).selectMove(p, game, DateUtil.future(timePerMoveMs));
                    if(null == move)
                    {
                        throw new GameLost((p.equals(players[0])?players[1]:players[0]), p);
                    }
                    System.out.println(move.toString());
                    game = game.doMove(move);
                    System.out.println(((com.sawdust.games.stop.immutable.GoBoard)game).toXmlString());
                    System.out.println(String.format("Move duration: %f sec", DateUtil.timeSince(startTime)));
                }
            }
        }
        catch (GameWon e)
        {
            return e;
        }
    }
}
