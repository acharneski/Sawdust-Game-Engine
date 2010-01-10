package com.sawdust.games.poker;

import com.sawdust.engine.controller.entities.Account;
import com.sawdust.engine.controller.entities.GameSession;
import com.sawdust.engine.model.GameType;
import com.sawdust.engine.model.SessionFactory;
import com.sawdust.engine.model.basetypes.BaseGame;
import com.sawdust.engine.view.config.GameConfig;
import com.sawdust.engine.view.config.PropertyConfig;
import com.sawdust.engine.view.config.PropertyConfig.DetailLevel;
import com.sawdust.engine.view.config.PropertyConfig.PropertyType;
import com.sawdust.games.blackjack.BlackjackGame;

public class PokerGameType extends com.sawdust.engine.model.GameType<PokerGame>
{
    public static final PokerGameType INSTANCE = new PokerGameType();

    private PokerGameType()
    {
    }

    @Override
    public String getDescription()
    {
        return "This is a simplified version of the 5-card draw variation of poker, " + "in which you can draw up to 5 cards. Detailed information can be "
                + "found in our <a href='/rules/Poker.jsp'>implementation notes</a> "
                + "or on <a href='http://en.wikipedia.org/wiki/Poker' target=\\\"_new\\\">Wikipedia</a>.";
    }

    @Override
    public String getName()
    {
        return "Poker";
    }

    @Override
    public String getID()
    {
        return "Poker";
    }

    @Override
    public GameConfig getPrototypeConfig(final Account account)
    {
        final GameConfig gameTemplate = super.getPrototypeConfig(account);

        final PropertyConfig value = new PropertyConfig(PropertyType.Number, GameConfig.NUM_PLAYERS);
        value.description = "Number of players to play poker (including you)";
        value.levelOfDetail = DetailLevel.Runtime;
        value.defaultValue = "2";
        gameTemplate.addProperty(value);

        return gameTemplate;
    }

    @Override
    public String getLinks()
    {
        return "<a href='http://en.wikipedia.org/wiki/Poker' target=\\\"_new\\\">Wikipedia</a>";
    }

    @Override
    public BaseGame createNewGame(final com.sawdust.engine.view.config.GameConfig c, final SessionFactory sessionFactory)
    {
        return new PokerGame(c)
        {
            @Override
            public GameSession getSession()
            {
                return sessionFactory.getSession();
            }
        };
    }

};
