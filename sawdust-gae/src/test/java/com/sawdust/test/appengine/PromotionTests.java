package com.sawdust.test.appengine;

import java.io.File;
import java.util.Date;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

import com.google.appengine.tools.development.ApiProxyLocalImpl;
import com.google.apphosting.api.ApiProxy;
import com.sawdust.engine.controller.PromotionConfig;
import com.sawdust.engine.view.AccessToken;
import com.sawdust.gae.datastore.DataStore;
import com.sawdust.gae.datastore.entities.Promotion;
import com.sawdust.gae.logic.SessionToken;
import com.sawdust.gae.logic.User;
import com.sawdust.gae.logic.User.UserTypes;

public class PromotionTests extends TestCase
{

    @Test(timeout = 10000)
    public void testPersist() throws Exception
    {
        File dbFile = new File("target/testData/PromotionTests");
        if(dbFile.exists())
        {
            dbFile.renameTo(new File("target/testData/" + dbFile.getName() + "." + Long.toHexString(new Date().getTime()) + ".bak"));
        }
        
        ApiProxy.setEnvironmentForCurrentThread(new MockEnvironment());
        ApiProxy.setDelegate(new ApiProxyLocalImpl(dbFile){});
        
        String id1 = "test1";
        com.sawdust.gae.datastore.entities.Account r1 = com.sawdust.gae.datastore.entities.Account.LoadIfExists(id1);
        Assert.assertEquals(null, r1);
        
        DataStore.Clear();

        
        
        AccessToken accessData = new AccessToken(id1);
        User user = new User(UserTypes.Member, accessData.getUserId(), null);
        final SessionToken access1 = new SessionToken(accessData, user);
        com.sawdust.gae.datastore.entities.Account account = access1.doLoadAccount();
        Assert.assertEquals(10, account.getBalance());
        Promotion p = Promotion.load(account, new PromotionConfig(3, "P", 100,"",""));
        String url = p.getUrl();
        DataStore.Save();
        DataStore.Clear();

        
        com.sawdust.gae.datastore.entities.Account r2 = com.sawdust.gae.datastore.entities.Account.LoadIfExists(id1);
        Assert.assertEquals(account, r2);
        Assert.assertEquals(10, r2.getBalance());
        Promotion p1 = Promotion.load(url);
        p1.addAccount(r2);
        Assert.assertEquals(110, r2.getBalance());
        DataStore.Clear();
    }
}
