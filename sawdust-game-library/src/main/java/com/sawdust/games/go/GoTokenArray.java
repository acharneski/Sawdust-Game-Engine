package com.sawdust.games.go;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Logger;

import com.sawdust.engine.model.players.Participant;
import com.sawdust.games.stop.ArrayPosition;
import com.sawdust.games.stop.StopGame;
import com.sawdust.games.stop.StopIsland;
import com.sawdust.games.stop.TokenArray;

public class GoTokenArray extends TokenArray
{
   private static final Logger LOG = Logger.getLogger(GoTokenArray.class.getName());
   
   public GoTokenArray()
   {
      super();
   }
   
   public GoTokenArray(final GoTokenArray obj)
   {
      super(obj);
   }
   
   public GoTokenArray(final int numRows, final int numCols, StopGame game)
   {
      super(numRows, numCols, game);
   }
   
   public int getScore(final int playerIdx)
   {
      int score = 0;
      for (final ArrayPosition p : getAllPositions())
      {
         if (getPosition(p) == playerIdx)
         {
            score++;
         }
      }
      return score;
   }
   
   @Override
   protected ArrayList<StopIsland> initIslands()
   {
      ArrayList<StopIsland> arrayList = super.initIslands();
      for (StopIsland stopIsland : arrayList)
      {
         if (-1 == stopIsland.getPlayer())
         {
            HashSet<GoIsland> adjacentIslands = ((GoIsland) stopIsland).getAdjacentIslands();
            HashSet<Integer> surroundingPlayers = new HashSet<Integer>();
            for (GoIsland goIsland : adjacentIslands)
            {
               int player = goIsland.getPlayer();
               surroundingPlayers.add(player);
            }
            if(1 == surroundingPlayers.size())
            {
               for (GoIsland goIsland : adjacentIslands)
               {
                  goIsland.addEye((GoIsland) stopIsland, (1 == adjacentIslands.size()));
               }
            }
         }
      }
      return arrayList;
   }
   
   @Override
   protected GoIsland seedIsland(final ArrayPosition p)
   {
      return new GoIsland(p, this);
   }
   
   @Override
   protected void captureIsland(StopGame game, StopIsland i, boolean modifyGame)
   {
      if (modifyGame)
      {
         ((GoGame) game).doCaptureIsland(i);
      }
      super.captureIsland(game, i, modifyGame);
   }
   
}
