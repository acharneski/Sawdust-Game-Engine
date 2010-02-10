package com.sawdust.games.model.ai;

import com.sawdust.games.model.Game;
import com.sawdust.games.model.Player;
import com.sawdust.games.stop.immutable.GoBoard;

@SuppressWarnings("serial")
public class GameWon extends Exception
{

    public final Player winner;
    public final Game game;

    public GameWon(final Game game, final Player winner)
    {
        super();
        this.game = game;
        this.winner = winner;
    }

    @Override
    public String toString()
    {
        return "GameWon [game=" + game + ", winner=" + winner + "]";
    }


}
