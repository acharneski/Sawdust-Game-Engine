package com.sawdust.engine.service.data;

import com.sawdust.engine.service.debug.GameException;

public interface SessionToken
{

    String getUserId();

    Account doLoadAccount();

    GameSession doLoadSession() throws GameException;

}
