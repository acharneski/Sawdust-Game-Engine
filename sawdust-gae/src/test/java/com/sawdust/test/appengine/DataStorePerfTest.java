package com.sawdust.test.appengine;


import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import junit.framework.TestCase;

import org.junit.Test;

import com.google.appengine.api.datastore.dev.LocalDatastoreService;
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
import com.sawdust.gae.logic.GameTypes;
import com.sawdust.gae.logic.SessionToken;
import com.sawdust.gae.logic.User;
import com.sawdust.gae.logic.User.UserTypes;
import com.sawdust.games.blackjack.BlackjackGame;
import com.sawdust.games.blackjack.BlackjackGameType;
import com.sawdust.test.Util;

public class DataStorePerfTest extends TestCase
{
    private static final String DEV_SERVER_ROOT = "target/testData/perf-1";

    static final String USER_ID_1 = "test1";

    private LoadedDeck _deck;
    private AccessToken _accessData;
    private User _user;
    private SessionToken _access1;
    private com.sawdust.gae.datastore.entities.Account _account;
    private com.sawdust.engine.controller.entities.GameSession _session;
    private BlackjackGame _blackjackGame;
    private Player _player1;
    private String _id;

    private ApiProxyLocalImpl apiProxyLocalImpl;
    
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        up();
    }

    private void up() throws GameException
    {
        initAppEngine();
        
        _deck = new LoadedDeck();
        _accessData = new AccessToken(USER_ID_1);
        _user = new User(UserTypes.Member, USER_ID_1, null);
        _access1 = new SessionToken(_accessData, _user);
        _account = _access1.doLoadAccount();
        _session = new GameSession(_account);
        _blackjackGame = getGame(_session, _accessData, _account);
        _blackjackGame.doSaveState();
        _blackjackGame.setDeck(_deck);
        _player1 = _account.getPlayer();
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
    
    private void up(String id) throws GameException
    {
        initAppEngine();
        
        _accessData = new AccessToken(USER_ID_1);
        _user = new User(UserTypes.Member, USER_ID_1, null);
        _access1 = new SessionToken(_accessData, _user);
        _account = _access1.doLoadAccount();
        _player1 = _account.getPlayer();
        _session = GameSession.load(id, _player1);
        if(null == _session) throw new RuntimeException("Unknown Session: " + id);
        _blackjackGame = (BlackjackGame) _session.getState();
    }
    
    @Override
    protected void tearDown() throws Exception
    {
        down();
        super.tearDown();
    }

    private void reset() throws GameException
    {
        _blackjackGame.doSaveState();
        down();
        up(_id);
    }

    private void down()
    {
        DataStore.Save();
        _id = _session.getStringId();
        DataStore.Clear();

        LocalDatastoreService lds = (LocalDatastoreService) apiProxyLocalImpl.getService("datastore_v3");
        lds.stop();

        ApiProxy.clearEnvironmentForCurrentThread();
        ApiProxy.setDelegate(null);
        ApiProxy.setEnvironmentForCurrentThread(null);
    }
    
    private BlackjackGame getGame(com.sawdust.engine.controller.entities.GameSession session, AccessToken access, Account account) throws GameException
    {
        GameConfig prototypeConfig = BlackjackGameType.INSTANCE.getBaseConfig(account);
        return (BlackjackGame) Games.NewGame(BlackjackGameType.INSTANCE, prototypeConfig, session, access);
    }
    
    private void startGame(com.sawdust.engine.controller.entities.GameSession session, Player player1) throws com.sawdust.engine.view.GameException
    {
        ArrayList<Participant> players = new ArrayList<Participant>();
        players.add(player1);
        session.doStart();
    }
    
    @Test(timeout = 10000)
    public void testGameWin() throws Exception
    {
        System.out.println("\ntestGameWin()\n");
        Date now = new Date(0);
        _deck.doAddCard(Ranks.Ace, Suits.Clubs);
        _deck.doAddCard(Ranks.Ace, Suits.Clubs);
        _deck.doAddCard(Ranks.King, Suits.Clubs);
        _deck.doAddCard(Ranks.Jack, Suits.Hearts);
        _deck.doAddCard(Ranks.Five, Suits.Clubs);
        _deck.doAddCard(Ranks.Four, Suits.Clubs);
        Util.assertEqual(_access1.doLoadAccount().getBalance(), 10);
        reset();
        
        _session.addPlayer(_player1);
        // blackjackGame.addMember(player1);
        startGame(_session, _player1);
        reset();
        
        now = Util.printNewMessages(_blackjackGame, now);
        Util.assertEqual(_access1.doLoadAccount().getBalance(), 10);
        
        Util.testGuiCommand(_session, _player1, "Hit Me");
        reset();
        
        now = Util.printNewMessages(_blackjackGame, now);
        Util.testGuiCommand(_session, _player1, "Hit Me");
        reset();
        
        now = Util.printNewMessages(_blackjackGame, now);
        Util.testGuiCommand(_session, _player1, "Stay");
        reset();

        now = Util.printNewMessages(_blackjackGame, now);
        Util.assertMessageFound(_blackjackGame, "You Lose.");
        Util.assertEqual(_access1.doLoadAccount().getBalance(), 10);
    }
    
}
