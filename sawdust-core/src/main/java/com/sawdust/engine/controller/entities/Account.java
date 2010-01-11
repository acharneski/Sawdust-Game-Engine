package com.sawdust.engine.controller.entities;

import java.io.Serializable;

import com.sawdust.engine.controller.PromotionConfig;
import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.Bank;
import com.sawdust.engine.view.game.ActivityEvent;

public interface Account extends Bank
{
    String getUserId();
    
    String getName();

    void setName(String displayName);

    public boolean isAdmin();

    public void setAdmin(boolean isAdmin);

    void doRemoveSession(GameSession gameSession);

    <T extends Serializable> T getResource(Class<T> c);

    <T extends Serializable> void setResource(Class<T> c, T markovChain);

    Promotion doAwardPromotion(PromotionConfig p) throws GameException;

    void doLogActivity(ActivityEvent event);
}
