package com.sawdust.engine.game.go;

import java.util.ArrayList;
import java.util.List;

import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.game.BaseGame;
import com.sawdust.engine.game.Game;
import com.sawdust.engine.game.GameType;
import com.sawdust.engine.game.SessionFactory;
import com.sawdust.engine.game.blackjack.BlackjackGame;
import com.sawdust.engine.game.stop.StopGame;
import com.sawdust.engine.service.data.Account;
import com.sawdust.engine.service.data.GameSession;

public class GoGameType extends com.sawdust.engine.game.GameType<GoGame>
{
    public static final GoGameType INSTANCE = new GoGameType();

    protected GoGameType()
    {
    }

    @Override
    public String getDescription()
    {
        return "The game of Go is played with white and black stones, " 
           + "which are places on the grid so as to surround the opponent. "
           + "Detailed information can be found on <a href='http://en.wikipedia.org/wiki/Go_%28game%29' target=\"_new\">Wikipedia</a>. "
           + "This particular version is currently limited to a 9x9 board.";
    }

    @Override
    public String getName()
    {
        return "Go";
    }

    @Override
    public String getID()
    {
        return "Go";
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
        return new GoGame(c)
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
        arrayList.add(com.sawdust.engine.game.go.tutorial.basic.Go101_GameType.INSTANCE);
        return arrayList;
    }
};
