package com.sawdust.engine.service.data;

import com.sawdust.engine.common.Bank;
import com.sawdust.engine.game.players.ActivityEvent;

public interface Account extends Bank
{
    String getName();

    String getUserId();

    public boolean isAdmin();

    void removeSession(GameSession gameSession);

    public void setAdmin(boolean isAdmin);

    void setName(String displayName);
}
