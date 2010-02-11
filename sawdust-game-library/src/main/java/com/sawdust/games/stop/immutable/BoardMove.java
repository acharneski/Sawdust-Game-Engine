package com.sawdust.games.stop.immutable;

import com.sawdust.games.model.Move;


public class BoardMove implements Move
{
    public final BoardPosition position;
    public final GoPlayer player;
    public final Island island;

    public BoardMove(final GoPlayer player, final BoardPosition position, final Island island)
    {
        super();
        this.player = player;
        this.position = position;
        this.island = island;
        if(null != position)
        {
            assert(null != this.island);
            assert(this.island.contains(position));
        }
        else
        {
            assert(null == this.island);
        }
        
    }

    @Override
    public String toString()
    {
        return "BoardMove [player=" + player + ", position=" + position + "]";
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((island == null) ? 0 : island.hashCode());
        result = prime * result + ((player == null) ? 0 : player.hashCode());
        result = prime * result + ((position == null) ? 0 : position.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        BoardMove other = (BoardMove) obj;
        if (island == null)
        {
            if (other.island != null) return false;
        }
        else if (!island.equals(other.island)) return false;
        if (player == null)
        {
            if (other.player != null) return false;
        }
        else if (!player.equals(other.player)) return false;
        if (position == null)
        {
            if (other.position != null) return false;
        }
        else if (!position.equals(other.position)) return false;
        return true;
    }


}