package com.sawdust.test.game;

import org.junit.Test;

import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.game.blackjack.BlackjackGame;
import com.sawdust.engine.game.euchre.EuchreGame;
import com.sawdust.engine.game.poker.PokerGame;
import com.sawdust.engine.game.stop.StopGame;
import com.sawdust.engine.service.data.GameSession;
import com.sawdust.test.mock.MockGameSession;

public class RandomSuite extends GenericPlayTest {

	@Test(timeout = 10000)
	public void testBlackjack() throws Exception {
		System.out.println("\n testBlackjack() \n");
		testGame(new BlackjackGame(new GameConfig()) {
			MockGameSession session;
			
			@Override
			public GameSession getSession() {
				if (null == session) {
					session = new MockGameSession(this);
				}
				return session;
			}
		}, 1);
	}

//	@Test(timeout = 10000)
//	public void testPoker() throws Exception {
//		System.out.println("\n testGo() \n");
//		testGame(new PokerGame(new GameConfig()) {
//			TestGameSession session;
//
//			@Override
//			public GameSession getSession() {
//				if (null == session) {
//					session = new TestGameSession(this);
//				}
//				return session;
//			}
//		}, 2);
//	}
//
//	@Test(timeout = 10000)
//	public void testEuchre() throws Exception {
//		System.out.println("\n testGo() \n");
//		testGame(new EuchreGame(new GameConfig()) {
//			TestGameSession session;
//
//			@Override
//			public GameSession getSession() {
//				if (null == session) {
//					session = new TestGameSession(this);
//				}
//				return session;
//			}
//		}, 4);
//	}

	@Test(timeout = 10000)
	public void testGo() throws Exception {
		System.out.println("\n testGo() \n");
		StopGame game = new StopGame(new GameConfig()) {
			MockGameSession session;

			@Override
			public GameSession getSession() {
				if (null == session) {
					session = new MockGameSession(this);
				}
				return session;
			}
		};
		testGame(game, newAgent("Mario"), newPlayer(game, "Luigi"));
	}
}
