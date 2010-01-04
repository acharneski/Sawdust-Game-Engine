package com.sawdust.test.game;

import java.util.ArrayList;
import java.util.Date;

import junit.framework.TestCase;

import org.junit.Test;

import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.game.players.ActivityEvent;
import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.game.players.Player;
import com.sawdust.engine.game.wordHunt.WordHuntGame;
import com.sawdust.engine.service.HttpInterface;
import com.sawdust.engine.service.HttpResponse;
import com.sawdust.engine.service.LanguageProvider;
import com.sawdust.engine.service.data.Account;
import com.sawdust.engine.service.data.GameSession;
import com.sawdust.test.Util;
import com.sawdust.test.mock.MockGameSession;
import com.sawdust.test.mock.MockSessionToken;

public class WordHuntTests extends TestCase
{

    @Test(timeout = 10000)
    public void testGameLose() throws Exception
    {
        System.out.println("\ntestGameWin()\n");
        WordHuntGame blackjackGame = getGame();
        GameSession session = blackjackGame.getSession();
        final MockSessionToken access1 = new MockSessionToken("test1", session);
        Player player1 = new Player(access1.getUserId(), false)
        {
            @Override
            public Account loadAccount()
            {
                return access1.doLoadAccount();
            }

            @Override
            public void logActivity(ActivityEvent event)
            {
                // TODO Auto-generated method stub
                
            }
        };
        Date now = new Date(0);

        Util.assertEqual(access1.doLoadAccount().getBalance(), 10);

        session.addPlayer(player1);
        blackjackGame.addPlayer(player1);
        startGame(session, player1);

        now = Util.printNewMessages(blackjackGame, now);
        Util.assertEqual(access1.doLoadAccount().getBalance(), 9);
    }

    private WordHuntGame getGame()
    {
        WordHuntGame game = new WordHuntGame(new GameConfig())
        {
            MockGameSession session;

            @Override
            public GameSession getSession()
            {
                if(null == session) 
                {
                    session = new MockGameSession(this);
                }                    
                return session;
            }
        };
        ((WordHuntGame)game).setHttpInterface(new HttpInterface()
        {
            @Override
            public HttpResponse getURL(String urlString)
            {
                return new HttpResponse("Helter Skelter", 200);
            }
        });
        ((WordHuntGame)game).setLanguage(new LanguageProvider()
        {
            
            @Override
            public boolean verifyWord(String word, HttpInterface theInternet)
            {
                return true;
            }
            
            @Override
            public ArrayList<String> tokens(String commandText)
            {
                ArrayList<String> l = new ArrayList<String>();
                for(char c : commandText.toCharArray())
                {
                    l.add(Character.toString(c));
                }
                return l;
            }
            
            @Override
            public String normalizeString(String b)
            {
                return b;
            }
            
            @Override
            public ArrayList<String> getWordCharacterSet()
            {
                ArrayList<String> l = new ArrayList<String>();
                l.add("a"); l.add("h"); l.add("o"); l.add("v");
                l.add("b"); l.add("i"); l.add("p"); l.add("w");
                l.add("c"); l.add("j"); l.add("q"); l.add("x");
                l.add("d"); l.add("k"); l.add("r"); l.add("y");
                l.add("e"); l.add("l"); l.add("s"); l.add("z");
                l.add("f"); l.add("m"); l.add("t"); 
                l.add("g"); l.add("n"); l.add("u"); 
                return l;
            }
            
            @Override
            public String getUrl(String urlString, HttpInterface theInternet)
            {
                return theInternet.getURL(urlString).getContent();
            }
            
            @Override
            public ArrayList<String> getDelimiterCharacterSet()
            {
                ArrayList<String> l = new ArrayList<String>();
                l.add(" "); l.add("."); l.add("?"); l.add("!");
                return l;
            }
        });
        return game;
    }

    private void startGame(GameSession session, Player player1) throws com.sawdust.engine.common.GameException
    {
        ArrayList<Participant> players = new ArrayList<Participant>();
        players.add(player1);
        session.doStart(players);
    }
}
