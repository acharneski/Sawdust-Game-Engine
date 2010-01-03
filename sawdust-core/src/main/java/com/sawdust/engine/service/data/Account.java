package com.sawdust.engine.service.data;

import java.io.Serializable;

import com.sawdust.engine.game.Bank;
import com.sawdust.engine.game.players.ActivityEvent;

public interface Account extends Bank
{
    String getName();

    String getUserId();

    public boolean isAdmin();

    void removeSession(GameSession gameSession);

    public void setAdmin(boolean isAdmin);

    void setName(String displayName);

    <T extends Serializable> T getResource(Class<T> c);

    <T extends Serializable> void setResource(Class<T> c, T markovChain);
}
