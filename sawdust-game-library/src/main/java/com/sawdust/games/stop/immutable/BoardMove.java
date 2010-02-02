package com.sawdust.games.stop.immutable;

import com.sawdust.games.model.Move;


public class BoardMove implements Move
{
    public final BoardPosition position;
    public final GoPlayer player;

    public BoardMove(final GoPlayer player, final BoardPosition position)
    {
        super();
        this.player = player;
        this.position = position;
    }


    @Override
    public String toString()
    {
        return "BoardMove [player=" + player + ", position=" + position + "]";
    }


}