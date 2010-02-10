package com.sawdust.games.model.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;

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
        try
        {
            GameScript thePlan = move_N(goGame, depth, deadline);
            return thePlan.firstMove();
        }
        catch (GameLost e)
        {
            return null;
        }
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

    public static class GameTransition
    {
        public final Game startGame;
        public final Move move;
        public final Game endGame;
        public GameTransition(Game startGame, Move move, Game endGame)
        {
            super();
            this.startGame = startGame;
            this.move = move;
            this.endGame = endGame;
        }
    }
    
    public static class GameScript
    {
        private final LinkedList<GameTransition> moves = new LinkedList<GameTransition>();
        public final Game startGame;
        public final Game endGame;

        public GameScript(GameScript start, GameTransition t)
        {
            super();
            this.startGame = start.startGame;
            moves.addAll(start.moves);
            moves.add(t);
            this.endGame = t.endGame;
        }

        public Move firstMove()
        {
            Move move = moves.peek().move;
            assert(null != move);
            return move;
        }

        public GameScript(Game startGame)
        {
            super();
            this.startGame = startGame;
            this.endGame = null;
        }

        public GameScript(GameScript start, GameScript end)
        {
            this.startGame = start.startGame;
            moves.addAll(start.moves);
            moves.addAll(end.moves);
            this.endGame = end.endGame;
        }
    }
    
    private GameScript move_N(Game game, int n, Date deadline) throws GameLost
    {
        long startMs = new Date().getTime();
        long thinkingMs = deadline.getTime() - startMs;
        if(thinkingMs < 0) 
        {
            return null;
        }
        Player participant = game.getCurrentPlayer();
        ArrayList<? extends Move> moves = intuition(game, participant);
        Move bestMove = null;
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
                  bestMove = thisMove;
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
                GameScript moveN = move_N(hypotheticalGame, n - 1, DateUtil.future(startMs, (int) (loopCount*thinkingMs/breadth)));
                if(null != moveN) 
                {
                    hypotheticalGame = moveN.endGame;
                    hypotheticalScript = new GameScript(hypotheticalScript, moveN);
                }
            }

            double fitness = gameFitness(hypotheticalGame, participant);
            boolean isBetter = fitness > bestFitness;
            if (null == bestMove || isBetter)
            {
                bestMove = thisMove;
                bestFitness = fitness;
                bestScript = hypotheticalScript;
            }
        }
        assert(null != bestScript);
        return bestScript;
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