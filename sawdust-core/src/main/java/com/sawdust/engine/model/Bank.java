package com.sawdust.engine.model;

import com.sawdust.engine.controller.exceptions.GameException;

public interface Bank
{
    int getBalance();

    void withdraw(int amount, Bank from, String description) throws GameException;
}
