package com.sawdust.test.game;

import java.io.Serializable;

import org.junit.Test;

import com.sawdust.engine.controller.entities.GameSession;
import com.sawdust.engine.view.config.GameConfig;
import com.sawdust.games.go.GoAgent1;
import com.sawdust.games.stop.StopGame;
import com.sawdust.test.mock.MockGameSession;

public class GoAI extends GenericPlayTest implements Serializable
{
    public GoAI()
    {}

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
		testGame(game, 
		        new GoAgent1("Mario",1,10), 
		        new GoAgent1("Luigi",0,10)
		        );
	}
}
