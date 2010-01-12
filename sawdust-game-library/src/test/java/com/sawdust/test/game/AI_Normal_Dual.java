package com.sawdust.test.game;

import org.junit.Test;

import com.sawdust.engine.controller.entities.GameSession;
import com.sawdust.engine.view.config.GameConfig;
import com.sawdust.games.blackjack.BlackjackGame;
import com.sawdust.games.euchre.EuchreGame;
import com.sawdust.games.euchre.ai.Normal1;
import com.sawdust.games.go.GoAgent1;
import com.sawdust.games.go.GoGame;
import com.sawdust.games.poker.PokerGame;
import com.sawdust.games.poker.ai.Regular1;
import com.sawdust.games.stop.StopGame;
import com.sawdust.test.mock.MockGameSession;

public class AI_Normal_Dual extends GenericPlayTest {

	@Test(timeout = 10000)
	public void testPoker() throws Exception {
		System.out.println("\n testGo() \n");
		testGame(new PokerGame(new GameConfig()) {
			MockGameSession session;

			@Override
			public GameSession getSession() {
				if (null == session) {
					session = new MockGameSession(this);
				}
				return session;
			}
		}, Regular1.getAgent("test1"), Regular1.getAgent("test2"));
	}

	@Test(timeout = 10000)
	public void testEuchre() throws Exception {
		System.out.println("\n testGo() \n");
		testGame(new EuchreGame(new GameConfig()) {
			MockGameSession session;

			@Override
			public GameSession getSession() {
				if (null == session) {
					session = new MockGameSession(this);
				}
				return session;
			}
		}, Normal1.getAgent("test1"), Normal1.getAgent("test2"), Normal1.getAgent("test3"), Normal1.getAgent("test4"));
	}

	@Test(timeout = 10000)
	public void testGo() throws Exception {
		System.out.println("\n testGo() \n");
		StopGame game = new GoGame(new GameConfig()) {
			MockGameSession session;

			@Override
			public GameSession getSession() {
				if (null == session) {
					session = new MockGameSession(this);
				}
				return session;
			}
		};
		testGame(game, GoAgent1.getAgent("test1", 1, 15), GoAgent1.getAgent("test2", 1, 15));
	}
}
