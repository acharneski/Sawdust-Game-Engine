package com.sawdust.engine.game.wordHunt;

import java.io.Serializable;

import com.sawdust.engine.service.PromotionConfig;

public class WordHuntLoot implements Serializable
{
    int wins = 0;

    public PromotionConfig getLoot()
    {
        double odds = 0.3;
        if(0 == wins) odds  = 1.0;
        if(Math.random() > odds) return null;
        wins++;
        String title = "Social Loot - Word Hunt";
        String msg = 
            "I won some loot playing Word Hunt at Sawdust Games, and I'd like to share... "+
            "The first 5 players to visit %s will get 50 free credits!";
        String attachment = "{}";
        return new PromotionConfig(5, title, 50, msg, attachment);
    }

}
