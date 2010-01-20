package com.sawdust.test.gae;


import java.util.Date;

import org.junit.Test;

import com.sawdust.engine.controller.entities.Account;
import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.LoadedDeck;
import com.sawdust.engine.model.state.GameCommand;
import com.sawdust.engine.view.AccessToken;
import com.sawdust.engine.view.cards.Ranks;
import com.sawdust.engine.view.cards.Suits;
import com.sawdust.engine.view.config.GameConfig;
import com.sawdust.gae.datastore.Games;
import com.sawdust.games.blackjack.BlackjackGame;
import com.sawdust.games.blackjack.BlackjackGameType;
import com.sawdust.games.go.GoAgent1;
import com.sawdust.games.go.GoGame;
import com.sawdust.games.go.GoGameType;
import com.sawdust.test.Util;

public class Go_DuelAgent_Normal extends GameTest<GoGame>
{
    public Go_DuelAgent_Normal()
    {
        super("Go_DuelAgent_Normal");
    }
    
    @Override
    protected GoGame getGame(com.sawdust.engine.controller.entities.GameSession session, AccessToken access, Account account) throws GameException
    {
        session.setAgentEnabled(false);
        GameConfig prototypeConfig = GoGameType.INSTANCE.getPrototypeConfig(account);
        GoGame newGame = (GoGame) Games.NewGame(GoGameType.INSTANCE, prototypeConfig, session, access);
        return newGame;
    }
    
    @Test(timeout = 10000)
    public void testGame() throws Exception
    {
        System.out.println("\ntestGameWin()\n");
        GenericPlayUtil<GoGame> gameRunner = new GenericPlayUtil<GoGame>(_game, 
            GoAgent1.getAgent("AI 1", 1, 15),
            GoAgent1.getAgent("AI 2", 1, 15));
        gameRunner.runGame();
    }
}
