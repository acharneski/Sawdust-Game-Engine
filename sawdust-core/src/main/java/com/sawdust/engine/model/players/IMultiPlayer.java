package com.sawdust.engine.model.players;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.basetypes.BaseGame;
import com.sawdust.engine.model.basetypes.GameState;

public interface IMultiPlayer
{
    void addMember(GameState game, Participant agent) throws GameException;

    void doForceMove(BaseGame game, Participant participant) throws GameException, com.sawdust.engine.view.GameException;

    Agent<BaseGame> getAgent(String playerID);

    PlayerManager getPlayerManager();

    Agent<BaseGame> getTimeoutAgent();

    void removeMember(GameState game, Participant email) throws GameException;

    void setPlayerManager(PlayerManager playerManager);

    void setTimeoutAgent(Agent<?> timeoutAgent);
}
