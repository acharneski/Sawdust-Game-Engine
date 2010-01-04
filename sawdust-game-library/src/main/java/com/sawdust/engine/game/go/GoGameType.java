package com.sawdust.engine.game.go;

import java.util.ArrayList;
import java.util.List;

import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.game.GameType;
import com.sawdust.engine.game.SessionFactory;
import com.sawdust.engine.game.basetypes.BaseGame;
import com.sawdust.engine.game.basetypes.GameState;
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
        return "Go (碁) is a board game for two players who alternately place black and white stones  on a grid. The object of the game is to control the board  by surrounding territory and stones of the opposing color.";
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
    public GameState createNewGame(final com.sawdust.engine.common.config.GameConfig c, final SessionFactory sessionFactory)
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
