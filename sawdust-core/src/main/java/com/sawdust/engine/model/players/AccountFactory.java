package com.sawdust.engine.model.players;

import java.io.Serializable;

import com.sawdust.engine.controller.entities.Account;

public abstract class AccountFactory implements Serializable
{
    public abstract Account getAccount();

}
