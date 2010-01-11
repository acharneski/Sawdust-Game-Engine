package com.sawdust.games.stop.tutorial.basic;

import com.sawdust.engine.controller.entities.GameSession;
import com.sawdust.engine.model.SessionFactory;
import com.sawdust.engine.model.basetypes.BaseGame;
import com.sawdust.engine.model.basetypes.TutorialGameBase;
import com.sawdust.games.blackjack.BlackjackGame;
import com.sawdust.games.blackjack.BlackjackGameType;
import com.sawdust.games.stop.StopGame;
import com.sawdust.games.stop.StopGameType;

public class Stop101_GameType extends StopGameType
{
    public static final Stop101_GameType INSTANCE = new Stop101_GameType();

    @Override
    public TutorialGameBase<StopGame> getNewGame(final com.sawdust.engine.view.config.GameConfig c, final SessionFactory sessionFactory)
    {
        return new TutorialGame(c, sessionFactory);
    }
    
    @Override
    public String getDescription()
    {
        return "A basic tutorial on how to play Stop, a shorter variant of Go.";
    }

    @Override
    public String getName()
    {
        return "Stop 101";
    }

    protected Stop101_GameType()
    {
    }

    @Override
    public boolean isSubtype()
    {
        return true;
    }
    
}
