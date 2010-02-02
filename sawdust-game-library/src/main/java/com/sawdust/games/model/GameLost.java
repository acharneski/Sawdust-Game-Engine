package com.sawdust.games.model;

public class GameLost extends GameWon
{
    private final Player loser;

    public GameLost(final Player winner, final Player loser)
    {
        super(winner);
        this.loser = loser;
    }

}
