package com.sawdust.engine.game.stop;

import java.io.Serializable;

import com.sawdust.engine.game.PromotionConfig;

public class StopLoot implements Serializable
{
    int wins = 0;
    
    public PromotionConfig getLoot()
    {
        double odds = 1.0;
        if(0 == wins) odds  = 1.0;
        if(Math.random() > odds) return null;
        wins++;
        String title = "Social Loot - Stop";
        String msg = "I won some loot playing Stop at Sawdust Games, and I'd like to share... ";
        
        String mediaData = String.format("[{'type':'image','src':'%s','href':'%s'}]",
                "http://sawdust-games.appspot.com/media/go.png",
                "http://apps.facebook.com/sawdust-games/quickPlay.jsp?game=Stop");
        String attachment = String.format("{'name':'Join My Game','media':%s,'href':'%s','description':'%s'}", 
                mediaData,
                "PROMOLINK",
                "The first 5 players to visit %s will get 50 free credits!");
        PromotionConfig promotionConfig = new PromotionConfig(5, title, 50, msg, attachment);
        return promotionConfig;
    }

}
