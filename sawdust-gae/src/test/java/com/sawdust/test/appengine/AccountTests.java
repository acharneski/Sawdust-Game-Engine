package com.sawdust.test.appengine;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

import com.google.appengine.tools.development.ApiProxyLocalImpl;
import com.google.apphosting.api.ApiProxy;
import com.sawdust.engine.common.AccessToken;
import com.sawdust.server.datastore.DataStore;
import com.sawdust.server.datastore.entities.Account;
import com.sawdust.server.datastore.entities.Promotion;
import com.sawdust.server.logic.SessionToken;
import com.sawdust.server.logic.User;
import com.sawdust.server.logic.User.UserTypes;

public class AccountTests extends TestCase implements Serializable
{
    public class MyTestData implements Serializable
    {
        boolean flag = false;
    }
    
    
    @Test(timeout = 10000)
    public void testPersist() throws Exception
    {
        File dbFile = new File("target/testData/AccountTests");
        if(dbFile.exists())
        {
            dbFile.renameTo(new File("target/testData/" + dbFile.getName() + "." + Long.toHexString(new Date().getTime()) + ".bak"));
        }
        
        ApiProxy.setEnvironmentForCurrentThread(new MockEnvironment());
        ApiProxy.setDelegate(new ApiProxyLocalImpl(dbFile){});
        
        String id1 = "test1";
        com.sawdust.server.datastore.entities.Account r1 = com.sawdust.server.datastore.entities.Account.LoadIfExists(id1);
        Assert.assertEquals(null, r1);
        
        DataStore.Clear();
        
        AccessToken accessData = new AccessToken(id1);
        User user = new User(UserTypes.Member, accessData.getUserId(), null);
        final SessionToken access1 = new SessionToken(accessData, user);
        com.sawdust.server.datastore.entities.Account account = access1.loadAccount();
        Assert.assertEquals(10, account.getBalance());
        MyTestData resource = account.getResource(MyTestData.class);
        Assert.assertEquals(null, resource);
        DataStore.Save();
        DataStore.Clear();
        
        Account ac1 = Account.Load(id1);
        MyTestData obj1 = new MyTestData();
        obj1.flag = true;
        ac1.setResource(MyTestData.class, obj1);
        DataStore.Save();
        DataStore.Clear();
        
        Account ac2 = Account.Load(id1);
        MyTestData obj2 = ac1.getResource(MyTestData.class);
        Assert.assertEquals(obj1.flag, obj1.flag);
        DataStore.Save();
        DataStore.Clear();
        
    }
}
