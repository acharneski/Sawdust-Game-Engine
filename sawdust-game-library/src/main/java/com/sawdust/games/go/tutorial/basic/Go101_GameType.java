package com.sawdust.games.go.tutorial.basic;

import com.sawdust.engine.controller.entities.GameSession;
import com.sawdust.engine.model.SessionFactory;
import com.sawdust.engine.model.basetypes.BaseGame;
import com.sawdust.engine.model.basetypes.TutorialGameBase;
import com.sawdust.games.blackjack.BlackjackGame;
import com.sawdust.games.blackjack.BlackjackGameType;
import com.sawdust.games.go.GoGame;
import com.sawdust.games.go.GoGameType;

public class Go101_GameType extends GoGameType
{
    public static final Go101_GameType INSTANCE = new Go101_GameType();

    @Override
    public TutorialGameBase<GoGame> createNewGame(final com.sawdust.engine.view.config.GameConfig c, final SessionFactory sessionFactory)
    {
        return new TutorialGame(c, sessionFactory);
    }
    
    @Override
    public String getDescription()
    {
        return "A basic tutorial on how to play Go.";
    }

    @Override
    public String getName()
    {
        return "Go 101";
    }

    protected Go101_GameType()
    {
    }

    @Override
    public boolean isSubtype()
    {
        return true;
    }
    
}
