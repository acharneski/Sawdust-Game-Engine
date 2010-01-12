package com.sawdust.test.game;

import java.util.Collection;
import java.util.Date;

import junit.framework.TestCase;

import org.junit.Test;

import com.sawdust.engine.controller.entities.Account;
import com.sawdust.engine.controller.entities.GameSession;
import com.sawdust.engine.model.LoadedDeck;
import com.sawdust.engine.model.players.AccountFactory;
import com.sawdust.engine.model.players.Player;
import com.sawdust.engine.view.cards.Ranks;
import com.sawdust.engine.view.cards.Suits;
import com.sawdust.engine.view.config.GameConfig;
import com.sawdust.engine.view.game.ActivityEvent;
import com.sawdust.games.euchre.EuchreGame;
import com.sawdust.test.Util;
import com.sawdust.test.mock.MockGameSession;
import com.sawdust.test.mock.MockSessionToken;

public class EuchreTests extends TestCase
{

	@Test(timeout=10000)
	public void testGameWin() throws Exception
	{
		System.out.println("\ntestGameWin()\n");
		Date now = new Date(0);
		EuchreGame game = new EuchreGame(new GameConfig()){
			MockGameSession session = null;
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
		MockGameSession session = (MockGameSession) game.getSession();
		LoadedDeck deck = new LoadedDeck();
		
		deck.doAddCard(Ranks.King, Suits.Clubs);
		deck.doAddCard(Ranks.Six, Suits.Diamonds);
		deck.doAddCard(Ranks.Four, Suits.Clubs);
		deck.doAddCard(Ranks.Eight, Suits.Diamonds);
		deck.doAddCard(Ranks.Two, Suits.Clubs);
		deck.doAddCard(Ranks.Seven, Suits.Clubs);
		deck.doAddCard(Ranks.Nine, Suits.Clubs);
		deck.doAddCard(Ranks.Six, Suits.Spades);
		deck.doAddCard(Ranks.Six, Suits.Hearts);
		deck.doAddCard(Ranks.Five, Suits.Spades);
		deck.doAddCard(Ranks.Two, Suits.Diamonds);
		deck.doAddCard(Ranks.Ace, Suits.Spades);
		deck.doAddCard(Ranks.Five, Suits.Diamonds);
		deck.doAddCard(Ranks.Queen, Suits.Diamonds);
		deck.doAddCard(Ranks.Jack, Suits.Hearts);
		deck.doAddCard(Ranks.Jack, Suits.Diamonds);
		deck.doAddCard(Ranks.Nine, Suits.Spades);
		deck.doAddCard(Ranks.King, Suits.Spades);
		deck.doAddCard(Ranks.Queen, Suits.Spades);
		deck.doAddCard(Ranks.Ten, Suits.Clubs);
		deck.doAddCard(Ranks.Ace, Suits.Hearts);
		
		game.setDeck(deck);
		final MockSessionToken access1 = new MockSessionToken("test1",session);
		final MockSessionToken access2 = new MockSessionToken("test2",session);
		final MockSessionToken access3 = new MockSessionToken("test3",session);
		final MockSessionToken access4 = new MockSessionToken("test4",session);
        Player player1 = new Player(access1.getUserId(), false, new AccountFactory()
        {
            
            @Override
            public Account getAccount()
            {
                return access1.doLoadAccount();
            }
        });
		Player player2 = new Player(access2.getUserId(), false, new AccountFactory()
        {
            
            @Override
            public Account getAccount()
            {
                return access2.doLoadAccount();
            }
        });
		Player player3 = new Player(access3.getUserId(), false, new AccountFactory()
        {
            
            @Override
            public Account getAccount()
            {
                return access3.doLoadAccount();
            }
        });
		Player player4 = new Player(access4.getUserId(), false, new AccountFactory()
        {
            
            @Override
            public Account getAccount()
            {
                return access4.doLoadAccount();
            }
        });
		

		session.addPlayer(player1);
		game.doAddPlayer(player1);
		session.addPlayer(player2);
		game.doAddPlayer(player2);
		session.addPlayer(player3);
		game.doAddPlayer(player3);
		session.addPlayer(player4);
		game.doAddPlayer(player4);
		session.doStart();

		Util.assertEqual(access1.doLoadAccount().getBalance(), 9);
		Util.assertEqual(access2.doLoadAccount().getBalance(), 9);
		Util.assertEqual(access3.doLoadAccount().getBalance(), 9);
		Util.assertEqual(access4.doLoadAccount().getBalance(), 9);

		Util.testGuiCommand(session, player2, "Pass"); now = Util.printNewMessages(game, now);
		Util.testGuiCommand(session, player3, "Pass"); now = Util.printNewMessages(game, now);
		Util.testGuiCommand(session, player4, "Pass"); now = Util.printNewMessages(game, now);
		Util.testGuiCommand(session, player1, "Pass"); now = Util.printNewMessages(game, now);

		Util.testGuiCommand(session, player2, "Call Hearts"); now = Util.printNewMessages(game, now);

		Util.testGuiCommand(session, player2, "Play 0"); now = Util.printNewMessages(game, now);

		for(int player = 0; player < 4; player++) game.doForceMove(game.getCurrentPlayer()).doCommand(game.getCurrentPlayer(), null);
        Util.assertMessageFound(game, "test1 (team 1) wins this trick, for a total of 1 wins");
		for(int player = 0; player < 4; player++) game.doForceMove(game.getCurrentPlayer()).doCommand(game.getCurrentPlayer(), null);
        Util.assertMessageFound(game, "test1 (team 1) wins this trick, for a total of 2 wins");
		for(int player = 0; player < 4; player++) game.doForceMove(game.getCurrentPlayer()).doCommand(game.getCurrentPlayer(), null);
		Util.assertMessageFound(game, "test3 (team 1) wins this trick, for a total of 3 wins");

		now = Util.printNewMessages(game, now);
		 
		Util.assertEqual(access1.doLoadAccount().getBalance(), 9);
		Util.assertEqual(access2.doLoadAccount().getBalance(), 9);
		Util.assertEqual(access3.doLoadAccount().getBalance(), 9);
		Util.assertEqual(access4.doLoadAccount().getBalance(), 9);
	}

}
