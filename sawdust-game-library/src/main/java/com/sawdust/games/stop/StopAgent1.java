/**
 * 
 */
package com.sawdust.games.stop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.logging.Logger;

import com.sawdust.engine.controller.entities.GameSession;
import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.basetypes.BaseGame;
import com.sawdust.engine.model.players.Agent;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.state.GameCommand;

public class StopAgent1<S extends StopGame> extends Agent<S>
{
   private static final Logger LOG        = Logger.getLogger(StopAgent1.class.getName());
   
   protected int               _depth     = 1;
   
   protected int               _expansion = 10;
   
   public StopAgent1(String s, int depth, int expansion)
   {
      super(s);
      _depth = depth;
      _expansion = expansion;
   }
   
   @Override
   public void Move(S game, Participant participant) throws GameException
   {
      try
      {
         GameCommand moveN = move_N(game, _depth);
         String x = (null == moveN) ? "<null>" : moveN.getCommandText();
         LOG.fine("Command: " + x);
      }
      catch (CloneNotSupportedException e)
      {
         e.printStackTrace();
      }
   }
   
   protected ArrayList<GameCommand> intuition(final S game, Participant participant) throws GameException
   {
      ArrayList<GameCommand> moves = game.getMoves(participant);
      Collections.sort(moves, new Comparator<GameCommand>()
      {
         @Override
         public int compare(GameCommand o1, GameCommand o2)
         {
            return sortGameMoves(o1, o2, game);
         }
      });
      return moves;
   }
   
   protected GameCommand move_N(S game, int n) throws CloneNotSupportedException, GameException
   {
      Participant participant = game.getPlayerManager().getCurrentPlayer();
      ArrayList<GameCommand> moves = intuition(game, participant);
      GameCommand bestMove = null;
      int cnt = 0;
      int maxLoop1 = expansionLimit(n);
      double bestFitness = Integer.MIN_VALUE;
      for (GameCommand thisMove : moves)
      {
         if (0 > --maxLoop1) break;
         try
         {
            S hypotheticalGame = (S) cloneForSimulation(game);
            hypotheticalGame.setSilent(true);
            ArrayList<GameCommand> moves2 = hypotheticalGame.getMoves(participant);
            String commandText = thisMove.getCommandText();
            for (GameCommand i : moves2)
            {
               if (i.getCommandText().equals(commandText))
               {
                  i.doCommand(participant, commandText);
                  break;
               }
            }
            
            if (n > 0)
            {
               move_N(hypotheticalGame, n - 1);
            }
            double fitness1 = gameFitness(hypotheticalGame, participant);
            if (n >= _depth)
            {
               LOG.fine((++cnt) + "\t" + n + "\t" + commandText + "\t" + fitness1);
            }
            boolean isBetter = fitness1 > bestFitness;
            if (null == bestMove || isBetter)
            {
               bestMove = thisMove;
               bestFitness = fitness1;
            }
         }
         catch (GameException e)
         {
            // Nothing
         }
      }
      if (null != bestMove)
      {
         bestMove.doCommand(participant, bestMove.getCommandText());
      }
      return bestMove;
   }
   
   protected BaseGame cloneForSimulation(final BaseGame game) throws CloneNotSupportedException
   {
      final GameSession session = game.getSession();
      return (BaseGame) new StopGame((StopGame) game)
      {
         @Override
         public GameSession getSession()
         {
            return null;
         }

        @Override
        public void doOnPostStartActivity()
        {
        }
         
      };
   }
   
   protected double gameFitness(S game, Participant self) throws GameException
   {
      if (null == game) return Integer.MIN_VALUE;
      if (null == self) return Integer.MIN_VALUE;
      int playerIdx = game.getPlayerManager().findPlayer(self);
      int otherIdx = (playerIdx == 0) ? 1 : 0;
      int score1 = game.getTokenArray().getScore(playerIdx);
      int score2 = game.getTokenArray().getScore(otherIdx);
      int scoreDiff = score1 - score2;
      double fitness = scoreDiff;
      
      for (StopIsland island : game.getTokenArray().getIslands())
      {
         double bias = -1.0;
         if (playerIdx == island.getPlayer()) bias = 1.0;
         double spanH = (island.posRight - island.posLeft);
         if (0 >= spanH) spanH = 1;
         double spanV = (island.posBottom - island.posTop);
         if (0 >= spanV) spanV = 1;
         double freedom = island.openBorder.size();
         // double size = island.positions.size();
         fitness += bias * spanH * spanV * freedom;
      }
      
      return fitness;
   }
   
   protected int sortGameMoves(GameCommand o1, GameCommand o2, S game)
   {
      double v1 = 0.0;
      double v2 = 0.0;
      v1 = moveFitness(o1, game, v1);
      v2 = moveFitness(o2, game, v2);
      int compare = Double.compare(v2, v1);
      if (0 == compare) return (Math.random() < 0.5) ? -1 : 1;
      return compare;
   }
   
   protected double moveFitness(GameCommand o1, S game, double v1)
   {
      if (!(o1 instanceof MoveCommand)) return -1000;
      ArrayPosition position1 = ((MoveCommand) o1).getPosition();
      HashSet<Integer> valueSet = new HashSet<Integer>();
      for (ArrayPosition p : position1.getNeighbors())
      {
         BoardData boardData = game.getBoardData(p.row, p.col);
         int value = (null == boardData) ? -1 : boardData.value;
         if (!valueSet.contains(value))
         {
            v1 += 1;
            valueSet.add(value);
         }
      }
      return v1;
   }
   
   protected int expansionLimit(int n)
   {
      return _expansion;
   }
}
