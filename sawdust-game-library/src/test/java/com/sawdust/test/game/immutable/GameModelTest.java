package com.sawdust.test.game.immutable;

import java.util.HashMap;

import org.junit.Test;

import com.sawdust.engine.controller.Util;
import com.sawdust.games.model.Agent;
import com.sawdust.games.model.Game;
import com.sawdust.games.model.GameWon;
import com.sawdust.games.model.Move;
import com.sawdust.games.model.Player;
import com.sawdust.games.stop.immutable.BoardMove;
import com.sawdust.games.stop.immutable.GoBoard;
import com.sawdust.games.stop.immutable.GoPlayer;

public class GameModelTest
{
    
    public GameModelTest()
    {
    }

    @Test
    public void testGame() throws Exception
    {
        Game game = gameFactory();
        HashMap<Player,Agent> agents = new HashMap<Player, Agent>();
        GoPlayer[] players = game.getPlayers();
        for(Player p : players)
        {
            agents.put(p, getAgent());
        }
        try
        {
            while(true)
            {
                for(Player p : players)
                {
                    Move move = agents.get(p).selectMove(p, game);
                    game = game.doMove(move);
                    System.out.println(((GoBoard)game).toXmlString());
                }
            }
        }
        catch (GameWon e)
        {
            Player p = e.winner;
        }
    }

    private Agent getAgent()
    {
        return new Agent()
        {
            @Override
            public Move selectMove(Player p, Game game)
            {
                GoBoard goGame = (GoBoard) game;
                GoPlayer goPlayer = (GoPlayer) p;
                return getMove(goGame, goPlayer);
            }
        };
    }

    private BoardMove getMove(GoBoard goGame, GoPlayer goPlayer)
    {
        return Util.randomMember(goGame.getMoves(goPlayer));
    }

    private Game gameFactory()
    {
        return new GoBoard();
    }
}
