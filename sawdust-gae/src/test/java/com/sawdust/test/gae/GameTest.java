package com.sawdust.test.gae;


import java.util.ArrayList;

import com.sawdust.engine.controller.entities.Account;
import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.basetypes.GameState;
import com.sawdust.engine.model.players.Player;
import com.sawdust.engine.view.AccessToken;
import com.sawdust.gae.datastore.entities.GameSession;
import com.sawdust.gae.logic.SessionToken;
import com.sawdust.gae.logic.User;
import com.sawdust.gae.logic.User.UserTypes;

public abstract class GameTest<T extends GameState> extends LocalGaeTest
{
    protected GameTest(String testDir)
    {
        super(testDir);
    }
    
    protected static class PlayerContext
    {
        public PlayerContext(String id)
        {
            accessToken = new AccessToken(id);
            user = new User(UserTypes.Member, id, null);
            sessionToken = new SessionToken(accessToken, user);
            account = sessionToken.doLoadAccount();
            player = account.getPlayer();
        }
        
        final User user;
        final AccessToken accessToken;
        final SessionToken sessionToken;
        final Player player;
        final com.sawdust.gae.datastore.entities.Account account;
    }

    final ArrayList<PlayerContext> _players = new ArrayList<PlayerContext>();
    com.sawdust.engine.controller.entities.GameSession _session;
    T _game;
    String _sessionId;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        createContext();
    }

    private void createContext() throws GameException
    {
        _players.clear();
        for(String id : getPlayerIds())
        {
            _players.add(new PlayerContext(id));
        }
        
        PlayerContext firstPlayer = _players.get(0);
        _session = new GameSession(firstPlayer.account);
        _game = getGame(_session, firstPlayer.accessToken, firstPlayer.account);
        _session.setState(_game);
        _sessionId = _session.getStringId();
    }

    private void reloadContext(String id) throws GameException
    {
        _players.clear();
        for(String playerid : getPlayerIds())
        {
            _players.add(new PlayerContext(playerid));
        }
        
        PlayerContext firstPlayer = _players.get(0);
        _session = GameSession.load(id, firstPlayer.player);
        if(null == _session) throw new RuntimeException("Unknown Session: " + id);
        _game = (T) _session.getState();
    }

    private String[] getPlayerIds()
    {
        return new String[]{"test1"};
    }

    protected void reset() throws GameException
    {
        _game.doSaveState();
        teardownGaeEnvironment();
        setupGaeEnvironment();
        reloadContext(_sessionId);
    }
    
    protected abstract T getGame(com.sawdust.engine.controller.entities.GameSession session, AccessToken access, Account account) throws GameException;
    
}
