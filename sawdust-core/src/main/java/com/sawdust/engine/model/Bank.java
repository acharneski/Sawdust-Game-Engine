package com.sawdust.engine.model;

import com.sawdust.engine.controller.exceptions.GameException;

public interface Bank
{
    void doWithdraw(int amount, Bank from, String description) throws GameException;

    int getBalance();
}
