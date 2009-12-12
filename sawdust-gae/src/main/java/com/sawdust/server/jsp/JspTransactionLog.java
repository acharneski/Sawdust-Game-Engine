package com.sawdust.server.jsp;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.datastore.KeyFactory;
import com.sawdust.engine.service.debug.GameException;
import com.sawdust.server.datastore.entities.Account;
import com.sawdust.server.datastore.entities.GameSession;
import com.sawdust.server.datastore.entities.MoneyAccount;

public class JspTransactionLog implements Serializable
{
    
    private MoneyAccount _account = null;
    
    private volatile boolean _isInitialized = false;
    private volatile String accountId;
    private volatile HttpServletRequest request = null;
    
    public MoneyAccount getAccount() throws GameException
    {
        init();
        return _account;
    }
    
    public String getAccountId()
    {
        return accountId;
    }
    
    public HttpServletRequest getRequest()
    {
        return request;
    }
    
    void init() throws GameException
    {
        if (!_isInitialized)
        {
            _isInitialized = true;
            _account = null;
            if (null == _account)
            {
                try
                {
                    KeyFactory.stringToKey(accountId);
                    _account = MoneyAccount.Load(accountId, GameSession.load(accountId, null).getKey());
                }
                catch (Exception e)
                {
                }
            }
            if (null == _account)
            {
                try
                {
                    _account = MoneyAccount.Load(accountId, Account.Load(accountId).getKey());
                }
                catch (Exception e)
                {
                }
            }
            // DataStore.Save();
        }
    }
    
    public void setAccountId(final String paccountId)
    {
        accountId = paccountId;
    }
    
    public void setRequest(final HttpServletRequest prequest)
    {
        request = prequest;
    }
    
}
