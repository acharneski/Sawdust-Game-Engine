package com.sawdust.server.jsp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.logging.Logger;

import com.sawdust.engine.service.debug.GameException;

public class JspRequestInfoBean implements Serializable
{
    private static final Logger LOG = Logger.getLogger(JspRequestInfoBean.class.getName());

    private HashMap<String,String> _properties = new HashMap<String, String>();
    
    private boolean isMobile = false;
    private String gameType = "";
    private String gameTypeId = "";
    private String sessionId = "";
    private String gameSessionName = "";
    
    private volatile boolean initialized = false;

    public JspRequestInfoBean() throws GameException
    {
        super();
    }

    public void put(String s, String v)
    {
        _properties.put(s,v);
    }

    public String get(String s)
    {
        if(!_properties.containsKey(s)) return "";
        return _properties.get(s);
    }

    public void setMobile(boolean isMobile)
    {
        this.isMobile = isMobile;
    }

    public boolean isMobile()
    {
        return isMobile;
    }

    public void setGameType(String gameType)
    {
        this.gameType = gameType;
    }

    public String getGameType()
    {
        return gameType;
    }

    public void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
    }

    public String getSessionId()
    {
        return sessionId;
    }

    public void setGameSessionName(String gameSessionName)
    {
        this.gameSessionName = gameSessionName;
    }

    public String getGameSessionName()
    {
        return gameSessionName;
    }

    public void setGameTypeId(String gameTypeId)
    {
        this.gameTypeId = gameTypeId;
    }

    public String getGameTypeId()
    {
        return gameTypeId;
    }
}
