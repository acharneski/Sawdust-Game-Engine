package com.sawdust.engine.game.wordHunt;

import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.common.config.PropertyConfig;
import com.sawdust.engine.common.config.PropertyConfig.DetailLevel;
import com.sawdust.engine.common.config.PropertyConfig.PropertyType;
import com.sawdust.engine.game.BaseGame;
import com.sawdust.engine.game.GameType;
import com.sawdust.engine.game.SessionFactory;
import com.sawdust.engine.game.blackjack.BlackjackGame;
import com.sawdust.engine.service.data.Account;
import com.sawdust.engine.service.data.GameSession;

public class WordHuntGameType extends com.sawdust.engine.game.GameType<WordHuntGame>
{
    public static final WordHuntGameType INSTANCE = new WordHuntGameType();

    private WordHuntGameType()
    {
    }

    @Override
    public String getDescription()
    {
        return "In this game, a random matrix of letters is given to one or more players " + "who then have 5 minutes to spell as many words as possible. "
                + "Longer words are worth a higher score; " + "the player with the highest score at the end wins!";
    }

    @Override
    public String getName()
    {
        return "WordHunt";
    }

    @Override
    public String getID()
    {
        return "WordHunt";
    }

    @Override
    public GameConfig getPrototypeConfig(final Account account)
    {
        final GameConfig gameTemplate = super.getPrototypeConfig(account);

        final PropertyConfig value = new PropertyConfig(PropertyType.Number, GameConfig.NUM_PLAYERS);
        value.description = "Number of players to play (including you)";
        value.defaultValue = "1";
        value.levelOfDetail = DetailLevel.Runtime;
        gameTemplate.addProperty(value);

        return gameTemplate;
    }

    @Override
    public String getLinks()
    {
        return "<a href=\"http://www.wiktionary.com/\" target=\"_new\">Wiktionary</a>";
    }

    @Override
    public BaseGame createNewGame(final com.sawdust.engine.common.config.GameConfig c, final SessionFactory sessionFactory)
    {
        return new WordHuntGame(c)
        {
            @Override
            public GameSession getSession()
            {
                return sessionFactory.getSession();
            }
        };
    }

};
