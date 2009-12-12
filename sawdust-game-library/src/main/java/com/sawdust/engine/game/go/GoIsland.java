package com.sawdust.engine.game.go;

import java.util.HashMap;
import java.util.HashSet;

import com.sawdust.engine.game.stop.ArrayPosition;
import com.sawdust.engine.game.stop.StopIsland;
import com.sawdust.engine.game.stop.TokenArray;

public class GoIsland extends StopIsland
{
   private HashMap<GoIsland,Boolean> eyes = new HashMap<GoIsland, Boolean>(); 
   
   public GoIsland(ArrayPosition p, TokenArray ta)
   {
      super(p, ta);
   }

   public GoIsland(GoIsland obj)
   {
      super(obj);
   }

   public HashSet<GoIsland> getAdjacentIslands()
   {
      HashSet<GoIsland> neighbors = new HashSet<GoIsland>();
      for (ArrayPosition position : getPerimiter())
      {
         StopIsland island = _ta.getIsland(position);
         if(null == island) continue; 
         if(-1 == island.getPlayer()) continue; 
         neighbors.add((GoIsland) island);
      }
      return neighbors;
   }

   public void addEye(GoIsland stopIsland, boolean exclusive)
   {
      eyes.put(stopIsland, exclusive);
      
   }

   public HashMap<GoIsland,Boolean> getEyes()
   {
      return eyes;
   }
   
}
