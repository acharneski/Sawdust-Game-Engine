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

        // Get a handle on the datastore itself
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        for(String token : query.split(" "))
        {
            com.google.appengine.api.datastore.Query qu = new com.google.appengine.api.datastore.Query("Task");
            for (Entity entity : datastore.prepare(qu).asIterable())
            {
                datastore.delete(entity.getKey());
            }
            
        }

        try
        {
            PersistenceManager pm = DataStore.create();
            Query q = pm.newQuery(qu);
            if (operation.equals("DELETE"))
            {
                long deleteCount = q.deletePersistentAll();
                return String.format("%d entities deleted", deleteCount);
            }
            else
            {
                List execute = (List) q.execute();
                return String.format("%d entities found", execute.size());
            }
        }
        catch (Throwable e)
        {
            return Util.getFullString(e);
        }
    }
}
