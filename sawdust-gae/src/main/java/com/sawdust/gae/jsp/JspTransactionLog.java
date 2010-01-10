package com.sawdust.gae.jsp;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.datastore.KeyFactory;
import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.gae.datastore.entities.Account;
import com.sawdust.gae.datastore.entities.GameSession;
import com.sawdust.gae.datastore.entities.MoneyAccount;

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
                catch (Throwable e)
                {
                    // TODO Log something
                }
            }
            if (null == _account)
            {
                try
                {
                    _account = MoneyAccount.Load(accountId, Account.Load(accountId).getKey());
                }
                catch (Throwable e)
                {
                    // TODO Log something
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
