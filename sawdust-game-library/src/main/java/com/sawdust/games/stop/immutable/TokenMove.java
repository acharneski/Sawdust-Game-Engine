/**
 * 
 */
package com.sawdust.games.stop.immutable;


public class TokenMove
{
    final TokenPosition position;
    final Player player;

    public TokenMove(final Player player, final TokenPosition position)
    {
        super();
        this.player = player;
        this.position = position;
    }
}