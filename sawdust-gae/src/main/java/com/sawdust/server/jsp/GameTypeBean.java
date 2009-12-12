package com.sawdust.server.jsp;

import java.io.Serializable;

public class GameTypeBean implements Serializable
{
    private volatile boolean _isInitialized = false;
    private volatile com.sawdust.engine.game.GameType<?> type = null;

    public void setType(com.sawdust.engine.game.GameType<?> type)
    {
        init();
        this.type = type;
    }
    private void init()
    {
        if(!_isInitialized) 
        {
            // TODO init
            _isInitialized = true;
        }
    }
    public com.sawdust.engine.game.GameType<?> getType()
    {
        _isInitialized = false;
        return type;
    }

}
