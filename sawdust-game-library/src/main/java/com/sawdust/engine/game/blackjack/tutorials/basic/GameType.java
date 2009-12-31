package com.sawdust.engine.game.blackjack.tutorials.basic;

import com.sawdust.engine.game.Game;
import com.sawdust.engine.game.SessionFactory;
import com.sawdust.engine.game.blackjack.BlackjackGame;
import com.sawdust.engine.game.blackjack.BlackjackGameType;
import com.sawdust.engine.game.players.Agent;
import com.sawdust.engine.service.data.GameSession;
import com.sawdust.engine.service.debug.GameException;

public class GameType extends BlackjackGameType
{
    public static final GameType INSTANCE = new GameType();

    @Override
    public Game createNewGame(final com.sawdust.engine.common.config.GameConfig c, final SessionFactory sessionFactory) throws GameException
    {
      return new TutorialGame(c, sessionFactory)
        {
            @Override
            protected Agent<BlackjackGame> initAgent()
            {
               return null;
            }
        };
    }
    
    @Override
    public String getDescription()
    {
        return "A basic tutorial on how to play Blackjack. Demonstrates basic tutorial functionality.";
    }

    @Override
    public String getName()
    {
        return "BlackJack 101";
    }

    protected GameType()
    {
    }

    @Override
    public boolean isSubtype()
    {
        return true;
    }
}
