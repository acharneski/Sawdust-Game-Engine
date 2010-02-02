package com.sawdust.games.model;

import com.sawdust.games.stop.immutable.GoPlayer;


public interface Game
{
    Game doMove(Move move) throws GameWon, GameLost;
    Move[] getMoves(GoPlayer player);
    Score getScore(GoPlayer p);
    GoPlayer[] getPlayers();
}
