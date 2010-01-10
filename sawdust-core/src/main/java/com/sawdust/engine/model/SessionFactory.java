package com.sawdust.engine.model;

import java.io.Serializable;

import com.sawdust.engine.controller.entities.GameSession;

public interface SessionFactory extends Serializable
{

    GameSession getSession();
    
}
