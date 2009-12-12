package com.sawdust.engine.game.stop.tutorial.basic;

import com.sawdust.engine.game.BaseGame;
import com.sawdust.engine.game.SessionFactory;
import com.sawdust.engine.game.TutorialGameBase;
import com.sawdust.engine.game.blackjack.BlackjackGame;
import com.sawdust.engine.game.blackjack.BlackjackGameType;
import com.sawdust.engine.game.stop.StopGame;
import com.sawdust.engine.game.stop.StopGameType;
import com.sawdust.engine.service.data.GameSession;

public class Stop101_GameType extends StopGameType
{
    public static final Stop101_GameType INSTANCE = new Stop101_GameType();

    @Override
    public TutorialGameBase<StopGame> createNewGame(final com.sawdust.engine.common.config.GameConfig c, final SessionFactory sessionFactory)
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
}
