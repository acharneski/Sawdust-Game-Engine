/**
 * 
 */
package com.sawdust.games.model.ai;

import com.sawdust.games.model.Game;
import com.sawdust.games.model.Move;

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