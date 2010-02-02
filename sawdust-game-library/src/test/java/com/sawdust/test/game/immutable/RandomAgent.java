/**
 * 
 */
package com.sawdust.test.game.immutable;

import com.sawdust.engine.controller.Util;
import com.sawdust.games.model.Agent;
import com.sawdust.games.model.Game;
import com.sawdust.games.model.Move;
import com.sawdust.games.model.Player;
import com.sawdust.games.stop.immutable.BoardMove;
import com.sawdust.games.stop.immutable.GoBoard;
import com.sawdust.games.stop.immutable.GoPlayer;

public final class RandomAgent implements Agent
{
    @Override
    public Move selectMove(Player p, Game game)
    {
        GoBoard goGame = (GoBoard) game;
        GoPlayer goPlayer = (GoPlayer) p;
        return getMove(goGame, goPlayer);
    }

    private BoardMove getMove(GoBoard goGame, GoPlayer goPlayer)
    {
        return Util.randomMember(goGame.getMoves(goPlayer));
    }
}