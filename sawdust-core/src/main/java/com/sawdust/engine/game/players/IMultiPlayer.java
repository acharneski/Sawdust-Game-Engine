package com.sawdust.engine.game.players;

import com.sawdust.engine.game.basetypes.BaseGame;
import com.sawdust.engine.game.basetypes.GameState;
import com.sawdust.engine.service.debug.GameException;

public interface IMultiPlayer
{
    void addMember(GameState game, Participant agent) throws GameException;

    void doForceMove(BaseGame game, Participant participant) throws GameException, com.sawdust.engine.common.GameException;

    Agent<BaseGame> getAgent(String playerID);

    PlayerManager getPlayerManager();

    Agent<BaseGame> getTimeoutAgent();

    void removeMember(GameState game, Participant email) throws GameException;

    void setPlayerManager(PlayerManager playerManager);

    void setTimeoutAgent(Agent<?> timeoutAgent);
}
