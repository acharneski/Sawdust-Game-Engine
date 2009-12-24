package com.sawdust.server.datastore;



import com.sawdust.engine.common.AccessToken;
import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.game.Game;
import com.sawdust.engine.game.GameType;
import com.sawdust.engine.game.LanguageProvider;
import com.sawdust.engine.game.SessionFactory;
import com.sawdust.engine.game.wordHunt.WordHuntGame;
import com.sawdust.engine.game.wordHunt.WordHuntGameType;
import com.sawdust.server.appengine.EnglishLanguageProvider;

public class Games
{
    static final LanguageProvider ENGLISH = new EnglishLanguageProvider();

    public static Game NewGame(final GameType<?> gameToCreate, final GameConfig config, final com.sawdust.engine.service.data.GameSession session,
            final AccessToken access2)
    {
        final String id = session.getId();
        SessionFactory sessionFactory = new MySessionFactory(id);
        Game newGame = gameToCreate.createNewGame(config, sessionFactory);
        if (WordHuntGameType.INSTANCE.equals(gameToCreate))
        {
            ((WordHuntGame)newGame).setHttpInterface(new SDWebCacheWrapper());
            ((WordHuntGame)newGame).setLanguage(Games.ENGLISH);
        }
        return newGame;
    }
}
