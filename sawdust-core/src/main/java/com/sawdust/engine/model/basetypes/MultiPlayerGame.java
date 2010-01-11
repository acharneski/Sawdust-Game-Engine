package com.sawdust.engine.model.basetypes;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.players.PlayerManager;
import com.sawdust.engine.model.state.GameCommand;

public interface MultiPlayerGame extends GameState
{

    PlayerManager getPlayerManager();

    GameCommand<MultiPlayerGame> doForceMove(Participant currentPlayer) throws GameException;
    
}
