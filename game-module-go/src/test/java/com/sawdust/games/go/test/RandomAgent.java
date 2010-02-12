/**
 * 
 */
package com.sawdust.games.go.test;

import java.util.Date;

import com.sawdust.engine.controller.Util;
import com.sawdust.games.go.controller.GoBoard;
import com.sawdust.games.go.model.Agent;
import com.sawdust.games.go.model.BoardMove;
import com.sawdust.games.go.model.Game;
import com.sawdust.games.go.model.GoPlayer;
import com.sawdust.games.go.model.Move;
import com.sawdust.games.go.model.Player;

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
        if(null == moves) return null;
        if(0 == moves.length) return null;
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