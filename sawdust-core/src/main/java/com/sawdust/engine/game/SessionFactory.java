package com.sawdust.engine.game;

import java.io.Serializable;

import com.sawdust.engine.service.data.GameSession;

public interface SessionFactory extends Serializable
{

    GameSession getSession();
    
}
