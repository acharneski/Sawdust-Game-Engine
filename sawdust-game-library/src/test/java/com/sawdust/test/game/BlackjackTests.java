package com.sawdust.test.game;

import java.util.ArrayList;
import java.util.Date;

import junit.framework.TestCase;

import org.junit.Test;

import com.sawdust.engine.common.cards.Ranks;
import com.sawdust.engine.common.cards.Suits;
import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.game.LoadedDeck;
import com.sawdust.engine.game.blackjack.BlackjackGame;
import com.sawdust.engine.game.players.ActivityEvent;
import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.game.players.Player;
import com.sawdust.engine.service.data.Account;
import com.sawdust.engine.service.data.GameSession;
import com.sawdust.engine.service.debug.GameException;
import com.sawdust.test.Util;
import com.sawdust.test.mock.MockGameSession;
import com.sawdust.test.mock.MockSessionToken;

public class BlackjackTests extends TestCase
{

    @Test(timeout = 10000)
    public void testGameLose() throws Exception
    {
        System.out.println("\ntestGameWin()\n");
        BlackjackGame blackjackGame = getGame();
        GameSession session = blackjackGame.getSession();
        LoadedDeck deck = new LoadedDeck();
        blackjackGame.setDeck(deck);
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

        deck.addCard(Ranks.Ace, Suits.Clubs);
        deck.addCard(Ranks.Ace, Suits.Clubs);
        deck.addCard(Ranks.Jack, Suits.Hearts);
        deck.addCard(Ranks.Five, Suits.Clubs);
        deck.addCard(Ranks.Four, Suits.Clubs);
        deck.addCard(Ranks.Two, Suits.Hearts);

        Util.assertEqual(access1.doLoadAccount().getBalance(), 10);

        session.addPlayer(player1);
        blackjackGame.addPlayer(player1);
        startGame(session, player1);

        now = Util.printNewMessages(blackjackGame, now);
        Util.assertEqual(access1.doLoadAccount().getBalance(), 9);

        Util.testGuiCommand(session, player1, "Hit 0");
        now = Util.printNewMessages(blackjackGame, now);
        Util.testGuiCommand(session, player1, "Stay");
        now = Util.printNewMessages(blackjackGame, now);
        Util.assertMessageFound(blackjackGame, "You Lose.");
        Util.assertEqual(access1.doLoadAccount().getBalance(), 9);
    }

    private BlackjackGame getGame()
    {
        BlackjackGame blackjackGame = new BlackjackGame(new GameConfig())
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
        return blackjackGame;
    }

    private void startGame(GameSession session, Player player1) throws com.sawdust.engine.common.GameException
    {
        ArrayList<Participant> players = new ArrayList<Participant>();
        players.add(player1);
        session.doStart(players);
    }

    @Test(timeout = 10000)
    public void testGameWin() throws Exception
    {
        System.out.println("\ntestGameWin()\n");
        BlackjackGame blackjackGame = getGame();
        GameSession session = blackjackGame.getSession();
        LoadedDeck deck = new LoadedDeck();
        blackjackGame.setDeck(deck);
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

        deck.addCard(Ranks.Five, Suits.Clubs);
        deck.addCard(Ranks.Ace, Suits.Clubs);
        deck.addCard(Ranks.King, Suits.Clubs);
        deck.addCard(Ranks.Jack, Suits.Hearts);
        deck.addCard(Ranks.Four, Suits.Clubs);
        deck.addCard(Ranks.Ace, Suits.Clubs);

        Util.assertEqual(access1.doLoadAccount().getBalance(), 10);

        session.addPlayer(player1);
        blackjackGame.addPlayer(player1);
        startGame(session, player1);

        now = Util.printNewMessages(blackjackGame, now);
        Util.assertEqual(access1.doLoadAccount().getBalance(), 9);

        Util.testGuiCommand(session, player1, "Hit 0");
        now = Util.printNewMessages(blackjackGame, now);
        Util.testGuiCommand(session, player1, "Hit 0");
        now = Util.printNewMessages(blackjackGame, now);
        Util.testGuiCommand(session, player1, "Stay");
        now = Util.printNewMessages(blackjackGame, now);
        Util.assertMessageFound(blackjackGame, "You Win.");
        Util.assertEqual(access1.doLoadAccount().getBalance(), 11);
    }

}
