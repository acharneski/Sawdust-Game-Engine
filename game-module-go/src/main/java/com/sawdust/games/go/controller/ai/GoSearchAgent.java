package com.sawdust.games.go.controller.ai;

import java.util.HashMap;

import com.sawdust.games.go.controller.GoBoard;
import com.sawdust.games.go.model.BoardMove;
import com.sawdust.games.go.model.Game;
import com.sawdust.games.go.model.GoPlayer;
import com.sawdust.games.go.model.Island;
import com.sawdust.games.go.model.Move;
import com.sawdust.games.go.model.Player;

public final class GoSearchAgent extends FitnessSearchAgent
{
    public GoSearchAgent(int breadth, int depth)
    {
        super(breadth, depth);
    }

    @Override
    protected double gameFitness(Game game, Player self)
    {
        if (null == game) return Double.MIN_VALUE;
        if (null == self) return Double.MIN_VALUE;
        double score = game.getScore((GoPlayer) self).getValue();
        Player[] players = game.getPlayers();
        Player otherPlayer = (players[0].equals(self))?players[1]:players[0];
        double otherPlayerScore = game.getScore((GoPlayer) otherPlayer).getValue();
        return score - otherPlayerScore;
    }

    @Override
    protected double moveFitness(Move o1, Game game)
    {
        BoardMove move = (BoardMove) o1;
        GoBoard g = (GoBoard) game;
        if(null != move.island)
        {
            HashMap<Island, Player> territory = g.findTerritory();
            if(territory.containsKey(move.island))
            {
                if(1 == move.island.tokens.length) return -1;
            }
        }
        else
        {
            return 0;
        }
        return super.moveFitness(o1, game);
    }
    
}