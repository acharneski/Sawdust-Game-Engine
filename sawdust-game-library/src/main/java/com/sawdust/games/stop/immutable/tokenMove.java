/**
 * 
 */
package com.sawdust.games.stop.immutable;


class tokenMove
{
    final tokenPosition position;
    final int player;

    public tokenMove(final int player, final tokenPosition position)
    {
        super();
        this.player = player;
        this.position = position;
    }
}