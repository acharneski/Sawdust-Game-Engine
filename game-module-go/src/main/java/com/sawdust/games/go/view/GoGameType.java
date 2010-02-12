/**
 * 
 */
package com.sawdust.games.go.view;

import com.sawdust.engine.controller.entities.GameSession;
import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.GameType;
import com.sawdust.engine.model.SessionFactory;
import com.sawdust.engine.model.basetypes.GameState;
import com.sawdust.engine.view.config.GameConfig;

public final class GoGameType extends GameType<GoGame>
{
    public static final GoGameType INSTANCE = new GoGameType();
    
    GoGameType(){}
    
    @Override
    public GameState createNewGame(final GameConfig c, final SessionFactory sessionFactory) throws GameException
    {
        return new GoGame(c){
            @Override
            public GameSession getSession()
            {
                return sessionFactory.getSession();
            }};
    }

    @Override
    public String getDescription()
    {
        return "EXPERIMENTAL: This version of Go has better AI, but only supports single player.";
    }

    @Override
    public String getID()
    {
        return "Go2";
    }

    @Override
    public String getLinks()
    {
        return "<a href='http://en.wikipedia.org/wiki/Go_%28game%29' target=\"_new\">Wikipedia</a>";
    }

    @Override
    public String getName()
    {
        return "Go 2.0";
    }
}