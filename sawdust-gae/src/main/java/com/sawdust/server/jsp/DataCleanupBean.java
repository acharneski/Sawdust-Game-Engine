package com.sawdust.server.jsp;

import java.io.Serializable;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sawdust.server.datastore.DataObj;
import com.sawdust.server.datastore.DataStats;
import com.sawdust.server.datastore.DataStore;
import com.sawdust.server.datastore.entities.Account;
import com.sawdust.server.datastore.entities.AccountPlayer;
import com.sawdust.server.datastore.entities.GameLeague;
import com.sawdust.server.datastore.entities.GameListing;
import com.sawdust.server.datastore.entities.GameSession;
import com.sawdust.server.datastore.entities.GameStateEntity;
import com.sawdust.server.datastore.entities.MoneyAccount;
import com.sawdust.server.datastore.entities.MoneyTransaction;
import com.sawdust.server.datastore.entities.SDWebCache;
import com.sawdust.server.datastore.entities.SessionMember;
import com.sawdust.server.datastore.entities.TinySession;

public class DataCleanupBean implements Serializable
{
    private static final Logger LOG = Logger.getLogger(DataCleanupBean.class.getName());

    public DataCleanupBean()
    {
    }

    private volatile HttpServletRequest _request = null;
    private volatile HttpServletResponse _response = null;

    public void setRequest(final HttpServletRequest request)
    {
        _request = request;
    }

    public void setResponse(final HttpServletResponse response)
    {
        _response = response;
    }

    private DataStats extraResults = new DataStats();
    
    public String clean(String className, Date since)
    {
        StringBuffer sb = new StringBuffer();
        int count = DataStore.Clean(getClass(className), since, 5000, 100, getExtraResults());
        sb.append(String.format("Processed %d rows of the %s table", count, className));
        return sb.toString();
    }

    private Class<? extends DataObj> getClass(String className)
    {
        if(className.equals("Account")) return Account.class;
        if(className.equals("GameLeague")) return GameLeague.class;
        if(className.equals("GameListing")) return GameListing.class;
        if(className.equals("GameSession")) return GameSession.class;
        if(className.equals("GameState")) return GameStateEntity.class;
        if(className.equals("MoneyAccount")) return MoneyAccount.class;
        if(className.equals("MoneyTransaction")) return MoneyTransaction.class;
        if(className.equals("SDWebCache")) return SDWebCache.class;
        if(className.equals("SessionMember")) return SessionMember.class;
        if(className.equals("TinySession")) return TinySession.class;
        throw new RuntimeException("Unknown class type: " + className);
    }

    void setExtraResults(DataStats extraResults)
    {
        this.extraResults = extraResults;
    }

    public DataStats getExtraResults()
    {
        return extraResults;
    }
}
