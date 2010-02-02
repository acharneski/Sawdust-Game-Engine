package com.sawdust.games.model;

@SuppressWarnings("serial")
public class GameLost extends GameWon
{
    public final Player loser;

    public GameLost(final Player winner, final Player loser)
    {
        super(winner);
        this.loser = loser;
    }

}
