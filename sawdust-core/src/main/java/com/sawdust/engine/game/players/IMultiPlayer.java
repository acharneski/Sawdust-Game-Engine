package com.sawdust.engine.game.players;

import com.sawdust.engine.game.BaseGame;
import com.sawdust.engine.game.Game;
import com.sawdust.engine.service.debug.GameException;

public interface IMultiPlayer
{
    void addMember(Game game, Participant agent) throws GameException;

    void doForceMove(BaseGame game, Participant participant) throws GameException, com.sawdust.engine.common.GameException;

    Agent<BaseGame> getAgent(String playerID);

    PlayerManager getPlayerManager();

    Agent<BaseGame> getTimeoutAgent();

    void removeMember(Game game, Participant email) throws GameException;

    void setPlayerManager(PlayerManager playerManager);

    void setTimeoutAgent(Agent<?> timeoutAgent);
}
