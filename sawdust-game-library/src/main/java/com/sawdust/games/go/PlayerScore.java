package com.sawdust.games.go;

import java.io.Serializable;

public class PlayerScore implements Serializable
{
   public PlayerScore()
   {
      super();
   }
   public PlayerScore(PlayerScore value)
   {
      _prisoners = value._prisoners;
      _territory = value._territory;
   }
   private int _prisoners = 0;
   private int _territory = 0;
   public void addPrisoners(int prisoners)
   {
      this._prisoners += prisoners;
   }
   public int getPrisoners()
   {
      return _prisoners;
   }
   public void setTerritory(int territory)
   {
      this._territory = territory;
   }
   public int getTerritory()
   {
      return _territory;
   }
   public int getScore()
   {
      return _territory - _prisoners;
   }
   public void clear()
   {
      _prisoners = 0;
      _territory = 0;
   }
}
