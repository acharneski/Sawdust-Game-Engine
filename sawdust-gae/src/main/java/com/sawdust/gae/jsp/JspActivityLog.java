package com.sawdust.gae.jsp;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.gae.datastore.entities.ActivityEventRecord;
import com.sawdust.gae.datastore.entities.MoneyAccount;
import com.sawdust.gae.datastore.entities.MoneyTransaction;

public class JspActivityLog implements Serializable
{
    private volatile boolean _isInitialized = false;
    private volatile List<ActivityEventRecord> _transactions = null;

    private MoneyAccount account;
    private Date maxTime = new Date(0);
    private Date minTime = new Date();
    private volatile HttpServletRequest request = null;

    public MoneyAccount getAccount()
    {
        return account;
    }

    public Date getMaxTime()
    {
        return maxTime;
    }

    public Date getMinTime()
    {
        return minTime;
    }

    public HttpServletRequest getRequest()
    {
        return request;
    }

    public List<ActivityEventRecord> getTransactions() throws GameException
    {
        init();
        return _transactions;
    }

    void init() throws GameException
    {
        if (!_isInitialized)
        {
            _isInitialized = true;
            if (null != request.getParameter("until"))
            {
                Date time = new Date(Long.parseLong(request.getParameter("until")));
                _transactions = ActivityEventRecord.getTransactionsUntil(account.getStringId(), time);
            }
            else if (null != request.getParameter("since"))
            {
                Date time = new Date(Long.parseLong(request.getParameter("since")));
                _transactions = ActivityEventRecord.getTransactionsSince(account.getStringId(), time);
            }
            else
            {
                Date time = new Date(new Date().getTime() + 1000);
                _transactions = ActivityEventRecord.getTransactionsUntil(account.getStringId(), time);
            }
            for (final ActivityEventRecord t : _transactions)
            {
                if (maxTime.before(t.time))
                {
                    maxTime = t.time;
                }
                if (minTime.after(t.time))
                {
                    minTime = t.time;
                }
            }
        }
    }

    public void setAccount(final MoneyAccount paccount)
    {
        account = paccount;
    }

    public void setMaxTime(final Date pmaxTime)
    {
        maxTime = pmaxTime;
    }

    public void setMinTime(final Date pminTime)
    {
        minTime = pminTime;
    }

    public void setRequest(final HttpServletRequest prequest)
    {
        request = prequest;
    }
}
