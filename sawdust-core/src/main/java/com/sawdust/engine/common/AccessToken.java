package com.sawdust.engine.common;

import java.io.Serializable;

public class AccessToken implements Serializable
{
    protected String _sessionId = "";
    protected String _userId = "";

    public AccessToken()
    {
    }

    public AccessToken(final String email)
    {
        _userId = email;
    }

    /**
     * @return the session
     */
    public String getSessionId()
    {
        return _sessionId;
    }

    public String getUserId()
    {
        return _userId;
    }

    /**
     * @param session
     *            the session to set
     */
    public void setSessionId(final String session)
    {
        _sessionId = session;
    }

    public void setUserId(final String email)
    {
        _userId = email;
    }
}
