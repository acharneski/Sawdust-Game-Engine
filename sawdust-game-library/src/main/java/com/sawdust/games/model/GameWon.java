package com.sawdust.games.model;

@SuppressWarnings("serial")
public class GameWon extends Exception
{

    public GameWon(final Player winner)
    {
        super();
        this.winner = winner;
    }

    public final Player winner;

}
