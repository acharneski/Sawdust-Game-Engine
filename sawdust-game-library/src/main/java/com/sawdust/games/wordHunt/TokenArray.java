package com.sawdust.games.wordHunt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TokenArray implements Serializable
{
    public class ArrayPosition implements Serializable
    {
        public int col = 0;
        public int row = 0;

        /**
         * @param prow
         * @param pcol
         */
        public ArrayPosition(final int prow, final int pcol)
        {
            super();
            row = prow;
            col = pcol;
        }
        
        protected ArrayPosition()
        {
            super();
        }

        @Override
        public boolean equals(final Object obj)
        {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            final ArrayPosition other = (ArrayPosition) obj;
            if (col != other.col) return false;
            if (row != other.row) return false;
            return true;
        }

        public ArrayList<ArrayList<ArrayPosition>> getNeighborChain(final int i)
        {
            return getNeighborChain(i, new HashSet<ArrayPosition>());
        }

        public ArrayList<ArrayList<ArrayPosition>> getNeighborChain(final int i, final HashSet<ArrayPosition> dirtyPath)
        {
            if (i < 0) throw new IndexOutOfBoundsException();
            final ArrayList<ArrayList<ArrayPosition>> arrayList = new ArrayList<ArrayList<ArrayPosition>>();
            if (i == 0)
            {
                final ArrayList<ArrayPosition> rootPath = new ArrayList<ArrayPosition>();
                rootPath.add(this);
                arrayList.add(rootPath);
                return arrayList;
            }
            else
            {
                dirtyPath.add(this);
                final boolean isFilled = containsKey(this);
                for (final ArrayPosition p : getNeighbors())
                {
                    if (containsKey(p) && !dirtyPath.contains(p))
                    {
                        final HashSet<ArrayPosition> hashSet = new HashSet<ArrayPosition>(dirtyPath);
                        for (final ArrayList<ArrayPosition> k : p.getNeighborChain(i - 1, hashSet))
                        {
                            if (isFilled)
                            {
                                k.add(0, this);
                            }
                            arrayList.add(k);
                        }
                    }
                }
            }
            return arrayList;
        }

        public ArrayList<ArrayPosition> getNeighbors()
        {
            final ArrayList<ArrayPosition> arrayList = new ArrayList<ArrayPosition>();
            arrayList.add(new ArrayPosition(row + 1, col));
            arrayList.add(new ArrayPosition(row - 1, col));
            arrayList.add(new ArrayPosition(row, col - 1));
            arrayList.add(new ArrayPosition(row, col + 1));
            arrayList.add(new ArrayPosition(row + 1, col - 1));
            arrayList.add(new ArrayPosition(row - 1, col + 1));
            arrayList.add(new ArrayPosition(row - 1, col - 1));
            arrayList.add(new ArrayPosition(row + 1, col + 1));
            return arrayList;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + col;
            result = prime * result + row;
            return result;
        }
    };

    class ElementState implements Serializable
    {
        String state = "";

        public ElementState(final String pstate)
        {
            super();
            state = pstate;
        }

        protected ElementState()
        {
            super();
        }
    }

    private HashMap<ArrayPosition, ElementState> _map = new HashMap<ArrayPosition, ElementState>();
    private int _numCols = 0;
    private int _numRows = 0;

    public TokenArray(final int numRows, final int numCols)
    {
        _numCols = numCols;
        _numRows = numRows;
    }

    protected TokenArray()
    {
        super();
    }

    public boolean containsKey(final ArrayPosition n)
    {
        return _map.containsKey(n);
    }

    public ElementState get(final ArrayPosition p)
    {
        ElementState elementState = new ElementState("");
        if (_map.containsKey(p))
        {
            elementState = _map.get(p);
        }
        return elementState;
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

    public ElementState getPosition(final ArrayPosition key)
    {
        if (!_map.containsKey(key)) return new ElementState("");
        final ElementState value = get(key);
        return value;
    }

    public Set<ArrayPosition> getPositions()
    {
        return _map.keySet();

    }

    public void put(final ArrayPosition p, final ElementState winningString)
    {
        _map.put(p, winningString);
    }

}
