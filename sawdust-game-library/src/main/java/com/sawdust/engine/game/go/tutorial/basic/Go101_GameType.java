package com.sawdust.engine.game.go.tutorial.basic;

import com.sawdust.engine.game.SessionFactory;
import com.sawdust.engine.game.basetypes.BaseGame;
import com.sawdust.engine.game.basetypes.TutorialGameBase;
import com.sawdust.engine.game.blackjack.BlackjackGame;
import com.sawdust.engine.game.blackjack.BlackjackGameType;
import com.sawdust.engine.game.go.GoGame;
import com.sawdust.engine.game.go.GoGameType;
import com.sawdust.engine.service.data.GameSession;

public class Go101_GameType extends GoGameType
{
    public static final Go101_GameType INSTANCE = new Go101_GameType();

    @Override
    public TutorialGameBase<GoGame> createNewGame(final com.sawdust.engine.common.config.GameConfig c, final SessionFactory sessionFactory)
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
