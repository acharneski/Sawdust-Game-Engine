package com.sawdust.games.go.model;

import java.util.Date;



public interface Agent
{
    Move selectMove(Player p, Game game, Date deadline);

}
