package com.sawdust.games.model.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.sawdust.games.model.Agent;
import com.sawdust.games.model.Game;
import com.sawdust.games.model.GameLost;
import com.sawdust.games.model.GameWon;
import com.sawdust.games.model.Move;
import com.sawdust.games.model.Player;
import com.sawdust.games.stop.immutable.GoPlayer;

public abstract class FitnessSearchAgent implements Agent
{

    @Override
    public Move selectMove(Player p, Game game)
    {
        return getMove(game,p);
    }

    final int breadth;
    final int depth;
    
    
    public FitnessSearchAgent(int breadth, int depth)
    {
        super();
        this.breadth = breadth;
        this.depth = depth;
    }

    private Move getMove(Game goGame, Player goPlayer)
    {
        return (Move) move_N(goGame, depth);
    }

    private ArrayList<? extends Move> intuition(final Game game, Player participant)
    {
        ArrayList<? extends Move> moves = toArrayList(game.getMoves(participant));
        Collections.sort(moves, new Comparator<Move>()
        {
            @Override
            public int compare(Move o1, Move o2)
            {
                return sortGameMoves(o1, o2, game);
            }
        });
        return moves;
    }

    private <T> ArrayList<T> toArrayList(T[] moves)
    {
        ArrayList<T> arrayList = new ArrayList<T>();
        for (T m : moves)
            arrayList.add(m);
        return arrayList;
    }

    private Move move_N(Game game, int n)
    {
        Player participant = game.getCurrentPlayer();
        ArrayList<? extends Move> moves = intuition(game, participant);
        Move bestMove = null;
        double bestFitness = Integer.MIN_VALUE;
        int loopCount = breadth;
        for (Move thisMove : moves)
        {
            if (0 > --loopCount) break;
            try
            {
                Game hypotheticalGame = game.doMove(thisMove);
                if (n > 0)
                {
                    Move moveN = move_N(hypotheticalGame, n - 1);
                    hypotheticalGame = hypotheticalGame.doMove(moveN);
                }
                double fitness1 = gameFitness(hypotheticalGame, participant);
                boolean isBetter = fitness1 > bestFitness;
                if (null == bestMove || isBetter)
                {
                    bestMove = thisMove;
                    bestFitness = fitness1;
                }
            }
            catch (GameLost e)
            {
                continue;
            }
            catch (GameWon e)
            {
                bestMove = thisMove;
                break;
            }
        }
        return bestMove;
    }

    private int sortGameMoves(Move o1, Move o2, Game game)
    {
        double v1 = moveFitness(o1, game);
        double v2 = moveFitness(o2, game);
        int compare = Double.compare(v2, v1);
        if (0 == compare) return (Math.random() < 0.5) ? -1 : 1;
        return compare;
    }

    protected double gameFitness(Game game, Player self)
    {
        if (null == game) return Double.MIN_VALUE;
        if (null == self) return Double.MIN_VALUE;
        return game.getScore((GoPlayer) self).getValue();
    }

    protected double moveFitness(Move o1, Game game)
    {
        return 1.0;
    }
}