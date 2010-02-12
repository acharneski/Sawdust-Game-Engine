/**
 * 
 */
package com.sawdust.games.go.controller.ai;

import java.util.LinkedList;

import com.sawdust.games.go.model.Game;
import com.sawdust.games.go.model.Move;

public class GameScript
{
    private final LinkedList<GameTransition> moves = new LinkedList<GameTransition>();
    public final Game startGame;
    public final Game endGame;

    public GameScript(GameScript start, GameTransition t)
    {
        super();
        this.startGame = start.startGame;
        moves.addAll(start.moves);
        moves.add(t);
        this.endGame = t.endGame;
    }

    public Move firstMove()
    {
        Move move = moves.peek().move;
        assert(null != move);
        return move;
    }

    public GameScript(Game startGame)
    {
        super();
        this.startGame = startGame;
        this.endGame = null;
    }

    public GameScript(GameScript start, GameScript end)
    {
        this.startGame = start.startGame;
        moves.addAll(start.moves);
        moves.addAll(end.moves);
        this.endGame = end.endGame;
    }
}