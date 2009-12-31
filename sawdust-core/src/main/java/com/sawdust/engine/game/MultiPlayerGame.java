package com.sawdust.engine.game;

import com.sawdust.engine.service.debug.GameException;
import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.game.players.PlayerManager;

public interface MultiPlayerGame
{

    PlayerManager getPlayerManager();

    void doForceMove(Participant currentPlayer) throws GameException;
    
}
