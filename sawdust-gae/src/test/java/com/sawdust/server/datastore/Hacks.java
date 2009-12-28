package com.sawdust.server.datastore;

import com.sawdust.server.datastore.entities.GameSession;

public class Hacks
{

    public static void nullifyUpdateTime(GameSession session)
    {
        session.updated = null;
        
    }

}
