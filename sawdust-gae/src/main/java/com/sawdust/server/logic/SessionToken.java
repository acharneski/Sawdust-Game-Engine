package com.sawdust.server.logic;

import java.util.logging.Logger;

import com.sawdust.engine.service.debug.GameException;
import com.sawdust.server.datastore.entities.Account;
import com.sawdust.server.datastore.entities.GameSession;
import com.sawdust.server.logic.User.UserTypes;

public class SessionToken implements com.sawdust.engine.service.data.SessionToken
{
    private static final Logger LOG = Logger.getLogger(SessionToken.class.getName());

    public Account _account = null;
    private GameSession _session = null;

    private com.sawdust.engine.common.AccessToken _token = null;
    public User _user = null;

    public SessionToken()
    {
    }

    public SessionToken(final com.sawdust.engine.common.AccessToken accessData, final User user)
    {
        _token = accessData;
        _user = user;
    }

    public String getName()
    {
        final com.sawdust.engine.service.data.Account account = loadAccount();
        return account.getName();
    }

    public User getUser()
    {
        return _user;
    }

    public String getUserId()
    {
        return _user.getUserID();
    }

    public boolean isAdmin()
    {
        return (UserTypes.Admin == _user.getType());
    }

    public Account loadAccount()
    {
        if (null != _account) return _account;
        _account = Account.Load(_user.getUserID());
        return _account;
    }

    public GameSession loadSession() throws GameException
    {
        if (null != _session) return _session;
        final String sessionKey = _token.getSessionId();
        if ((null != sessionKey) && !sessionKey.isEmpty())
        {
            try
            {
                _session = GameSession.load(sessionKey, loadAccount().getPlayer());
            }
            catch (final Exception e)
            {
                LOG.info("GameSession load exception: " + e);
            }
        }
        return _session;
    }

}
