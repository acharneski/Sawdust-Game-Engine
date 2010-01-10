/**
 * 
 */
package com.sawdust.games.stop;

import java.util.ArrayList;


public class ArrayPosition
{
    /**
	 * 
	 */
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

    public ArrayList<ArrayPosition> getNeighbors()
    {
        final ArrayList<ArrayPosition> arrayList = new ArrayList<ArrayPosition>();
        arrayList.add(new ArrayPosition(row + 1, col));
        arrayList.add(new ArrayPosition(row - 1, col));
        arrayList.add(new ArrayPosition(row, col - 1));
        arrayList.add(new ArrayPosition(row, col + 1));
        return arrayList;
    }

    @Override
    public int hashCode()
    {
        return col ^ row;
    }

    public boolean isNear(TokenArray board, final int otherPlayer)
    {
        for (final ArrayPosition p : getNeighbors())
        {
            if (otherPlayer == board.getPosition(p)) return true;
        }
        return false;
    }

}