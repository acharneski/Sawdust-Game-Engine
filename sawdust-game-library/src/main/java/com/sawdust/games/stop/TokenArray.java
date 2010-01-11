package com.sawdust.games.stop;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.state.IndexPosition;
import com.sawdust.engine.model.state.Token;

public class TokenArray implements Serializable
{
   private static final Logger     LOG         = Logger.getLogger(TokenArray.class.getName());
   
   public static final int         EMPTY_VALUE = -1;                                           ;
   
   protected transient ArrayList<StopIsland> _islands    = null;
   protected int                   _boardData[][];
   protected int             _numCols;
   protected int             _numRows;
   
   public TokenArray(final TokenArray obj)
   {
      _numCols = obj._numCols;
      _numRows = obj._numRows;
      _boardData = new int[_numRows][_numCols];
      
      for (int i = 0; i < StopGame.NUM_ROWS; i++)
      {
         for (int j = 0; j < StopGame.NUM_ROWS; j++)
         {
            _boardData[i][j] = obj._boardData[i][j];
         }
      }
      if (null != _islands)
      {
         _islands = new ArrayList<StopIsland>();
         for (final StopIsland i : obj._islands)
         {
            _islands.add(new StopIsland(i));
         }
      }
   }
   
   public TokenArray(final int numRows, final int numCols, StopGame game)
   {
      _numCols = numCols;
      _numRows = numRows;
      _boardData = new int[_numRows][_numCols];
      
      for (int i = 0; i < StopGame.NUM_ROWS; i++)
      {
         for (int j = 0; j < StopGame.NUM_ROWS; j++)
         {
            BoardData boardData = game.getBoardData(i, j);
            _boardData[i][j] = (null == boardData) ? -1 : boardData.value;
         }
      }
   }
   
   protected TokenArray()
    {
        // TODO Auto-generated constructor stub
    }

public void cleanIslands(final int playerIdx, final StopGame game, final boolean modifyGame)
   {
      ArrayList<StopIsland> islands = getIslands();
      for (final StopIsland i : islands)
      {
         if (playerIdx == i.getPlayer())
         {
            continue;
         }
         if (-1 == i.getPlayer())
         {
            continue;
         }
         if (i.isSurrounded())
         {
            captureIsland(game, i, modifyGame);
         }
      }
   }
   
   protected void captureIsland(final StopGame game, final StopIsland i, final boolean modifyGame)
   {
      final ArrayList<ArrayPosition> allPositions = i.getAllPositions();
      if (modifyGame)
      {
         final Participant islandOwner = game.getPlayerManager().getPlayerName(i.getPlayer());
         final String displayName = game.getDisplayName(islandOwner);
         game.doAddMessage("%d pieces captured from %s", allPositions.size(), displayName);
      }
      for (final ArrayPosition p2 : allPositions)
      {
         this.setPosition(p2.row, p2.col, -1);
         if (modifyGame)
         {
            game.setBoardData(p2.row, p2.col, -1);
         }
      }
   }
   
   public ElementState get(final ArrayPosition p)
   {
      int i = _boardData[p.row][p.col];
      return (i < 0) ? null : new ElementState(i);
   }
   
   public ArrayList<ArrayPosition> getAllPositions()
   {
      final ArrayList<ArrayPosition> arrayList = new ArrayList<ArrayPosition>();
      for (int i = 0; i < _numRows; i++)
      {
         for (int j = 0; j < _numCols; j++)
         {
            arrayList.add(new ArrayPosition(i, j));
         }
      }
      return arrayList;
   }
   
   public int getPosition(final ArrayPosition key)
   {
      if (key.col < 0) return -1;
      if (key.col >= StopGame.NUM_ROWS) return -1;
      if (key.row >= StopGame.NUM_ROWS) return -1;
      if (key.row < 0) return -1;
      return _boardData[key.row][key.col];
   }
   
   public int getPosition(final int i, final int j)
   {
      return _boardData[i][j];
   }
   
   public Set<ArrayPosition> getPositions()
   {
      final HashSet<ArrayPosition> arrayList = new HashSet<ArrayPosition>();
      for (int i = 0; i < _numRows; i++)
      {
         for (int j = 0; j < _numCols; j++)
         {
            if (0 <= _boardData[i][j])
            {
               arrayList.add(new ArrayPosition(i, j));
            }
         }
      }
      return arrayList;
   }
   
   public Set<ArrayPosition> getPositions(final int findPlayer)
   {
      final HashSet<ArrayPosition> arrayList = new HashSet<ArrayPosition>();
      for (int i = 0; i < _numRows; i++)
      {
         for (int j = 0; j < _numCols; j++)
         {
            if (findPlayer == _boardData[i][j])
            {
               arrayList.add(new ArrayPosition(i, j));
            }
         }
      }
      return arrayList;
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
   
   public ArrayList<StopIsland> getIslands()
   {
      if (null == _islands)
      {
         _islands = initIslands();
      }
      ArrayList<StopIsland> arrayList = new ArrayList<StopIsland>();
      arrayList.addAll(_islands);
      return arrayList;
   }
   
   protected ArrayList<StopIsland> initIslands()
   {
      _islands = new ArrayList<StopIsland>();
      for (final ArrayPosition p : getAllPositions())
      {
         StopIsland found = getIsland(p);
         if (null == found)
         {
            _islands.add(seedIsland(p));
         }
      }
      return _islands;
   }
   
   public StopIsland getIsland(final ArrayPosition p)
   {
      StopIsland found = null;
      for (final StopIsland i : _islands)
      {
         if (i.contains(p))
         {
            found = i;
            break;
         }
      }
      return found;
   }
   
   protected StopIsland seedIsland(final ArrayPosition p)
   {
      return new StopIsland(p, this);
   }
   
   public void setPosition(final ArrayPosition arrayPosition, final int playerIdx)
   {
      setPosition(arrayPosition.row, arrayPosition.col, playerIdx);
   }
   
   public void setPosition(final int i, final int j, final int k)
   {
      _boardData[i][j] = k;
      _islands = null;
   }
   
   public int getNumCols()
   {
      return _numCols;
   }
   
   public int getNumRows()
   {
      return _numRows;
   }
   
   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      for(int[] x : _boardData)
      {
         result = prime * result + Arrays.hashCode(x);
      }
      result = prime * result + _numCols;
      result = prime * result + _numRows;
      return result;
   }
   
   @Override
   public boolean equals(Object obj)
   {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      TokenArray other = (TokenArray) obj;
      if (_boardData.length != other._boardData.length) return false;
      for(int i=0; i<_boardData.length; i++)
      {
         if (!Arrays.equals(_boardData[i], other._boardData[i])) return false;
      }
      if (_numCols != other._numCols) return false;
      if (_numRows != other._numRows) return false;
      return true;
   }
   
}
