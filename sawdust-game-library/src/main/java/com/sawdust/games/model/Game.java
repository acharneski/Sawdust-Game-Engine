package com.sawdust.games.model;


public interface Game
{
    Game doMove(Move move) throws GameWon, GameLost;
    Move[] getMoves(Player participant);
    Score getScore(Player p);
    Player[] getPlayers();
    Player getCurrentPlayer();
}
