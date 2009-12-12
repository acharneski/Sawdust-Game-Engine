package com.sawdust.server.datastore;

import java.net.URLEncoder;

import com.google.appengine.api.datastore.Key;
import com.sawdust.engine.common.AccessToken;
import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.game.BaseGame;
import com.sawdust.engine.game.Game;
import com.sawdust.engine.game.GameType;
import com.sawdust.engine.game.SessionFactory;
import com.sawdust.engine.game.blackjack.BlackjackGame;
import com.sawdust.engine.game.euchre.EuchreGame;
import com.sawdust.engine.game.poker.PokerGame;
import com.sawdust.engine.game.stop.StopGame;
import com.sawdust.engine.game.stop.StopGameType;
import com.sawdust.engine.game.wordHunt.LanguageProvider;
import com.sawdust.engine.game.wordHunt.WordHuntGame;
import com.sawdust.engine.game.wordHunt.WordHuntGameType;
import com.sawdust.server.datastore.entities.GameSession;
import com.sawdust.server.datastore.entities.SDWebCache;
import com.sawdust.server.logic.GameTypes;

public class Games
{
    static final LanguageProvider ENGLISH = new LanguageProvider()
    {
        @Override
        public String getUrl(String urlString)
        {
            final SDWebCache word2 = SDWebCache.getURL(urlString);
            if (null == word2) return null;
            return word2.getContent();
        }
        
        @Override
        public boolean verifyWord(String word)
        {
            final String datasource = "http://en.wiktionary.org/wiki/";
            final String urlString = datasource + URLEncoder.encode(word.toLowerCase());
            final SDWebCache word2 = SDWebCache.getURL(urlString);
            if (null == word2) return false;
            return true;
        }
    };

    public static Game NewGame(final GameType<?> gameToCreate, final GameConfig config, final com.sawdust.engine.service.data.GameSession session,
            final AccessToken access2)
    {
        final String id = session.getId();
        SessionFactory sessionFactory = new SessionFactory()
        {
            @Override
            public com.sawdust.engine.service.data.GameSession getSession()
            {
                return GameSession.load(id, null);
            }
        };
        Game newGame = gameToCreate.createNewGame(config, sessionFactory);
        if (WordHuntGameType.INSTANCE.equals(gameToCreate))
        {
            ((WordHuntGame)newGame).setLanguage(ENGLISH);
        }
        return newGame;
    }
}
