package com.sawdust.test.jsp;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

import com.google.appengine.tools.development.ApiProxyLocalImpl;
import com.google.apphosting.api.ApiProxy;
import com.sawdust.engine.controller.entities.Account;
import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.GameType;
import com.sawdust.engine.model.LoadedDeck;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.players.Player;
import com.sawdust.engine.view.AccessToken;
import com.sawdust.engine.view.cards.Ranks;
import com.sawdust.engine.view.cards.Suits;
import com.sawdust.engine.view.config.GameConfig;
import com.sawdust.gae.datastore.DataStore;
import com.sawdust.gae.datastore.Games;
import com.sawdust.gae.datastore.entities.GameSession;
import com.sawdust.gae.jsp.DataAdhocBean;
import com.sawdust.gae.logic.GameTypes;
import com.sawdust.gae.logic.SessionToken;
import com.sawdust.gae.logic.User;
import com.sawdust.gae.logic.User.UserTypes;
import com.sawdust.games.blackjack.BlackjackGame;
import com.sawdust.games.blackjack.BlackjackGameType;
import com.sawdust.test.Util;
import com.sawdust.test.appengine.MockEnvironment;

public class DataUtilTests extends TestCase
{

    @Test(timeout = 10000)
    public void testPersist() throws Exception
    {
        File dbFile = new File("target/testData/DataUtilTests");
        if(dbFile.exists())
        {
            dbFile.delete();
        }
        
        ApiProxy.setEnvironmentForCurrentThread(new MockEnvironment());
        ApiProxy.setDelegate(new ApiProxyLocalImpl(dbFile){});
        
        com.sawdust.gae.datastore.entities.Account r1 = com.sawdust.gae.datastore.entities.Account.LoadIfExists("test1");
        Assert.assertEquals(null, r1);
        
        DataStore.Clear();
        AccessToken accessData = new AccessToken("test1");
        User user = new User(UserTypes.Member, accessData.getUserId(), null);
        final SessionToken access1 = new SessionToken(accessData, user);
        com.sawdust.gae.datastore.entities.Account account = access1.doLoadAccount();
        DataStore.Save();
        DataStore.Clear();
        
        com.sawdust.gae.datastore.entities.Account r2 = com.sawdust.gae.datastore.entities.Account.LoadIfExists("test1");
        Assert.assertEquals(account, r2);
        DataStore.Clear();
        
        String message = new DataAdhocBean().doQuery("Account", "DELETE", 100);
        System.err.println(message);
        
        com.sawdust.gae.datastore.entities.Account r3 = com.sawdust.gae.datastore.entities.Account.LoadIfExists("test1");
        Assert.assertEquals(null, r3);
        
    }

    @Test(timeout = 10000)
    public void testDeleteIsNeeded() throws Exception
    {
        File dbFile = new File("target/testData/DataUtilTests/");
        if(dbFile.exists())
        {
            dbFile.delete();
        }
        
        ApiProxy.setEnvironmentForCurrentThread(new MockEnvironment());
        ApiProxy.setDelegate(new ApiProxyLocalImpl(dbFile){});
        
        com.sawdust.gae.datastore.entities.Account r1 = com.sawdust.gae.datastore.entities.Account.LoadIfExists("test1");
        Assert.assertEquals(null, r1);
        
        DataStore.Clear();
        AccessToken accessData = new AccessToken("test1");
        User user = new User(UserTypes.Member, accessData.getUserId(), null);
        final SessionToken access1 = new SessionToken(accessData, user);
        com.sawdust.gae.datastore.entities.Account account = access1.doLoadAccount();
        DataStore.Save();
        DataStore.Clear();
        
        com.sawdust.gae.datastore.entities.Account r2 = com.sawdust.gae.datastore.entities.Account.LoadIfExists("test1");
        Assert.assertEquals(account, r2);
        DataStore.Clear();
        
        com.sawdust.gae.datastore.entities.Account r3 = com.sawdust.gae.datastore.entities.Account.LoadIfExists("test1");
        Assert.assertEquals(account, r2);
        DataStore.Clear();
        
    }

}
