package com.sawdust.server.jsp;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.sawdust.engine.service.Util;
import com.sawdust.server.datastore.DataObj;
import com.sawdust.server.datastore.DataStats;
import com.sawdust.server.datastore.DataStore;
import com.sawdust.server.datastore.entities.Account;
import com.sawdust.server.datastore.entities.AccountPlayer;
import com.sawdust.server.datastore.entities.GameLeague;
import com.sawdust.server.datastore.entities.GameListing;
import com.sawdust.server.datastore.entities.GameSession;
import com.sawdust.server.datastore.entities.GameState;
import com.sawdust.server.datastore.entities.MoneyAccount;
import com.sawdust.server.datastore.entities.MoneyTransaction;
import com.sawdust.server.datastore.entities.SDWebCache;
import com.sawdust.server.datastore.entities.SessionMember;
import com.sawdust.server.datastore.entities.TinySession;

public class DataAdhocBean implements Serializable
{
    private static final Logger LOG = Logger.getLogger(DataAdhocBean.class.getName());

    public DataAdhocBean()
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

    public String doQuery(String query, String operation, int maxRows) throws EntityNotFoundException
    {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Date endTime = new Date(new Date().getTime()+1000*30);
        boolean isDelete = "DELETE".equals(operation);
        String message = "";
        for(String token : query.split(" "))
        {
            int objDeletionCount = 0;
            com.google.appengine.api.datastore.Query qu = new com.google.appengine.api.datastore.Query(token);
            for (Entity entity : datastore.prepare(qu).asIterable())
            {
                objDeletionCount++;
                if(isDelete) 
                {
                    datastore.delete(entity.getKey());
                    if(objDeletionCount > maxRows) 
                    {
                        message += "Ended via row limit<br/>";
                        break;
                    }
                }
                if(endTime.before(new Date())) 
                {
                    message += "Ended via timeout<br/>";
                    break;
                }
            }
            message += String.format(isDelete?"%d %s objects deleted<br/>":"%d %s objects found<br/>", objDeletionCount, token);
        }
        return message;
    }
}
