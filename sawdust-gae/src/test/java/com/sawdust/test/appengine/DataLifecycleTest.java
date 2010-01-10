package com.sawdust.test.appengine;


import java.io.File;
import java.util.Date;

import junit.framework.TestCase;

import org.junit.Test;
import org.mortbay.log.Log;

import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.ApiProxyLocalImpl;
import com.google.apphosting.api.ApiProxy;
import com.sawdust.engine.controller.entities.Account;
import com.sawdust.engine.model.LoadedDeck;
import com.sawdust.engine.view.AccessToken;
import com.sawdust.engine.view.cards.Ranks;
import com.sawdust.engine.view.cards.Suits;
import com.sawdust.engine.view.config.GameConfig;
import com.sawdust.gae.datastore.DataStore;
import com.sawdust.gae.datastore.Games;
import com.sawdust.gae.datastore.entities.GameSession;
import com.sawdust.gae.logic.GameTypes;
import com.sawdust.gae.logic.SessionToken;
import com.sawdust.gae.logic.User;
import com.sawdust.gae.logic.User.UserTypes;
import com.sawdust.games.blackjack.BlackjackGame;
import com.sawdust.games.blackjack.BlackjackGameType;
import com.sawdust.test.Util;
import com.sawdust.server.datastore.Hacks;

public class DataLifecycleTest extends TestCase
{
    private static final String DEV_SERVER_ROOT = "target/testData/load-1";
    private ApiProxyLocalImpl apiProxyLocalImpl;
    
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        initAppEngine();
    }

    private void initAppEngine()
    {
        DataStore.initTest();
        DataStore.Clear();
        ApiProxy.setEnvironmentForCurrentThread(new MockEnvironment());

        File appRoot = new File(DEV_SERVER_ROOT);
        apiProxyLocalImpl = new ApiProxyLocalImpl(appRoot)
        {
            // Per http://code.google.com/appengine/docs/java/howto/unittesting.html
        };
        ApiProxy.setDelegate(apiProxyLocalImpl);
        //LocalDatastoreService.STORE_DELAY_PROPERTY

        LocalDatastoreService lds = (LocalDatastoreService) apiProxyLocalImpl.getService("datastore_v3");
        lds.setStoreDelay(0);
    }
        @Override
    protected void tearDown() throws Exception
    {
        DataStore.Save();
        DataStore.Clear();
        
        LocalDatastoreService lds = (LocalDatastoreService) apiProxyLocalImpl.getService("datastore_v3");
        lds.stop();
        
        ApiProxy.clearEnvironmentForCurrentThread();
        ApiProxy.setDelegate(null);
        ApiProxy.setEnvironmentForCurrentThread(null);
        super.tearDown();
    }
    
    @Test(timeout = 10000)
    public void testMassCreate() throws Exception
    {
        int loadSize = 50;
        int batchSize = 10;
        
        int flushCount = 0;
        for(int i=0;i<loadSize;i++)
        {
            com.sawdust.gae.datastore.entities.Account account;
            com.sawdust.gae.datastore.entities.GameSession session;
            account = com.sawdust.gae.datastore.entities.Account.Load("test"+i);
            session = new GameSession(account);
            session.setState(new DummyGame());
            if(Math.random()<0.5) com.sawdust.server.datastore.Hacks.nullifyUpdateTime(session); 
            if(++flushCount > batchSize)
            {
                DataStore.Save();
            }
        }
        
        Date since = new Date(new Date().getTime() - 10000);
        int cleanedRecords;
        while((cleanedRecords = DataStore.Clean(com.sawdust.gae.datastore.entities.Account.class, since, 15, batchSize,null))>0)
        {
            Log.info("Cleaning " + cleanedRecords + " rows from account...");
        }
    
    }
    
}
