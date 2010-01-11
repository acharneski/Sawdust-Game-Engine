package com.sawdust.games.stop;

import java.util.ArrayList;
import java.util.List;

import com.sawdust.engine.controller.entities.Account;
import com.sawdust.engine.controller.entities.GameSession;
import com.sawdust.engine.model.GameType;
import com.sawdust.engine.model.SessionFactory;
import com.sawdust.engine.model.basetypes.BaseGame;
import com.sawdust.engine.model.basetypes.GameState;
import com.sawdust.engine.view.config.GameConfig;
import com.sawdust.games.blackjack.BlackjackGame;

public class StopGameType extends com.sawdust.engine.model.GameType<StopGame>
{
    public static final StopGameType INSTANCE = new StopGameType();

    protected StopGameType()
    {
    }

    @Override
    public String getDescription()
    {
        return "Similar to Go, Stop employs a few small variations for faster games and less need to 'fill-in' space.";
    }

    @Override
    public String getName()
    {
        return "Stop";
    }

    @Override
    public String getID()
    {
        return "Stop";
    }

    @Override
    public String getIcon()
    {
        return "/media/go.png";
    }

    @Override
    public String getLinks()
    {
        return "<a href='http://en.wikipedia.org/wiki/Go_%28game%29' target=\"_new\">Wikipedia</a>";
    }

    @Override
    public GameState getNewGame(final com.sawdust.engine.view.config.GameConfig c, final SessionFactory sessionFactory)
    {
        return new StopGame(c)
        {
            @Override
            public GameSession getSession()
            {
                return sessionFactory.getSession();
            }
        };
    }
    
    @Override
    public List<GameType<?>> getTutorialSequence()
    {
        ArrayList<GameType<?>> arrayList = new ArrayList<GameType<?>>();
        arrayList.add(com.sawdust.games.stop.tutorial.basic.Stop101_GameType.INSTANCE);
        return arrayList;
    }
};
