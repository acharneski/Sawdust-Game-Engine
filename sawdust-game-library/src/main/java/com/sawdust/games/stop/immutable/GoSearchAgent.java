package com.sawdust.games.stop.immutable;

import com.sawdust.games.model.Game;
import com.sawdust.games.model.Player;
import com.sawdust.games.model.ai.FitnessSearchAgent;

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
    
}