package com.sawdust.test.game.immutable;

import java.util.HashMap;

import org.junit.Test;

import com.sawdust.games.model.Agent;
import com.sawdust.games.model.Game;
import com.sawdust.games.model.GameWon;
import com.sawdust.games.model.Move;
import com.sawdust.games.model.Player;

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
                    Move move = agents.get(p).selectMove(p, game);
                    game = game.doMove(move);
                    System.out.println(((com.sawdust.games.stop.immutable.GoBoard)game).toXmlString());
                }
            }
        }
        catch (GameWon e)
        {
        }
    }
    
    @Test
    public void testRandomVsBasicSearch() throws Exception
    {
        Game game = new com.sawdust.games.stop.immutable.GoBoard();
        HashMap<Player,Agent> agents = new HashMap<Player, Agent>();
        Player[] players = game.getPlayers();
        assert(2 == players.length);
        agents.put(players[0], new com.sawdust.games.stop.immutable.GoSearchAgent(3,3));
        agents.put(players[1], new RandomAgent());
        try
        {
            while(true)
            {
                for(Player p : players)
                {
                    Move move = agents.get(p).selectMove(p, game);
                    game = game.doMove(move);
                    System.out.println(((com.sawdust.games.stop.immutable.GoBoard)game).toXmlString());
                }
            }
        }
        catch (GameWon e)
        {
        }
    }
}
