package com.sawdust.server.logic;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.sawdust.engine.common.AccessToken;
import com.sawdust.engine.service.Util;
import com.sawdust.server.datastore.DataStore;
import com.sawdust.server.datastore.entities.Account;
import com.sawdust.server.facebook.FacebookSite;
import com.sawdust.server.facebook.FacebookUser;

public class User implements Serializable
{
    public enum UserTypes
    {
        Admin, 
        Guest, 
        Member
    }

    private static final Logger LOG = Logger.getLogger(User.class.getName());
    private static final String SECRET = "sdvljkdlknj";
    private static final UserService userService = com.google.appengine.api.users.UserServiceFactory.getUserService();

    public static void clearCookieLogin(final HttpServletResponse response)
    {
        LOG.fine(String.format("Sending 'clear cookie' directives to client"));
        Util.clearCookie(response, "sdge-login-id");
        Util.clearCookie(response, "sdge-login-time");
        Util.clearCookie(response, "sdge-login-signature");
    }

    private static String createNewGuest(final HttpServletResponse response, final String ip, final String agent)
    {
        if (null == response) return null;
        final String time = DateFormat.getDateTimeInstance().format(new Date());
        final String key = Util.hashStrings(ip, agent, time);
        final String id = "x" + key + "@guest.null";
        final String sig = Util.md5(id + SECRET);
        LOG.info(String.format("Coining guest %s; signature='%s'", key, sig));
        response.addCookie(new Cookie("sdge-guest-id", id));
        response.addCookie(new Cookie("sdge-guest-signature", sig));
        setP3P(response);

        final com.sawdust.engine.service.data.Account account = Account.Load(id);
        account.setName("Guest " + key);
        DataStore.Save();

        return id;
    }

    private static String getCookie(final HttpServletRequest request, final String cookieName)
    {
        String cookieValue = null;
        final Cookie[] cookies = request.getCookies();
        if (null != cookies)
        {
            for (final Cookie c : cookies)
            {
                if (c.getName().equals(cookieName))
                {
                    cookieValue = c.getValue();
                    LOG.finer(String.format("Found cookie %s = %s", cookieName, cookieValue));
                }
            }
        }
        return cookieValue;
    }

    private static String getCookieLogin(final HttpServletRequest request)
    {
        UserToken id = null;

        final String cookieValue_Signature = getCookie(request, "sdge-login-signature");
        final String cookieValue_ID = getCookie(request, "sdge-login-id");
        final String cookieValue_Expire = getCookie(request, "sdge-expire");

        // Not logged in:
        if (null == cookieValue_Signature) return null;
        if (null == cookieValue_ID) return null;
        if (null == cookieValue_Expire) return null;

        final String expectedSignature = Util.md5(cookieValue_ID + SECRET + cookieValue_Expire);
        try
        {
            if (Long.parseLong(cookieValue_Expire) < new Date().getTime())
            {
                // Graceful expiry
                LOG.info("Expired Token: " + cookieValue_Expire);
                return null;
            }
            else
            {
                LOG.finer(String.format("Expired cookie is not expiered: %s", cookieValue_Expire));
            }
        }
        catch (final NumberFormatException e)
        {
            LOG.fine(String.format( //
                    "Error parsing sha1 authentication expiry time\n" + // 
                            "Signature = %s\n" + // 
                            "ID = %s\n" + //
                            "Expire = %s", //
                    cookieValue_Signature, // 
                    cookieValue_ID, //
                    cookieValue_Expire));
            return null;
        }
        if (expectedSignature.equals(cookieValue_Signature))
        {
            LOG.fine("Signed Token: " + cookieValue_Signature);
            id = Util.unstring(cookieValue_ID);
            if(null == id) 
            {
                LOG.warning("Unserializable Token: " + cookieValue_ID);
                return null;
            }
            return id.getId();
        }
        else
        {
        	LOG.fine(String.format( //
                    "Error verifying sha1 authentication\n" + //
                            "Expected Signature = %s\n" + //
                            "Calculated Expire = %s\n" + //
                            "ID = %s\n" + //
                            "Expire = %s", //
                    cookieValue_Signature, //
                    expectedSignature, //
                    cookieValue_ID, //
                    cookieValue_Expire));
            return null;
        }
    }

    private static String getGoogleId()
    {
        final com.google.appengine.api.users.User userObj = userService.getCurrentUser();
        String email = null;
        if (null != userObj)
        {
            email = userObj.getEmail();
        }
        return email;
    }

    private static String getGuestId(final HttpServletRequest request, final HttpServletResponse response)
    {
        String id = getCookie(request, "sdge-guest-id");
        final String signiature = getCookie(request, "sdge-guest-signature");
        if ((null != id) && (null != signiature))
        {
            final String expectedSignature = Util.md5(id + SECRET);
            if (!signiature.equals(expectedSignature))
            {
            	LOG.warning(String.format("Error verifying guest authentication\n" + "Expected Signature = %s\n"
                        + "Calculated Expire = %s" + "ID = %s\n", signiature, expectedSignature, id));
                id = null;
            }
        }
        if (null == id)
        {
            if (null == response) return null;
            final String ip = request.getRemoteAddr();
            final String agent = request.getHeader("User-Agent");
            id = createNewGuest(response, ip, agent);
        }
        return id;
    }

    public static com.sawdust.server.logic.User getUser(final HttpServletRequest request, final HttpServletResponse response, final AccessToken accessData)
    {
        // TODO: We need a global place to put this stuff
        if (null != response)
        {
            setP3P(response); // Just always set this...
        }

        com.sawdust.server.logic.User user = null;

        if (null == user)
        {
            try
            {
                LOG.finer(String.format("Attempting google authorization..."));
                user = getUser_Google(request, accessData, user);
            }
            catch (final Throwable e)
            {
            	LOG.warning(Util.getFullString(e));
            }
        }

        if (null == user)
        {
            try
            {
                LOG.finer(String.format("Attempting facebook authorization..."));
                user = getUser_Facebook(request, user);
            }
            catch (final Throwable e)
            {
            	LOG.warning(Util.getFullString(e));
            }
        }

        if (null == user)
        {
            try
            {
                LOG.finer(String.format("Attempting signed-cookie authorization..."));
                user = getUser_Cookied(request, user);
            }
            catch (final Throwable e)
            {
            	LOG.warning(Util.getFullString(e));
            }
        }

        if (null == user)
        {
            LOG.finer(String.format("Using guest authorization..."));
            final String email = getGuestId(request, response);
            if (null == email) 
            {
                return null;
            }
            user = new com.sawdust.server.logic.User(UserTypes.Guest, email, null);
        }
        LOG.fine(String.format("User is %s", user.getId()));
        if (user.getUserID().endsWith("facebook.null"))
        {
            user.setSite("http://apps.facebook.com/sawdust-games/");
        }
        else if (user.getUserID().endsWith("facebook.beta"))
        {
            user.setSite("http://apps.facebook.com/sawdust-games-beta/");
        }
        return user;
    }

    private static com.sawdust.server.logic.User getUser_Cookied(final HttpServletRequest request, com.sawdust.server.logic.User user)
    {
        final String email = getCookieLogin(request);
        if (null != email)
        {
        	LOG.fine("Cookie Auth: " + email);
            user = new com.sawdust.server.logic.User(UserTypes.Member, email, "/logout.jsp");
        }
        return user;
    }

    private static com.sawdust.server.logic.User getUser_Facebook(final HttpServletRequest request, com.sawdust.server.logic.User user)
    {
        final String email = FacebookUser.getFacebookId(request);
        if (null != email)
        {
            user = new com.sawdust.server.logic.User(UserTypes.Member, email, null);
            final FacebookSite facebookId = FacebookUser.verifyFacebookSignature(request);
            user.setSite(facebookId.facebookSite);
            LOG.fine("User authenticated via Facebook");
        }
        return user;
    }

    private static com.sawdust.server.logic.User getUser_Google(final HttpServletRequest request, final AccessToken accessData,
            com.sawdust.server.logic.User user)
    {
        final String email = getGoogleId();
        if (null != email)
        {
            if (userService.isUserAdmin())
            {
                LOG.fine(String.format("User is google-authenticated administrator: %s", email));
                final Account load = Account.Load(email);
                load.setAdmin(true);
                String userOverride = request.getParameter("user");
                if ((null != accessData) && (null != accessData.getUserId()))
                {
                    userOverride = accessData.getUserId();
                }
                if ((null != userOverride) && !userOverride.isEmpty())
                {
                    LOG.fine(String.format("User override: %s (from %s)", userOverride, user));
                    user = new com.sawdust.server.logic.User(UserTypes.Admin, userOverride, null);
                }
                else
                {
                    user = new com.sawdust.server.logic.User(UserTypes.Admin, email, userService.createLogoutURL("/"));
                }
            }
            else
            {
                LOG.fine(String.format("User is google-authenticated: %s", email));
                user = new com.sawdust.server.logic.User(UserTypes.Member, email, userService.createLogoutURL("/"));
            }
        }
        return user;
    }

    public static void setP3P(final HttpServletResponse response)
    {
        // P3P Stuff to make IE hold our cookies. TODO: verify correctness
        response.setHeader("P3P", "policyref=\"http://sawdust-games.appspot.com/w3c/p3p.xml\",CP=\"SAWD\"");
    }

    private String _id;

    private String _signoutUrl;

    private String _site = "/";

    private UserTypes _type;

    public User(final UserTypes type, final String id, final String signout)
    {
        super();
        if (null == id) throw new NullPointerException();
        if (null != signout)
        {
            _signoutUrl = signout;
        }
        _type = type;
        _id = id;
    }

    public String getSignoutUrl()
    {
        return _signoutUrl;
    }

    public String getSite()
    {
        return _site;
    }

    public UserTypes getType()
    {
        return _type;
    }

    public String getUserID()
    {
        return _id;
    }

    public void setCookieLogin(final HttpServletResponse response)
    {
        Cookie c;
        final UserToken userToken = new UserToken(_id);
        final String userData = Util.string(userToken);
        setP3P(response);

        c = new Cookie("sdge-login-id", userData);
        c.setPath("/");
        response.addCookie(c);
        LOG.fine(String.format("Set Cookie: %s = %s", c.getName(), c.getValue()));

        final String expireStr = Long.toString(new Date().getTime() + 8 * 60 * 60 * 1000);
        c = new Cookie("sdge-expire", expireStr);
        c.setPath("/");
        response.addCookie(c);
        LOG.fine(String.format("Set Cookie: %s = %s", c.getName(), c.getValue()));

        c = new Cookie("sdge-login-signature", Util.md5(userData + SECRET + expireStr));
        c.setPath("/");
        response.addCookie(c);
        LOG.fine(String.format("Set Cookie: %s = %s", c.getName(), c.getValue()));
    }

    public void setSite(final String site)
    {
        _site = site;
    }

    private void setType(UserTypes _type)
    {
        this._type = _type;
    }

    private void setId(String _id)
    {
        this._id = _id;
    }

    private String getId()
    {
        return _id;
    }

}
