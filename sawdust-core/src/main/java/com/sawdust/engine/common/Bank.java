package com.sawdust.engine.common;

import com.sawdust.engine.common.GameException;

public interface Bank
{
    int getBalance();

    void withdraw(int amount, Bank from, String description) throws GameException;
}
