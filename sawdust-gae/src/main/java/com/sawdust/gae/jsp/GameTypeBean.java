package com.sawdust.gae.jsp;

import java.io.Serializable;

public class GameTypeBean implements Serializable
{
    private volatile boolean _isInitialized = false;
    private volatile com.sawdust.engine.model.GameType<?> type = null;

    public void setType(com.sawdust.engine.model.GameType<?> type)
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
    public com.sawdust.engine.model.GameType<?> getType()
    {
        _isInitialized = false;
        return type;
    }

}
