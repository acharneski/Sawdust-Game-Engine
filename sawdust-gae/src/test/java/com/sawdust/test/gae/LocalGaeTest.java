package com.sawdust.test.gae;

import java.io.File;

import junit.framework.TestCase;

import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.ApiProxyLocalImpl;
import com.google.apphosting.api.ApiProxy;
import com.sawdust.gae.datastore.DataStore;
import com.sawdust.test.appengine.MockEnvironment;

public abstract class LocalGaeTest extends TestCase
{
    final String DEV_SERVER_ROOT;
    final ApiProxyLocalImpl apiProxyLocalImpl;
    
    protected LocalGaeTest(String testDir)
    {
       DEV_SERVER_ROOT = "target/testData/" + testDir;
       apiProxyLocalImpl = new ApiProxyLocalImpl(new File(DEV_SERVER_ROOT))
       {
           // Per http://code.google.com/appengine/docs/java/howto/unittesting.html
       };
       
    }
    
    
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        setupGaeEnvironment();
    }
    
    protected void setupGaeEnvironment()
    {
        DataStore.initTest();
        DataStore.Clear();
        ApiProxy.setEnvironmentForCurrentThread(new MockEnvironment());
        ApiProxy.setDelegate(apiProxyLocalImpl);
        //LocalDatastoreService.STORE_DELAY_PROPERTY
        LocalDatastoreService lds = (LocalDatastoreService) apiProxyLocalImpl.getService("datastore_v3");
        lds.setStoreDelay(0);
    }
        
    @Override
    protected void tearDown() throws Exception
    {
        teardownGaeEnvironment();
        super.tearDown();
    }

    protected void teardownGaeEnvironment()
    {
        DataStore.Save();
        DataStore.Clear();
        LocalDatastoreService lds = (LocalDatastoreService) apiProxyLocalImpl.getService("datastore_v3");
        lds.stop();
        ApiProxy.clearEnvironmentForCurrentThread();
        ApiProxy.setDelegate(null);
        ApiProxy.setEnvironmentForCurrentThread(null);
    }
    
}
