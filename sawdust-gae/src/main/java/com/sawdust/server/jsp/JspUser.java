package com.sawdust.server.jsp;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.sawdust.engine.common.AccessToken;
import com.sawdust.engine.service.debug.GameException;
import com.sawdust.server.appengine.SawdustGameService_Google;
import com.sawdust.server.datastore.entities.Account;
import com.sawdust.server.logic.User.UserTypes;

public class JspUser implements Serializable
{
    private static final Logger LOG = Logger.getLogger(JspUser.class.getName());

    private static final UserService userService = UserServiceFactory.getUserService();
    private Account _account = null;
    private volatile HttpServletRequest _request = null;
    private volatile HttpServletResponse _response = null;
    private AccessToken accessToken = null;
    private volatile boolean initialized = false;
    private com.sawdust.server.logic.User user = null;
    private String userOverride;

    public JspUser() throws GameException
    {
        super();
    }

    /**
     * @return the accessToken
     */
    public AccessToken getAccessToken()
    {
        initUser();
        return accessToken;
    }

    public com.sawdust.server.datastore.entities.Account getAccount()
    {
        initUser();
        if(null == _account) LOG.warning("null == _account");
        return _account;
    }

    public String getEmail()
    {
        initUser();
        if (null != userOverride) return userOverride;
        if (null != _account) return _account.getName();
        return null;
    }

    public String getLoginUrl()
    {
        final String uri = _request.getRequestURI();
        String qs = _request.getQueryString();
        if (null == qs)
        {
            qs = "";
        }
        else
        {
            qs = "?" + getSessonJunk(false, true) + qs;
        }
        return userService.createLoginURL(uri + qs);
    }

    public String getLogoutUrl()
    {
        // String uri = request.getRequestURI();
        // String qs = request.getQueryString();
        // if (null == qs)
        // {
        // qs = "";
        // } else
        // {
        // qs = "?" + this.getSessonJunk(false, true) + qs;
        // }
        // return userService.createLogoutURL(uri + qs);
        return user.getSignoutUrl();
    }

    public String getSessonJunk(final boolean addQuestionMark, final boolean addAmpersand)
    {
        final StringBuilder sb = new StringBuilder();
        if (null != userOverride)
        {
            sb.append("user=");
            sb.append(userOverride);
        }
        String returnValue = "";
        if (sb.length() > 0)
        {
            if (addAmpersand)
            {
                sb.append("&");
            }
            if (addQuestionMark)
            {
                returnValue = "?" + sb.toString();
            }
            else
            {
                returnValue = sb.toString();
            }
        }
        return returnValue;
    }

    public com.sawdust.server.logic.User getUser()
    {
        initUser();
        return user;
    }

    private void initUser()
    {
        if (initialized) return;
        initialized = true;

        user = com.sawdust.server.logic.User.getUser(_request, _response, null);

        if (null != user)
        {
            final String userID = user.getUserID();
            accessToken = new AccessToken(userID);
            _account = Account.Load(userID);
        }
    }

    public boolean isAdmin()
    {
        initUser();
        UserTypes type = user.getType();
        boolean equals = UserTypes.Admin.equals(type);
        return equals;
    }

    public boolean isGuest()
    {
        initUser();
        return (UserTypes.Guest == user.getType());
    }

    public boolean setAccountData(final HttpServletRequest request)
    {
        final HashSet<String> hashMap = new HashSet<String>();
        final Enumeration<?> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements())
        {
            hashMap.add((String) parameterNames.nextElement());
        }
        if (!hashMap.contains("name")) return false;
        initUser();
        _account.setName(request.getParameter("name"));
        return true;
    }

    public void setRequest(final HttpServletRequest request)
    {
        _request = request;
    }

    public void setResponse(final HttpServletResponse response)
    {
        _response = response;
    }

}
