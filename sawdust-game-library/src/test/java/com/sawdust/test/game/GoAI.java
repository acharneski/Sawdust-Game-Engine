package com.sawdust.test.game;

import java.io.Serializable;

import org.junit.Test;

import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.game.go.GoAgent1;
import com.sawdust.engine.game.stop.StopGame;
import com.sawdust.engine.service.data.GameSession;
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
