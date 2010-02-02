package com.sawdust.games.model;

import com.sawdust.games.model.ai.GameLost;
import com.sawdust.games.model.ai.GameWon;


public interface Game
{
    Game doMove(Move move) throws GameWon, GameLost;
    Move[] getMoves(Player participant);
    Score getScore(Player p);
    Player[] getPlayers();
    Player getCurrentPlayer();
}
