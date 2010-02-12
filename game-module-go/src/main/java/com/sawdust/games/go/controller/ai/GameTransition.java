/**
 * 
 */
package com.sawdust.games.go.controller.ai;

import com.sawdust.games.go.model.Game;
import com.sawdust.games.go.model.Move;

public class GameTransition
{
    public final Game startGame;
    public final Move move;
    public final Game endGame;
    public GameTransition(Game startGame, Move move, Game endGame)
    {
        super();
        this.startGame = startGame;
        this.move = move;
        this.endGame = endGame;
    }
}