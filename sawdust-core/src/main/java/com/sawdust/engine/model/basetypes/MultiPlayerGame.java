package com.sawdust.engine.model.basetypes;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.players.PlayerManager;

public interface MultiPlayerGame
{

    PlayerManager getPlayerManager();

    MultiPlayerGame doForceMove(Participant currentPlayer) throws GameException;
    
}
