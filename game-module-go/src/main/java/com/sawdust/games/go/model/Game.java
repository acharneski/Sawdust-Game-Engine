package com.sawdust.games.go.model;

public interface Game
{
    Game doMove(Move move);
    Move[] getMoves(Player participant);
    Score getScore(Player p);
    Player[] getPlayers();
    Player getCurrentPlayer();
    Player getWinner();
}
