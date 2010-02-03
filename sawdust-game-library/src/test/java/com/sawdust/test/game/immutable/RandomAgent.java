/**
 * 
 */
package com.sawdust.test.game.immutable;

import java.util.Date;

import com.sawdust.engine.controller.Util;
import com.sawdust.games.model.Agent;
import com.sawdust.games.model.Game;
import com.sawdust.games.model.Move;
import com.sawdust.games.model.Player;
import com.sawdust.games.model.ai.GameLost;
import com.sawdust.games.model.ai.GameWon;
import com.sawdust.games.stop.immutable.BoardMove;
import com.sawdust.games.stop.immutable.GoBoard;
import com.sawdust.games.stop.immutable.GoPlayer;

public final class RandomAgent implements Agent
{
    @Override
    public Move selectMove(Player p, Game game, Date deadline)
    {
        GoBoard goGame = (GoBoard) game;
        GoPlayer goPlayer = (GoPlayer) p;
        return getMove(goGame, goPlayer);
    }

    private BoardMove getMove(GoBoard goGame, GoPlayer goPlayer)
    {
        BoardMove[] moves = goGame.getMoves(goPlayer);
        BoardMove move = null;
        while(move == null)
        {
            move = Util.randomMember(moves);
            if(move.position == null)
            {
                if(goGame.lastPlayerPassed)
                {
                    if(moves.length > 1)
                    {
                        GoPlayer[] players = goGame.getPlayers();
                        Player otherPlayer = players[0].equals(goPlayer)?players[1]:players[0];
                        if(goGame.getScore(otherPlayer).getValue() > goGame.getScore(goPlayer).getValue())
                        {
                            move = null;
                        }
                    }
                }
            }
        }
        return move;
    }
}