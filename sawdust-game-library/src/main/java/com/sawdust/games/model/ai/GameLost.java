package com.sawdust.games.model.ai;

import com.sawdust.games.model.Game;
import com.sawdust.games.model.Player;

@SuppressWarnings("serial")
public class GameLost extends GameWon
{
    public final Player loser;

    public GameLost(final Game game, final Player winner, final Player loser)
    {
        super(game, winner);
        this.loser = loser;
    }

    @Override
    public String toString()
    {
        return "GameLost [loser=" + loser + ", game=" + game + ", winner=" + winner + "]";
    }

}
