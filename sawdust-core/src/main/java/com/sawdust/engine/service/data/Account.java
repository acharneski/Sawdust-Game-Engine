package com.sawdust.engine.service.data;

import java.io.Serializable;

import com.sawdust.engine.game.Bank;
import com.sawdust.engine.game.PromotionConfig;
import com.sawdust.engine.game.players.ActivityEvent;
import com.sawdust.engine.service.debug.GameException;

public interface Account extends Bank
{
    String getUserId();
    
    String getName();

    void setName(String displayName);

    public boolean isAdmin();

    public void setAdmin(boolean isAdmin);

    void removeSession(GameSession gameSession);

    <T extends Serializable> T getResource(Class<T> c);

    <T extends Serializable> void setResource(Class<T> c, T markovChain);

    Promotion awardPromotion(PromotionConfig p) throws GameException;
}
