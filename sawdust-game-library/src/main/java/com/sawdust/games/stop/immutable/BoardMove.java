package com.sawdust.games.stop.immutable;

import com.sawdust.games.model.Move;


public class BoardMove implements Move
{
    final BoardPosition position;
    final GoPlayer player;

    public BoardMove(final GoPlayer player, final BoardPosition position)
    {
        super();
        this.player = player;
        this.position = position;
    }
}