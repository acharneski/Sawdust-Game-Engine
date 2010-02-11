package com.sawdust.games.model.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import com.sawdust.games.DateUtil;
import com.sawdust.games.model.Agent;
import com.sawdust.games.model.Game;
import com.sawdust.games.model.Move;
import com.sawdust.games.model.Player;
import com.sawdust.games.stop.immutable.GoPlayer;

public abstract class FitnessSearchAgent implements Agent
{

    @Override
    public Move selectMove(Player p, Game game, Date deadline)
    {
        return getMove(game,p, deadline);
    }

    final int breadth;
    final int depth;
    
    
    public FitnessSearchAgent(int breadth, int depth)
    {
        super();
        this.breadth = breadth;
        this.depth = depth;
    }

    private Move getMove(Game goGame, Player goPlayer, Date deadline)
    {
        GameScript thePlan = move_N(goGame, depth, deadline);
        if(null == thePlan) return null;
        return thePlan.firstMove();
    }

    private ArrayList<? extends Move> intuition(final Game game, Player participant)
    {
        ArrayList<? extends Move> moves = toArrayList(game.getMoves(participant));
        Collections.sort(moves, new Comparator<Move>()
        {
            @Override
            public int compare(Move o1, Move o2)
            {
                double v1 = moveFitness(o1, game);
                double v2 = moveFitness(o2, game);
                int compare1 = Double.compare(v2, v1);
                if (0 == compare1) return (Math.random() < 0.5) ? -1 : 1;
                return compare1;
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

    private GameScript move_N(Game game, int n, Date deadline)
    {
        long startMs = new Date().getTime();
        long thinkingMs = deadline.getTime() - startMs;
        if(thinkingMs < 0) 
        {
            return null;
        }
        double msPerMove = ((double)thinkingMs)/breadth;
        Player participant = game.getCurrentPlayer();
        ArrayList<? extends Move> moves = intuition(game, participant);
        GameScript bestScript = null;
        double bestFitness = Integer.MIN_VALUE / 2;
        int loopCount = 0;
        GameScript baseScript = new GameScript(game);
        
        for (Move thisMove : moves)
        {
            if (breadth < ++loopCount) break;
            //if(deadline.before(new Date())) break;
            Game hypotheticalGame;
            GameScript hypotheticalScript;
            hypotheticalGame = game.doMove(thisMove);
            hypotheticalScript = new GameScript(baseScript, new GameTransition(game, thisMove, hypotheticalGame));

            if(null != hypotheticalGame.getWinner())
            {
                if(hypotheticalGame.getWinner().equals(participant))
                {
                  bestScript = hypotheticalScript;
                  break;
                }
                else
                {
                    loopCount++;
                    continue;
                }
            }
            
            if (n > 0)
            {
                GameScript moveN = move_N(hypotheticalGame, n - 1, DateUtil.future(startMs, (int) (loopCount*msPerMove)));
                if(null != moveN)
                {
                    hypotheticalGame = moveN.endGame;
                    hypotheticalScript = new GameScript(hypotheticalScript, moveN);
                }
            }

            double fitness = gameFitness(hypotheticalGame, participant);
            boolean isBetter = fitness > bestFitness;
            if (null == bestScript || isBetter)
            {
                bestFitness = fitness;
                bestScript = hypotheticalScript;
            }
        }
        //assert(null != bestScript);
        return bestScript;
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