package com.sawdust.gae.jsp;

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
import com.sawdust.engine.controller.Util;
import com.sawdust.gae.datastore.DataObj;
import com.sawdust.gae.datastore.DataStats;
import com.sawdust.gae.datastore.DataStore;
import com.sawdust.gae.datastore.entities.Account;
import com.sawdust.gae.datastore.entities.AccountPlayer;
import com.sawdust.gae.datastore.entities.GameLeague;
import com.sawdust.gae.datastore.entities.GameListing;
import com.sawdust.gae.datastore.entities.GameSession;
import com.sawdust.gae.datastore.entities.GameStateEntity;
import com.sawdust.gae.datastore.entities.MoneyAccount;
import com.sawdust.gae.datastore.entities.MoneyTransaction;
import com.sawdust.gae.datastore.entities.SDWebCache;
import com.sawdust.gae.datastore.entities.SessionMember;
import com.sawdust.gae.datastore.entities.TinySession;

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
