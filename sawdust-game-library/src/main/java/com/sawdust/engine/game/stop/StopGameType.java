package com.sawdust.engine.game.stop;

import java.util.ArrayList;
import java.util.List;

import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.game.BaseGame;
import com.sawdust.engine.game.Game;
import com.sawdust.engine.game.GameType;
import com.sawdust.engine.game.SessionFactory;
import com.sawdust.engine.game.blackjack.BlackjackGame;
import com.sawdust.engine.service.data.Account;
import com.sawdust.engine.service.data.GameSession;

public class StopGameType extends com.sawdust.engine.game.GameType<StopGame>
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
    public Game createNewGame(final com.sawdust.engine.common.config.GameConfig c, final SessionFactory sessionFactory)
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
        arrayList.add(com.sawdust.engine.game.stop.tutorial.basic.Stop101_GameType.INSTANCE);
        return arrayList;
    }
};
