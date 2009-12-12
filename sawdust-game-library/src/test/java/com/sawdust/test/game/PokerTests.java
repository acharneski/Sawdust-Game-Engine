package com.sawdust.test.game;

import java.util.ArrayList;
import java.util.Date;

import junit.framework.TestCase;

import org.junit.Test;

import com.sawdust.engine.common.cards.Ranks;
import com.sawdust.engine.common.cards.Suits;
import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.game.LoadedDeck;
import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.game.players.Player;
import com.sawdust.engine.game.poker.PokerGame;
import com.sawdust.engine.service.data.Account;
import com.sawdust.engine.service.data.GameSession;
import com.sawdust.engine.service.debug.GameException;
import com.sawdust.test.Util;
import com.sawdust.test.mock.MockGameSession;
import com.sawdust.test.mock.MockSessionToken;

public class PokerTests extends TestCase
{

    private MockSessionToken access1;
    private MockSessionToken access2;
    private PokerGame game;
    private Player player2;
    private MockGameSession session;

    private Player Init(LoadedDeck deck) throws com.sawdust.engine.common.GameException
    {
        game = new PokerGame(new GameConfig())
        {
            public MockGameSession _session = null;

            @Override
            public GameSession getSession()
            {
                if(null == _session)
                {
                    _session = new MockGameSession(this);
                }
                return _session;
            }
        };
        session = (MockGameSession) game.getSession();
        game.setDeck(deck);
        access1 = new MockSessionToken("test1", session);
        final MockSessionToken accessF1 = access1;
        Player player1 = new Player(accessF1.getUserId(), false)
        {
            @Override
            public Account loadAccount()
            {
                return accessF1.loadAccount();
            }
        };
        access2 = new MockSessionToken("test2", session);
        final MockSessionToken accessF2 = access2;
        player2 = new Player(access2.getUserId(), false)
        {
            @Override
            public Account loadAccount()
            {
                return accessF2.loadAccount();
            }
        };
        session.addPlayer(player1);
        game.addMember(player1);
        session.addPlayer(player2);
        game.addMember(player2);
        session.start(new ArrayList<Participant>(session.getMembers()));
        return player1;
    }

    @Test(timeout = 10000)
    public void testFlush() throws Exception
    {
        System.out.println("\ntestFlush()\n");
        Date now = new Date(0);
        LoadedDeck deck = new LoadedDeck();

        // deck.add(Ranks.Three, Suits.Hearts);

        // Player 1 Cards
        deck.addCard(Ranks.King, Suits.Hearts); // 0
        deck.addCard(Ranks.Ace, Suits.Hearts); // 1
        deck.addCard(Ranks.Nine, Suits.Hearts); // 2
        deck.addCard(Ranks.Nine, Suits.Hearts); // 3
        deck.addCard(Ranks.Queen, Suits.Hearts); // 4

        // Player 2 Cards
        deck.addCard(Ranks.Five, Suits.Spades); // 0
        deck.addCard(Ranks.Ten, Suits.Hearts); // 1
        deck.addCard(Ranks.Eight, Suits.Clubs); // 2
        deck.addCard(Ranks.Seven, Suits.Diamonds); // 3
        deck.addCard(Ranks.Nine, Suits.Clubs); // 4

        Player player1 = Init(deck);

        Util.assertEqual(access1.loadAccount().getBalance(), 10);
        Util.assertEqual(access2.loadAccount().getBalance(), 10);

        System.out.println("Player 1: " + Util.printMyCards(session, player1));
        System.out.println("Player 2: " + Util.printMyCards(session, player2));

        Util.testGuiCommand(session, player2, "Call");
        now = Util.printNewMessages(game, now);
        Util.testGuiCommand(session, player1, "Raise 1");
        now = Util.printNewMessages(game, now);

        Util.assertEqual(access1.loadAccount().getBalance(), 8);
        Util.assertEqual(access2.loadAccount().getBalance(), 9);

        Util.testGuiCommand(session, player2, "Call");
        now = Util.printNewMessages(game, now);

        Util.assertEqual(access1.loadAccount().getBalance(), 8);
        Util.assertEqual(access2.loadAccount().getBalance(), 8);

        Util.testGuiCommand(session, player2, "Discard 1");
        now = Util.printNewMessages(game, now);
        Util.testGuiCommand(session, player2, "Discard 2");
        now = Util.printNewMessages(game, now);

        deck.addCard(Ranks.Seven, Suits.Clubs);
        deck.addCard(Ranks.Two, Suits.Clubs);
        Util.runCommand(session, player2, "Draw Cards");
        now = Util.printNewMessages(game, now);

        Util.testGuiCommand(session, player1, "Draw Cards");
        now = Util.printNewMessages(game, now);

        System.out.println("Player 1: " + Util.printMyCards(session, player1));
        System.out.println("Player 2: " + Util.printMyCards(session, player2));

        Util.assertMessageFound(game, "test1 won with Flush of Hearts");
        Util.assertEqual(access1.loadAccount().getBalance(), 12);
        Util.assertEqual(access2.loadAccount().getBalance(), 8);
    }

    @Test(timeout = 10000)
    public void testFourOfKind() throws Exception
    {
        System.out.println("\ntestFourOfKind()\n");
        Date now = new Date(0);
        LoadedDeck deck = new LoadedDeck();

        deck.addCard(Ranks.Three, Suits.Hearts);

        // Player 1 Cards
        deck.addCard(Ranks.Nine, Suits.Clubs); // 0
        deck.addCard(Ranks.Nine, Suits.Spades); // 1
        deck.addCard(Ranks.Nine, Suits.Hearts); // 2
        deck.addCard(Ranks.Nine, Suits.Diamonds); // 3
        deck.addCard(Ranks.Queen, Suits.Hearts); // 4

        // Player 2 Cards
        deck.addCard(Ranks.Five, Suits.Spades); // 0
        deck.addCard(Ranks.Ten, Suits.Hearts); // 1
        deck.addCard(Ranks.Eight, Suits.Clubs); // 2
        deck.addCard(Ranks.Seven, Suits.Diamonds); // 3
        deck.addCard(Ranks.Six, Suits.Clubs); // 4

        Player player1 = Init(deck);

        Util.assertEqual(access1.loadAccount().getBalance(), 10);
        Util.assertEqual(access2.loadAccount().getBalance(), 10);

        Util.testGuiCommand(session, player2, "Call");
        now = Util.printNewMessages(game, now);
        Util.testGuiCommand(session, player1, "Raise 1");
        now = Util.printNewMessages(game, now);

        Util.assertEqual(access1.loadAccount().getBalance(), 8);
        Util.assertEqual(access2.loadAccount().getBalance(), 9);

        Util.testGuiCommand(session, player2, "Call");
        now = Util.printNewMessages(game, now);

        Util.assertEqual(access1.loadAccount().getBalance(), 8);
        Util.assertEqual(access2.loadAccount().getBalance(), 8);

        Util.testGuiCommand(session, player2, "Discard 1");
        now = Util.printNewMessages(game, now);
        Util.testGuiCommand(session, player2, "Discard 2");
        now = Util.printNewMessages(game, now);

        deck.addCard(Ranks.Seven, Suits.Clubs);
        deck.addCard(Ranks.Two, Suits.Clubs);
        Util.runCommand(session, player2, "Draw Cards");
        now = Util.printNewMessages(game, now);

        Util.testGuiCommand(session, player1, "Draw Cards");
        now = Util.printNewMessages(game, now);

        System.out.println("Player 1: " + Util.printMyCards(session, player1));
        System.out.println("Player 2: " + Util.printMyCards(session, player2));

        now = Util.printNewMessages(game, now);
        Util.assertMessageFound(game, "test1 won with Four of a kind: Nine");
        Util.assertEqual(access1.loadAccount().getBalance(), 12);
        Util.assertEqual(access2.loadAccount().getBalance(), 8);
    }

    @Test(timeout = 10000)
    public void testFullHouse() throws Exception
    {
        System.out.println("\ntestFullHouse()\n");
        Date now = new Date(0);
        LoadedDeck deck = new LoadedDeck();

        // deck.add(Ranks.Three, Suits.Hearts);

        // Player 1 Cards
        deck.addCard(Ranks.Queen, Suits.Diamonds); // 0
        deck.addCard(Ranks.Nine, Suits.Hearts); // 1
        deck.addCard(Ranks.Nine, Suits.Hearts); // 2
        deck.addCard(Ranks.Nine, Suits.Diamonds); // 3
        deck.addCard(Ranks.Queen, Suits.Hearts); // 4

        // Player 2 Cards
        deck.addCard(Ranks.Five, Suits.Spades); // 0
        deck.addCard(Ranks.Ten, Suits.Hearts); // 1
        deck.addCard(Ranks.Eight, Suits.Clubs); // 2
        deck.addCard(Ranks.Seven, Suits.Diamonds); // 3
        deck.addCard(Ranks.Nine, Suits.Clubs); // 4

        Player player1 = Init(deck);

        Util.assertEqual(access1.loadAccount().getBalance(), 10);
        Util.assertEqual(access2.loadAccount().getBalance(), 10);

        Util.testGuiCommand(session, player2, "Call");
        now = Util.printNewMessages(game, now);
        Util.testGuiCommand(session, player1, "Raise 1");
        now = Util.printNewMessages(game, now);

        Util.assertEqual(access1.loadAccount().getBalance(), 8);
        Util.assertEqual(access2.loadAccount().getBalance(), 9);

        Util.testGuiCommand(session, player2, "Call");
        now = Util.printNewMessages(game, now);

        Util.assertEqual(access1.loadAccount().getBalance(), 8);
        Util.assertEqual(access2.loadAccount().getBalance(), 8);

        Util.testGuiCommand(session, player2, "Discard 1");
        now = Util.printNewMessages(game, now);
        Util.testGuiCommand(session, player2, "Discard 2");
        now = Util.printNewMessages(game, now);

        deck.addCard(Ranks.Seven, Suits.Clubs);
        deck.addCard(Ranks.Two, Suits.Clubs);
        Util.runCommand(session, player2, "Draw Cards");
        now = Util.printNewMessages(game, now);

        Util.testGuiCommand(session, player1, "Draw Cards");
        now = Util.printNewMessages(game, now);

        System.out.println("Player 1: " + Util.printMyCards(session, player1));
        System.out.println("Player 2: " + Util.printMyCards(session, player2));

        now = Util.printNewMessages(game, now);
        Util.assertMessageFound(game, "test1 won with Full House: Three of a kind: Nine and Pair of Queen");
        Util.assertEqual(access1.loadAccount().getBalance(), 12);
        Util.assertEqual(access2.loadAccount().getBalance(), 8);
    }

    @Test(timeout = 10000)
    public void testGameWin() throws Exception
    {
        System.out.println("\ntestGameWin()\n");
        Date now = new Date(0);
        LoadedDeck deck = new LoadedDeck();

        // deck.add(Ranks.Three, Suits.Hearts);

        // Player 1 Cards
        deck.addCard(Ranks.King, Suits.Hearts); // 0
        deck.addCard(Ranks.Ace, Suits.Hearts); // 1
        deck.addCard(Ranks.Nine, Suits.Hearts); // 2
        deck.addCard(Ranks.Nine, Suits.Diamonds); // 3
        deck.addCard(Ranks.Queen, Suits.Hearts); // 4

        // Player 2 Cards
        deck.addCard(Ranks.Five, Suits.Spades); // 0
        deck.addCard(Ranks.Ten, Suits.Hearts); // 1
        deck.addCard(Ranks.Eight, Suits.Clubs); // 2
        deck.addCard(Ranks.Seven, Suits.Diamonds); // 3
        deck.addCard(Ranks.Nine, Suits.Clubs); // 4

        Player player1 = Init(deck);

        Util.assertEqual(access1.loadAccount().getBalance(), 10);
        Util.assertEqual(access2.loadAccount().getBalance(), 10);

        Util.testGuiCommand(session, player2, "Raise 1");
        now = Util.printNewMessages(game, now);
        Util.testGuiCommand(session, player1, "Raise 5");
        now = Util.printNewMessages(game, now);

        Util.assertEqual(access1.loadAccount().getBalance(), 3);
        Util.assertEqual(access2.loadAccount().getBalance(), 8);

        Util.testGuiCommand(session, player2, "Call");
        now = Util.printNewMessages(game, now);

        Util.assertEqual(access1.loadAccount().getBalance(), 3);
        Util.assertEqual(access2.loadAccount().getBalance(), 3);

        Util.testGuiCommand(session, player2, "Discard 1");
        now = Util.printNewMessages(game, now);
        Util.testGuiCommand(session, player2, "Discard 2");
        now = Util.printNewMessages(game, now);
        Util.testGuiCommand(session, player1, "Discard 1");
        now = Util.printNewMessages(game, now);
        Util.testGuiCommand(session, player1, "Discard 2");
        now = Util.printNewMessages(game, now);

        deck.addCard(Ranks.Seven, Suits.Clubs);
        deck.addCard(Ranks.Two, Suits.Clubs);
        Util.runCommand(session, player2, "Draw Cards");
        now = Util.printNewMessages(game, now);

        deck.addCard(Ranks.Queen, Suits.Clubs);
        deck.addCard(Ranks.King, Suits.Diamonds);
        Util.testGuiCommand(session, player1, "Draw Cards");
        now = Util.printNewMessages(game, now);

        System.out.println("Player 1: " + Util.printMyCards(session, player1));
        System.out.println("Player 2: " + Util.printMyCards(session, player2));

        Util.assertMessageFound(game, "test1 won with Pair of Nine");
        Util.assertEqual(access1.loadAccount().getBalance(), 17);
        Util.assertEqual(access2.loadAccount().getBalance(), 3);
    }

    @Test(timeout = 10000)
    public void testStraight() throws Exception
    {
        System.out.println("\ntestStraight()\n");
        Date now = new Date(0);
        LoadedDeck deck = new LoadedDeck();

        // deck.add(Ranks.Three, Suits.Hearts);

        // Player 1 Cards
        deck.addCard(Ranks.King, Suits.Hearts); // 0
        deck.addCard(Ranks.Ace, Suits.Hearts); // 1
        deck.addCard(Ranks.Jack, Suits.Hearts); // 2
        deck.addCard(Ranks.Ten, Suits.Diamonds); // 3
        deck.addCard(Ranks.Queen, Suits.Hearts); // 4

        // Player 2 Cards
        deck.addCard(Ranks.Five, Suits.Spades); // 0
        deck.addCard(Ranks.Ten, Suits.Hearts); // 1
        deck.addCard(Ranks.Eight, Suits.Clubs); // 2
        deck.addCard(Ranks.Seven, Suits.Diamonds); // 3
        deck.addCard(Ranks.Nine, Suits.Clubs); // 4

        Player player1 = Init(deck);

        Util.assertEqual(access1.loadAccount().getBalance(), 10);
        Util.assertEqual(access2.loadAccount().getBalance(), 10);

        Util.testGuiCommand(session, player2, "Call");
        now = Util.printNewMessages(game, now);
        Util.testGuiCommand(session, player1, "Raise 1");
        now = Util.printNewMessages(game, now);

        Util.assertEqual(access1.loadAccount().getBalance(), 8);
        Util.assertEqual(access2.loadAccount().getBalance(), 9);

        Util.testGuiCommand(session, player2, "Call");
        now = Util.printNewMessages(game, now);

        Util.assertEqual(access1.loadAccount().getBalance(), 8);
        Util.assertEqual(access2.loadAccount().getBalance(), 8);

        Util.testGuiCommand(session, player2, "Discard 1");
        now = Util.printNewMessages(game, now);
        Util.testGuiCommand(session, player2, "Discard 2");
        now = Util.printNewMessages(game, now);

        deck.addCard(Ranks.Seven, Suits.Clubs);
        deck.addCard(Ranks.Two, Suits.Clubs);
        Util.runCommand(session, player2, "Draw Cards");
        now = Util.printNewMessages(game, now);

        Util.testGuiCommand(session, player1, "Draw Cards");
        now = Util.printNewMessages(game, now);

        System.out.println("Player 1: " + Util.printMyCards(session, player1));
        System.out.println("Player 2: " + Util.printMyCards(session, player2));

        Util.assertMessageFound(game, "test1 won with Straight To Ace");
        Util.assertEqual(access1.loadAccount().getBalance(), 12);
        Util.assertEqual(access2.loadAccount().getBalance(), 8);
    }

    @Test(timeout = 10000)
    public void testStraightFlush() throws Exception
    {
        System.out.println("\ntestStraightFlush()\n");
        Date now = new Date(0);
        LoadedDeck deck = new LoadedDeck();

        // deck.add(Ranks.Three, Suits.Hearts);

        // Player 1 Cards
        deck.addCard(Ranks.King, Suits.Hearts); // 0
        deck.addCard(Ranks.Ace, Suits.Hearts); // 1
        deck.addCard(Ranks.Jack, Suits.Hearts); // 2
        deck.addCard(Ranks.Ten, Suits.Hearts); // 3
        deck.addCard(Ranks.Queen, Suits.Hearts); // 4

        // Player 2 Cards
        deck.addCard(Ranks.Five, Suits.Spades); // 0
        deck.addCard(Ranks.Ten, Suits.Hearts); // 1
        deck.addCard(Ranks.Eight, Suits.Clubs); // 2
        deck.addCard(Ranks.Seven, Suits.Diamonds); // 3
        deck.addCard(Ranks.Nine, Suits.Clubs); // 4

        Player player1 = Init(deck);

        Util.assertEqual(access1.loadAccount().getBalance(), 10);
        Util.assertEqual(access2.loadAccount().getBalance(), 10);

        Util.testGuiCommand(session, player2, "Call");
        now = Util.printNewMessages(game, now);
        Util.testGuiCommand(session, player1, "Raise 1");
        now = Util.printNewMessages(game, now);

        Util.assertEqual(access1.loadAccount().getBalance(), 8);
        Util.assertEqual(access2.loadAccount().getBalance(), 9);

        Util.testGuiCommand(session, player2, "Call");
        now = Util.printNewMessages(game, now);

        Util.assertEqual(access1.loadAccount().getBalance(), 8);
        Util.assertEqual(access2.loadAccount().getBalance(), 8);

        Util.testGuiCommand(session, player2, "Discard 1");
        now = Util.printNewMessages(game, now);
        Util.testGuiCommand(session, player2, "Discard 2");
        now = Util.printNewMessages(game, now);

        deck.addCard(Ranks.Seven, Suits.Clubs);
        deck.addCard(Ranks.Two, Suits.Clubs);
        Util.runCommand(session, player2, "Draw Cards");
        now = Util.printNewMessages(game, now);

        Util.testGuiCommand(session, player1, "Draw Cards");
        now = Util.printNewMessages(game, now);

        System.out.println("Player 1: " + Util.printMyCards(session, player1));
        System.out.println("Player 2: " + Util.printMyCards(session, player2));

        Util.assertMessageFound(game, "test1 won with Straight Flush To Ace of Hearts");
        Util.assertEqual(access1.loadAccount().getBalance(), 12);
        Util.assertEqual(access2.loadAccount().getBalance(), 8);
    }

    @Test(timeout = 10000)
    public void testThreeOfKind() throws Exception
    {
        System.out.println("\ntestThreeOfKind()\n");
        Date now = new Date(0);
        LoadedDeck deck = new LoadedDeck();

        deck.addCard(Ranks.Three, Suits.Hearts);

        // Player 1 Cards
        deck.addCard(Ranks.King, Suits.Hearts); // 0
        deck.addCard(Ranks.Nine, Suits.Spades); // 1
        deck.addCard(Ranks.Nine, Suits.Hearts); // 2
        deck.addCard(Ranks.Nine, Suits.Diamonds); // 3
        deck.addCard(Ranks.Queen, Suits.Hearts); // 4

        // Player 2 Cards
        deck.addCard(Ranks.Five, Suits.Spades); // 0
        deck.addCard(Ranks.Ten, Suits.Hearts); // 1
        deck.addCard(Ranks.Eight, Suits.Clubs); // 2
        deck.addCard(Ranks.Seven, Suits.Diamonds); // 3
        deck.addCard(Ranks.Nine, Suits.Clubs); // 4

        Player player1 = Init(deck);

        Util.assertEqual(access1.loadAccount().getBalance(), 10);
        Util.assertEqual(access2.loadAccount().getBalance(), 10);

        Util.testGuiCommand(session, player2, "Call");
        now = Util.printNewMessages(game, now);
        Util.testGuiCommand(session, player1, "Raise 1");
        now = Util.printNewMessages(game, now);

        Util.assertEqual(access1.loadAccount().getBalance(), 8);
        Util.assertEqual(access2.loadAccount().getBalance(), 9);

        Util.testGuiCommand(session, player2, "Call");
        now = Util.printNewMessages(game, now);

        Util.assertEqual(access1.loadAccount().getBalance(), 8);
        Util.assertEqual(access2.loadAccount().getBalance(), 8);

        Util.testGuiCommand(session, player2, "Discard 1");
        now = Util.printNewMessages(game, now);
        Util.testGuiCommand(session, player2, "Discard 2");
        now = Util.printNewMessages(game, now);

        deck.addCard(Ranks.Seven, Suits.Clubs);
        deck.addCard(Ranks.Two, Suits.Clubs);
        Util.runCommand(session, player2, "Draw Cards");
        now = Util.printNewMessages(game, now);

        Util.testGuiCommand(session, player1, "Draw Cards");
        now = Util.printNewMessages(game, now);

        System.out.println("Player 1: " + Util.printMyCards(session, player1));
        System.out.println("Player 2: " + Util.printMyCards(session, player2));

        Util.assertMessageFound(game, "test1 won with Three of a kind: Nine");
        Util.assertEqual(access1.loadAccount().getBalance(), 12);
        Util.assertEqual(access2.loadAccount().getBalance(), 8);
    }

    @Test(timeout = 10000)
    public void testTwoPair() throws Exception
    {
        System.out.println("\ntestTwoPair()\n");
        Date now = new Date(0);
        LoadedDeck deck = new LoadedDeck();

        deck.addCard(Ranks.Three, Suits.Hearts);

        // Player 1 Cards
        deck.addCard(Ranks.King, Suits.Hearts); // 0
        deck.addCard(Ranks.King, Suits.Hearts); // 1
        deck.addCard(Ranks.Nine, Suits.Hearts); // 2
        deck.addCard(Ranks.Nine, Suits.Diamonds); // 3
        deck.addCard(Ranks.Queen, Suits.Hearts); // 4

        // Player 2 Cards
        deck.addCard(Ranks.Five, Suits.Spades); // 0
        deck.addCard(Ranks.Ten, Suits.Hearts); // 1
        deck.addCard(Ranks.Eight, Suits.Clubs); // 2
        deck.addCard(Ranks.Seven, Suits.Diamonds); // 3
        deck.addCard(Ranks.Nine, Suits.Clubs); // 4

        Player player1 = Init(deck);

        Util.assertEqual(access1.loadAccount().getBalance(), 10);
        Util.assertEqual(access2.loadAccount().getBalance(), 10);

        Util.testGuiCommand(session, player2, "Call");
        now = Util.printNewMessages(game, now);
        Util.testGuiCommand(session, player1, "Raise 1");
        now = Util.printNewMessages(game, now);

        Util.assertEqual(access1.loadAccount().getBalance(), 8);
        Util.assertEqual(access2.loadAccount().getBalance(), 9);

        Util.testGuiCommand(session, player2, "Call");
        now = Util.printNewMessages(game, now);

        Util.assertEqual(access1.loadAccount().getBalance(), 8);
        Util.assertEqual(access2.loadAccount().getBalance(), 8);

        Util.testGuiCommand(session, player2, "Discard 1");
        now = Util.printNewMessages(game, now);
        Util.testGuiCommand(session, player2, "Discard 2");
        now = Util.printNewMessages(game, now);

        deck.addCard(Ranks.Seven, Suits.Clubs);
        deck.addCard(Ranks.Two, Suits.Clubs);
        Util.runCommand(session, player2, "Draw Cards");
        now = Util.printNewMessages(game, now);

        Util.testGuiCommand(session, player1, "Draw Cards");
        now = Util.printNewMessages(game, now);

        System.out.println("Player 1: " + Util.printMyCards(session, player1));
        System.out.println("Player 2: " + Util.printMyCards(session, player2));

        Util.assertMessageFound(game, "test1 won with Two Pair: Pair of King and Pair of Nine");
        Util.assertEqual(access1.loadAccount().getBalance(), 12);
        Util.assertEqual(access2.loadAccount().getBalance(), 8);
    }
}
