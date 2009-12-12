package com.sawdust.server.logic;

import java.io.Serializable;
import java.util.Date;

public class UserToken implements Serializable
{
    private String _id;
    Date expires = new Date();

    /**
	 * 
	 */
    public UserToken()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param id
     */
    public UserToken(final String id)
    {
        super();
        _id = id;
        expires.setTime(expires.getTime() + 1000 * 60 * 60);
    }

    public String getId()
    {
        return _id;
    }

    public void setId(final String id)
    {
        _id = id;
    }
}
