package com.sawdust.engine.view;

import java.io.Serializable;

public class GameLocation implements Serializable
{
    public String page;
    public String site;
    public String token;

    public GameLocation()
    {
    }

    public GameLocation(final String pToken, final String pSite)
    {
        super();
        token = pToken;
        site = pSite;
    }

    public String getRedirectUrl()
    {
        return site + page + token;
    }
}
