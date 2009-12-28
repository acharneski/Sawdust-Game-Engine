package com.sawdust.test.jsp;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

import com.google.appengine.tools.development.ApiProxyLocalImpl;
import com.google.apphosting.api.ApiProxy;
import com.sawdust.engine.common.AccessToken;
import com.sawdust.engine.common.cards.Ranks;
import com.sawdust.engine.common.cards.Suits;
import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.game.GameType;
import com.sawdust.engine.game.LoadedDeck;
import com.sawdust.engine.game.blackjack.BlackjackGame;
import com.sawdust.engine.game.blackjack.BlackjackGameType;
import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.game.players.Player;
import com.sawdust.engine.service.data.Account;
import com.sawdust.engine.service.debug.GameException;
import com.sawdust.test.Util;
import com.sawdust.test.appengine.MockEnvironment;
import com.sawdust.server.datastore.DataStore;
import com.sawdust.server.datastore.Games;
import com.sawdust.server.datastore.entities.GameSession;
import com.sawdust.server.jsp.DataAdhocBean;
import com.sawdust.server.logic.GameTypes;
import com.sawdust.server.logic.SessionToken;
import com.sawdust.server.logic.User;
import com.sawdust.server.logic.User.UserTypes;

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
        
        com.sawdust.server.datastore.entities.Account r1 = com.sawdust.server.datastore.entities.Account.LoadIfExists("test1");
        Assert.assertEquals(null, r1);
        
        DataStore.Clear();
        AccessToken accessData = new AccessToken("test1");
        User user = new User(UserTypes.Member, accessData.getUserId(), null);
        final SessionToken access1 = new SessionToken(accessData, user);
        com.sawdust.server.datastore.entities.Account account = access1.loadAccount();
        DataStore.Save();
        DataStore.Clear();
        
        com.sawdust.server.datastore.entities.Account r2 = com.sawdust.server.datastore.entities.Account.LoadIfExists("test1");
        Assert.assertEquals(account, r2);
        DataStore.Clear();
        
        String message = new DataAdhocBean().doQuery("Account", "DELETE", 100);
        System.err.println(message);
        
        com.sawdust.server.datastore.entities.Account r3 = com.sawdust.server.datastore.entities.Account.LoadIfExists("test1");
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
        
        com.sawdust.server.datastore.entities.Account r1 = com.sawdust.server.datastore.entities.Account.LoadIfExists("test1");
        Assert.assertEquals(null, r1);
        
        DataStore.Clear();
        AccessToken accessData = new AccessToken("test1");
        User user = new User(UserTypes.Member, accessData.getUserId(), null);
        final SessionToken access1 = new SessionToken(accessData, user);
        com.sawdust.server.datastore.entities.Account account = access1.loadAccount();
        DataStore.Save();
        DataStore.Clear();
        
        com.sawdust.server.datastore.entities.Account r2 = com.sawdust.server.datastore.entities.Account.LoadIfExists("test1");
        Assert.assertEquals(account, r2);
        DataStore.Clear();
        
        com.sawdust.server.datastore.entities.Account r3 = com.sawdust.server.datastore.entities.Account.LoadIfExists("test1");
        Assert.assertEquals(account, r2);
        DataStore.Clear();
        
    }

}
