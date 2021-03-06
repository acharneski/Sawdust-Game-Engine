package com.sawdust.test.game;

import java.util.ArrayList;
import java.util.Date;

import junit.framework.TestCase;

import org.junit.Test;

import com.sawdust.engine.controller.entities.Account;
import com.sawdust.engine.controller.entities.GameSession;
import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.LoadedDeck;
import com.sawdust.engine.model.players.AccountFactory;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.players.Player;
import com.sawdust.engine.view.cards.Ranks;
import com.sawdust.engine.view.cards.Suits;
import com.sawdust.engine.view.config.GameConfig;
import com.sawdust.engine.view.game.ActivityEvent;
import com.sawdust.games.blackjack.BlackjackGame;
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
        Player player1 = new Player(access1.getUserId(), false, new AccountFactory()
        {
            
            @Override
            public Account getAccount()
            {
                return access1.doLoadAccount();
            }
        });

        Date now = new Date(0);

        deck.doAddCard(Ranks.Ace, Suits.Clubs);
        deck.doAddCard(Ranks.Ace, Suits.Clubs);
        deck.doAddCard(Ranks.Jack, Suits.Hearts);
        deck.doAddCard(Ranks.Five, Suits.Clubs);
        deck.doAddCard(Ranks.Four, Suits.Clubs);
        deck.doAddCard(Ranks.Two, Suits.Hearts);

        Util.assertEqual(access1.doLoadAccount().getBalance(), 10);

        session.addPlayer(player1);
        blackjackGame.doAddPlayer(player1);
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

    private void startGame(GameSession session, Player player1) throws com.sawdust.engine.view.GameException
    {
        ArrayList<Participant> players = new ArrayList<Participant>();
        players.add(player1);
        session.doStart();
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
        Player player1 = new Player(access1.getUserId(), false, new AccountFactory()
        {
            
            @Override
            public Account getAccount()
            {
                return access1.doLoadAccount();
            }
        });

        Date now = new Date(0);

        deck.doAddCard(Ranks.Five, Suits.Clubs);
        deck.doAddCard(Ranks.Ace, Suits.Clubs);
        deck.doAddCard(Ranks.King, Suits.Clubs);
        deck.doAddCard(Ranks.Jack, Suits.Hearts);
        deck.doAddCard(Ranks.Four, Suits.Clubs);
        deck.doAddCard(Ranks.Ace, Suits.Clubs);

        Util.assertEqual(access1.doLoadAccount().getBalance(), 10);

        session.addPlayer(player1);
        blackjackGame.doAddPlayer(player1);
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
