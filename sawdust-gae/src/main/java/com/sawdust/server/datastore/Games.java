package com.sawdust.server.datastore;

import com.sawdust.engine.common.AccessToken;
import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.game.GameType;
import com.sawdust.engine.game.SessionFactory;
import com.sawdust.engine.game.basetypes.GameState;
import com.sawdust.engine.game.wordHunt.WordHuntGame;
import com.sawdust.engine.game.wordHunt.WordHuntGameType;
import com.sawdust.engine.service.LanguageProvider;
import com.sawdust.engine.service.debug.GameException;
import com.sawdust.server.appengine.EnglishLanguageProvider;

public class Games
{
    static final LanguageProvider ENGLISH = new EnglishLanguageProvider();

    public static GameState NewGame(final GameType<?> gameToCreate, final GameConfig config, final com.sawdust.engine.service.data.GameSession session,
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
