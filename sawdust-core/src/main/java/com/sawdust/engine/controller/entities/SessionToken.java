package com.sawdust.engine.controller.entities;

import com.sawdust.engine.controller.exceptions.GameException;

public interface SessionToken
{

    String getUserId();

    Account doLoadAccount();

    GameSession doLoadSession() throws GameException;

}
