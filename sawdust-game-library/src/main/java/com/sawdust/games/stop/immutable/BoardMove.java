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
    }


    @Override
    public String toString()
    {
        return "BoardMove [player=" + player + ", position=" + position + "]";
    }


}