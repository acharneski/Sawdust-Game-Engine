/**
 * 
 */
package com.sawdust.games.go;

import java.util.HashSet;
import java.util.logging.Logger;

import com.sawdust.engine.controller.entities.GameSession;
import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.basetypes.BaseGame;
import com.sawdust.engine.model.players.Agent;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.state.GameCommand;
import com.sawdust.games.euchre.EuchreGame;
import com.sawdust.games.euchre.ai.Stupid1;
import com.sawdust.games.stop.ArrayPosition;
import com.sawdust.games.stop.BoardData;
import com.sawdust.games.stop.MoveCommand;
import com.sawdust.games.stop.StopAgent1;
import com.sawdust.games.stop.StopGame;
import com.sawdust.games.stop.StopIsland;
import com.sawdust.games.stop.StopGame.GamePhase;

public class GoAgent1 extends StopAgent1<GoGame>
{
   private static final Logger LOG        = Logger.getLogger(GoAgent1.class.getName());
   
   public GoAgent1(int depth, int expansion)
   {
      super(depth, expansion);
   }

   public static Agent<? extends StopGame> getAgent(String s, int depth, int expansion)
   {
       return new Agent<StopGame>(s, new GoAgent1(depth, expansion));
   }

   @Override
   protected BaseGame cloneForSimulation(BaseGame game) throws CloneNotSupportedException
   {
      return new GoGame((GoGame) game)
      {
         
         @Override
         public GameSession getSession()
         {
            return null;
         }
      };
      
   }
   
   @Override
   protected double gameFitness(GoGame game, Participant self) throws GameException
   {
      GoGame goGame = ((GoGame) game);
      if (null == game) return Integer.MIN_VALUE;
      if (null == self) return Integer.MIN_VALUE;
      int playerIdx = game.getPlayerManager().getPlayerIndex(self);
      int otherIdx = (playerIdx == 0) ? 1 : 0;
      if (GamePhase.Complete == game.getCurrentPhase())
      {
         if(game.getLastWinner() == playerIdx)
         {
            return Integer.MAX_VALUE;
         }
         else
         {
            return Integer.MIN_VALUE;
         }
      }
      int score1 = goGame._scores.get(self).getScore();
      int score2 = goGame._scores.get(game.getPlayerManager().getPlayerName(otherIdx)).getScore();
      int scoreDiff = score1 - score2;
      double fitness = scoreDiff * 1000;
      
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
   
   @Override
   protected double moveFitness(GameCommand o1, GoGame game, double v1)
   {
      if ((o1 instanceof MoveCommand))
      {
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
      else if (o1.getCommandText().equals("Pass"))
      {
         return 100;
      }
      return -1000;
   }
}
