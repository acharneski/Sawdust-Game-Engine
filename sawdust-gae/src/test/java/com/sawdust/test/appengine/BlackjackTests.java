package com.sawdust.test.appengine;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import junit.framework.TestCase;

import org.junit.Test;

import com.google.appengine.tools.development.ApiProxyLocalImpl;
import com.google.apphosting.api.ApiProxy;
import com.sawdust.engine.common.AccessToken;
import com.sawdust.engine.common.cards.Ranks;
import com.sawdust.engine.common.cards.Suits;
import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.game.GameType;
import com.sawdust.engine.game.LoadedDeck;
import com.sawdust.engine.game.blackjack.BlackjackGame;
import com.sawdust.engine.game.blackjack.BlackjackGameType;
import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.game.players.Player;
import com.sawdust.engine.service.data.Account;
import com.sawdust.engine.service.debug.GameException;
import com.sawdust.test.Util;
import com.sawdust.server.datastore.DataStore;
import com.sawdust.server.datastore.Games;
import com.sawdust.server.datastore.entities.GameSession;
import com.sawdust.server.logic.GameTypes;
import com.sawdust.server.logic.SessionToken;
import com.sawdust.server.logic.User;
import com.sawdust.server.logic.User.UserTypes;

public class BlackjackTests extends TestCase
{

    @Test(timeout = 10000)

    public void testGameLose() throws Exception
    {
        DataStore.Clear();
        ApiProxy.setEnvironmentForCurrentThread(new MockEnvironment());
        ApiProxy.setDelegate(new ApiProxyLocalImpl(new File("target/testData/empty")){});
        
        System.out.println("\ntestGameWin()\n");
        LoadedDeck deck = new LoadedDeck();
        String userId = "test1";
        AccessToken accessData = new AccessToken(userId);
        User user = new User(UserTypes.Member, userId, null);
        final SessionToken access1 = new SessionToken(accessData, user);
        com.sawdust.server.datastore.entities.Account account = access1.doLoadAccount();
        com.sawdust.engine.service.data.GameSession session = new GameSession(account);
        BlackjackGame blackjackGame = getGame(session, accessData, account);
        blackjackGame.saveState();
        blackjackGame.setDeck(deck);
        Player player1 = account.getPlayer();

        Date now = new Date(0);

        deck.addCard(Ranks.Ace, Suits.Clubs);
        deck.addCard(Ranks.Ace, Suits.Clubs);
        deck.addCard(Ranks.Jack, Suits.Hearts);
        deck.addCard(Ranks.Five, Suits.Clubs);
        deck.addCard(Ranks.Four, Suits.Clubs);
        deck.addCard(Ranks.Two, Suits.Hearts);

        Util.assertEqual(account.getBalance(), 10);

        session.addPlayer(player1);
        //blackjackGame.addMember(player1);
        startGame(session, player1);

        now = Util.printNewMessages(blackjackGame, now);
        Util.assertEqual(account.getBalance(), 10);

        Util.testGuiCommand(session, player1, "Hit Me");
        now = Util.printNewMessages(blackjackGame, now);
        Util.testGuiCommand(session, player1, "Stay");
        now = Util.printNewMessages(blackjackGame, now);
        Util.assertMessageFound(blackjackGame, "You Lose.");
        Util.assertEqual(account.getBalance(), 10);
        DataStore.Save();
    }

    private BlackjackGame getGame(com.sawdust.engine.service.data.GameSession session, AccessToken access, Account account) throws GameException
    {
        GameConfig prototypeConfig = BlackjackGameType.INSTANCE.getPrototypeConfig(account);
        return (BlackjackGame) Games.NewGame(BlackjackGameType.INSTANCE, prototypeConfig, session, access);
    }

    private void startGame(com.sawdust.engine.service.data.GameSession session, Player player1) throws com.sawdust.engine.common.GameException
    {
        ArrayList<Participant> players = new ArrayList<Participant>();
        players.add(player1);
        session.doStart(players);
    }

    @Test(timeout = 10000)
    public void testGameWin() throws Exception
    {
        DataStore.Clear();
        ApiProxy.setEnvironmentForCurrentThread(new MockEnvironment());
        ApiProxy.setDelegate(new ApiProxyLocalImpl(new File("test/empty")){});
        
        System.out.println("\ntestGameWin()\n");
        LoadedDeck deck = new LoadedDeck();
        String userId = "test1";
        AccessToken accessData = new AccessToken(userId);
        User user = new User(UserTypes.Member, userId, null);
        final SessionToken access1 = new SessionToken(accessData, user);
        com.sawdust.server.datastore.entities.Account account = access1.doLoadAccount();
        com.sawdust.engine.service.data.GameSession session = new GameSession(account);
        BlackjackGame blackjackGame = getGame(session, accessData, account);
        blackjackGame.saveState();
        blackjackGame.setDeck(deck);
        Player player1 = account.getPlayer();

        Date now = new Date(0);

        deck.addCard(Ranks.Ace, Suits.Clubs);
        deck.addCard(Ranks.Ace, Suits.Clubs);
        deck.addCard(Ranks.King, Suits.Clubs);
        deck.addCard(Ranks.Jack, Suits.Hearts);
        deck.addCard(Ranks.Five, Suits.Clubs);
        deck.addCard(Ranks.Four, Suits.Clubs);

        Util.assertEqual(access1.doLoadAccount().getBalance(), 10);

        session.addPlayer(player1);
        //blackjackGame.addMember(player1);
        startGame(session, player1);

        now = Util.printNewMessages(blackjackGame, now);
        Util.assertEqual(access1.doLoadAccount().getBalance(), 10);

        Util.testGuiCommand(session, player1, "Hit Me");
        now = Util.printNewMessages(blackjackGame, now);
        Util.testGuiCommand(session, player1, "Hit Me");
        now = Util.printNewMessages(blackjackGame, now);
        Util.testGuiCommand(session, player1, "Stay");
        now = Util.printNewMessages(blackjackGame, now);
        Util.assertMessageFound(blackjackGame, "You Lose.");
        Util.assertEqual(access1.doLoadAccount().getBalance(), 10);
    }

}
