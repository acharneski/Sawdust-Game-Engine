package com.sawdust.gae.datastore;

import com.sawdust.engine.controller.LanguageProvider;
import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.GameType;
import com.sawdust.engine.model.SessionFactory;
import com.sawdust.engine.model.basetypes.GameState;
import com.sawdust.engine.view.AccessToken;
import com.sawdust.engine.view.config.GameConfig;
import com.sawdust.gae.logic.EnglishLanguageProvider;
import com.sawdust.games.wordHunt.WordHuntGame;
import com.sawdust.games.wordHunt.WordHuntGameType;

public class Games
{
    static final LanguageProvider ENGLISH = new EnglishLanguageProvider();

    public static GameState NewGame(final GameType<?> gameToCreate, final GameConfig config, final com.sawdust.engine.controller.entities.GameSession session,
            final AccessToken access2) throws GameException
    {
        final String id = session.getStringId();
        SessionFactory sessionFactory = new MySessionFactory(id);
        GameState newGame = gameToCreate.createNewGame(config, sessionFactory);
        if (WordHuntGameType.INSTANCE.equals(gameToCreate))
        {
            ((WordHuntGame)newGame).setHttpInterface(new SDWebCacheWrapper());
            ((WordHuntGame)newGame).setLanguage(Games.ENGLISH);
        }
        return newGame;
    }
}
