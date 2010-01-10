package com.sawdust.gae.logic;

import java.util.logging.Logger;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.gae.datastore.entities.Account;
import com.sawdust.gae.datastore.entities.GameSession;
import com.sawdust.gae.logic.User.UserTypes;

public class SessionToken implements com.sawdust.engine.controller.entities.SessionToken
{
    private static final Logger LOG = Logger.getLogger(SessionToken.class.getName());

    public Account _account = null;
    private GameSession _session = null;

    private com.sawdust.engine.view.AccessToken _token = null;
    public User _user = null;

    public SessionToken()
    {
    }

    public SessionToken(final com.sawdust.engine.view.AccessToken accessData, final User user)
    {
        _token = accessData;
        _user = user;
    }

    public String getName()
    {
        final com.sawdust.engine.controller.entities.Account account = doLoadAccount();
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

    public Account doLoadAccount()
    {
        if (null != _account) return _account;
        _account = Account.Load(_user.getUserID());
        return _account;
    }

    public GameSession doLoadSession() throws GameException
    {
        if (null != _session) return _session;
        final String sessionKey = _token.getSessionId();
        if ((null != sessionKey) && !sessionKey.isEmpty())
        {
            try
            {
                _session = GameSession.load(sessionKey, doLoadAccount().getPlayer());
            }
            catch (final Exception e)
            {
                LOG.warning("GameSession load exception: " + e);
            }
        }
        return _session;
    }

}
