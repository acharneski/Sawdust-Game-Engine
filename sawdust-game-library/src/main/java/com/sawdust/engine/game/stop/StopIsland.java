package com.sawdust.engine.game.stop;

import java.util.ArrayList;
import java.util.HashSet;

import com.sawdust.engine.service.debug.SawdustSystemError;

public class StopIsland implements Island
{
   
    private final int _player;
    protected final TokenArray _ta;
    boolean freeSpace = false;
    HashSet<ArrayPosition> positions = new HashSet<ArrayPosition>();
    boolean touchBottom = false;
    boolean touchLeft = false;
    boolean touchRight = false;
    boolean touchTop = false;
    public int posBottom = Integer.MIN_VALUE;
    public int posLeft = Integer.MAX_VALUE;
    public int posRight = Integer.MIN_VALUE;
    public int posTop = Integer.MAX_VALUE;
    public HashSet<ArrayPosition> openBorder = new HashSet<ArrayPosition>();
    
    public StopIsland(final ArrayPosition p, final TokenArray ta)
    {
        _player = ta.getPosition(p);
        _ta = ta;
        init(p);
    }
    
    public StopIsland(StopIsland obj)
    {
        _player = obj._player;
        _ta = obj._ta;
        freeSpace = obj.freeSpace;
        positions = new HashSet<ArrayPosition>(obj.positions);
        touchBottom = obj.touchBottom;
        touchLeft = obj.touchLeft;
        touchRight = obj.touchRight;
        touchTop = obj.touchTop;
        openBorder = new HashSet<ArrayPosition>(obj.openBorder);
    }
    
    public boolean contains(final ArrayPosition p)
    {
        return positions.contains(p);
    }
    
    public ArrayList<ArrayPosition> getAllPositions()
    {
        return new ArrayList<ArrayPosition>(positions);
    }
        
    public HashSet<ArrayPosition> getPerimiter()
    {
        final HashSet<ArrayPosition> hashSet = new HashSet<ArrayPosition>();
        final ArrayList<ArrayPosition> allPositions = getAllPositions();
        for (final ArrayPosition p2 : allPositions)
        {
            for (final ArrayPosition n : p2.getNeighbors())
            {
                hashSet.add(n);
            }
        }
        return hashSet;
    }
    
    public int getPlayer()
    {
        return _player;
    }
    
    private void init(ArrayPosition init)
    {
        HashSet<ArrayPosition> newPositions = new HashSet<ArrayPosition>();
        newPositions.add(init);
        
        while (!newPositions.isEmpty())
        {
            HashSet<ArrayPosition> currentBatch = new HashSet<ArrayPosition>(newPositions);
            positions.addAll(newPositions);
            newPositions.clear();
            for (final ArrayPosition p2 : currentBatch)
            {
                for (final ArrayPosition n : p2.getNeighbors())
                {
                    if (positions.contains(n))
                    {
                        continue;
                    }
                    if (n.row >= StopGame.NUM_ROWS)
                    {
                        touchBottom = true;
                    }
                    else if (n.row < 0)
                    {
                        touchTop = true;
                    }
                    else if (n.col >= StopGame.NUM_ROWS)
                    {
                        touchRight = true;
                    }
                    else if (n.col < 0)
                    {
                        touchLeft = true;
                    }
                    else
                    {
                        if (n.row >= posBottom)
                        {
                            posBottom = n.row;
                        }
                        else if (n.row < posTop)
                        {
                            posTop = n.row;
                        }
                        else if (n.col >= posRight)
                        {
                            posRight = n.col;
                        }
                        else if (n.col < posLeft)
                        {
                            posLeft = n.col;
                        }
                        final int position = _ta.getPosition(n);
                        if (_player == position)
                        {
                            newPositions.add(n);
                        }
                        else if (-1 == position)
                        {
                            openBorder.add(n);
                            freeSpace = true;
                        }
                    }
                }
            }
        }
    }
    
    public boolean isImmortal()
    {
        if (touchRight && touchLeft) return true;
        if (touchBottom && touchTop) return true;
        return false;
    }
    
    public boolean isSurrounded()
    {
        if (isImmortal()) return false;
        return !freeSpace;
    }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + _player;
      result = prime * result + ((positions == null) ? 0 : positions.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      StopIsland other = (StopIsland) obj;
      if (_player != other._player) return false;
      if (positions == null)
      {
         if (other.positions != null) return false;
      }
      else if (!positions.equals(other.positions)) return false;
      return true;
   }
    

   public int getSize()
   {
      return getAllPositions().size();
   }
}
