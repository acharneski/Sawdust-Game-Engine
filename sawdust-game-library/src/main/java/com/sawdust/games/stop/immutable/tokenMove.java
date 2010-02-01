/**
 * 
 */
package com.sawdust.games.stop.immutable;


public class tokenMove
{
    final tokenPosition position;
    final player player;

    public tokenMove(final player player, final tokenPosition position)
    {
        super();
        this.player = player;
        this.position = position;
    }
}