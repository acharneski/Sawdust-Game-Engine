package com.sawdust.engine.game;

import com.sawdust.engine.service.debug.GameException;

public interface Bank
{
    int getBalance();

    void withdraw(int amount, Bank from, String description) throws GameException;
}
