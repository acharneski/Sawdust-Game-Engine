package com.sawdust.engine.model.players;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.basetypes.BaseGame;
import com.sawdust.engine.model.basetypes.GameState;

public interface IMultiPlayer
{
    IMultiPlayer doAddMember(GameState game, Participant agent) throws GameException;

    IMultiPlayer doForceMove(BaseGame game, Participant participant) throws GameException, com.sawdust.engine.view.GameException;

    Agent<BaseGame> getAgent(String playerID);

    PlayerManager getPlayerManager();

    Agent<BaseGame> getTimeoutAgent();

    IMultiPlayer doRemoveMember(GameState game, Participant email) throws GameException;

    void setPlayerManager(PlayerManager playerManager);

    void setTimeoutAgent(Agent<?> timeoutAgent);
}
