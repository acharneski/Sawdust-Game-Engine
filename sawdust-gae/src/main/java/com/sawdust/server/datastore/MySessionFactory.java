/**
 * 
 */
package com.sawdust.server.datastore;

import com.sawdust.engine.game.SessionFactory;
import com.sawdust.server.datastore.entities.GameSession;

final class MySessionFactory implements SessionFactory
{
    private final String id;

    MySessionFactory(String id)
    {
        this.id = id;
    }

    @Override
    public com.sawdust.engine.service.data.GameSession getSession()
    {
        return GameSession.load(id, null);
    }
}