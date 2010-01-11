package com.sawdust.games.blackjack.tutorials.basic;

import com.sawdust.engine.controller.entities.GameSession;
import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.SessionFactory;
import com.sawdust.engine.model.basetypes.GameState;
import com.sawdust.engine.model.players.Agent;
import com.sawdust.games.blackjack.BlackjackGame;
import com.sawdust.games.blackjack.BlackjackGameType;

public class GameType extends BlackjackGameType
{
    public static final GameType INSTANCE = new GameType();

    @Override
    public GameState getNewGame(final com.sawdust.engine.view.config.GameConfig c, final SessionFactory sessionFactory) throws GameException
    {
      return new TutorialGame(c, sessionFactory)
        {
            @Override
            protected Agent<BlackjackGame> getInitAgent()
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
