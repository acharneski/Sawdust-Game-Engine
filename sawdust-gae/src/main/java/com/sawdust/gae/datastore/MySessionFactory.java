/**
 * 
 */
package com.sawdust.gae.datastore;

import com.sawdust.engine.model.SessionFactory;
import com.sawdust.gae.datastore.entities.GameSession;

final class MySessionFactory implements SessionFactory
{
    private final String id;

    MySessionFactory(String id)
    {
        this.id = id;
    }

    @Override
    public com.sawdust.engine.controller.entities.GameSession getSession()
    {
        return GameSession.load(id, null);
    }
}