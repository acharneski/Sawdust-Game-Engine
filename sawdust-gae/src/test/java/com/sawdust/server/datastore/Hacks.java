package com.sawdust.server.datastore;

import com.sawdust.gae.datastore.entities.GameSession;

public class Hacks
{

    public static void nullifyUpdateTime(GameSession session)
    {
        session.updated = null;
        
    }

}
