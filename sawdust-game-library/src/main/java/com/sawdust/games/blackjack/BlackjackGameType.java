package com.sawdust.games.blackjack;

import java.util.ArrayList;
import java.util.List;

import com.sawdust.engine.controller.entities.GameSession;
import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.GameType;
import com.sawdust.engine.model.SessionFactory;
import com.sawdust.engine.model.basetypes.GameState;

public class BlackjackGameType extends com.sawdust.engine.model.GameType<BlackjackGame>
{
    public static final BlackjackGameType INSTANCE = new BlackjackGameType();

    protected BlackjackGameType()
    {
    }

    @Override
    public String getDescription()
    {
        return "This is a simplified version of <b>blackjack</b>, " + "one player only, with only hit, stay, deal and of course "
                + "a variable bet. See the detailed game description and rules at "
                + "<a href=\"http://en.wikipedia.org/wiki/Blackjack\" target=\"_new\">Wikipedia</a>"
                + " or our <a href='/rules/Blackjack.jsp'>custom specification and implementation notes</a>.";
    }

    @Override
    public String getIcon()
    {
        return "/media/blackjack.png";
    }

    @Override
    public String getName()
    {
        return "BlackJack";
    }

    @Override
    public String getID()
    {
        return "BlackJack";
    }

    @Override
    public String getLinks()
    {
        return "<a href=\"http://en.wikipedia.org/wiki/Blackjack\" target=\"_new\">Wikipedia</a>";
    }

    @Override
    public GameState getNewGame(final com.sawdust.engine.view.config.GameConfig c, final SessionFactory sessionFactory) throws GameException
    {
        return new BlackjackGame(c)
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
        arrayList.add(com.sawdust.games.blackjack.tutorials.basic.GameType.INSTANCE);
        return arrayList;
    }
};
