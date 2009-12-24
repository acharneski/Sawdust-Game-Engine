package com.sawdust.test.game;

import org.junit.Test;

import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.game.blackjack.BlackjackGame;
import com.sawdust.engine.game.euchre.EuchreGame;
import com.sawdust.engine.game.euchre.ai.Normal1;
import com.sawdust.engine.game.go.GoAgent1;
import com.sawdust.engine.game.go.GoGame;
import com.sawdust.engine.game.poker.PokerGame;
import com.sawdust.engine.game.poker.ai.Regular1;
import com.sawdust.engine.game.stop.StopGame;
import com.sawdust.engine.service.data.GameSession;
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
		}, new Regular1("test1"), new Regular1("test2"));
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
		}, new Normal1("test1"), new Normal1("test2"), new Normal1("test3"), new Normal1("test4"));
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
		testGame(game, new GoAgent1("test1", 1, 15), new GoAgent1("test2", 1, 15));
	}
}
