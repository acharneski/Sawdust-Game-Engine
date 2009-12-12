package com.sawdust.server.jsp;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Key;
import com.sawdust.engine.game.players.Player;
import com.sawdust.engine.service.data.Account;
import com.sawdust.engine.service.data.SessionToken;
import com.sawdust.engine.service.data.GameSession.SessionStatus;
import com.sawdust.engine.service.debug.GameException;
import com.sawdust.server.datastore.DataStore;
import com.sawdust.server.datastore.entities.GameListing;
import com.sawdust.server.datastore.entities.GameSession;
import com.sawdust.server.datastore.entities.TinySession;
import com.sawdust.server.datastore.entities.GameListing.InviteSearchParam;
import com.sawdust.server.logic.FacebookUser;
import com.sawdust.server.logic.FacebookUser.Site;

public class JspLib
{
    public static final JspLib Instance = new JspLib();

    private static final Logger LOG = Logger.getLogger(JspLib.class.getName());

    private final static void dump_headers(final StringBuilder out, final HttpServletRequest _request)
    {
        out.append("<h2>Headers:</h2>");

        out.append("<lit>");

        Enumeration<?> values;
        String key;

        final Enumeration<?> headers = _request.getHeaderNames();
        while (headers.hasMoreElements())
        {
            key = (String) headers.nextElement();
            values = _request.getHeaders(key);
            while (values.hasMoreElements())
            {
                dump_NPAIR(out, key, (String) values.nextElement());
            }
        }

        out.append("</lit>");
    }

    private final static void dump_NPAIR(final StringBuilder out, final String key, final String value)
    {
        out.append(key);
        out.append(" = [");
        out.append(value);
        out.append("]<br>");
    }

    private final static void dump_server_variables(final StringBuilder out, final HttpServletRequest _request)
    {
        out.append("<h2>Server Variables:</h2>");

        out.append("<lit>");

        dump_NPAIR(out, "AUTH_TYPE", _request.getAuthType());
        dump_NPAIR(out, "REQUEST_METHOD", _request.getMethod());
        dump_NPAIR(out, "PATH_INFO", _request.getPathInfo());
        dump_NPAIR(out, "PATH_TRANSLATED", _request.getPathTranslated());
        dump_NPAIR(out, "QUERY_STRING", _request.getQueryString());
        dump_NPAIR(out, "REQUEST_URI", _request.getRequestURI());
        dump_NPAIR(out, "SCRIPT_NAME", _request.getServletPath());
        // dump_NPAIR(out, "LOCAL_ADDR", _request.getLocalAddr());
        dump_NPAIR(out, "SERVER_PROTOCOL", _request.getProtocol());
        dump_NPAIR(out, "REMOTE_ADDR", _request.getRemoteAddr());
        dump_NPAIR(out, "REMOTE_HOST", _request.getRemoteHost());
        dump_NPAIR(out, "HTTPS", _request.getScheme());
        dump_NPAIR(out, "SERVER_NAME", _request.getServerName());
        dump_NPAIR(out, "SERVER_PORT", String.valueOf(_request.getServerPort()));

        out.append("</lit>");
    }

    protected final static void dumpCookies(final StringBuilder out, final HttpServletRequest _request)
    {
        out.append("<h2>Cookies:</h2>");
        final Cookie cookies[] = _request.getCookies();
        if (null != cookies)
        {
            final int length = cookies.length;
            for (int i = 0; i < length; i++)
            {
                out.append(cookies[i].getName());
                out.append(" = ");
                out.append(cookies[i].getValue());
                out.append("<br>");
            }
        }
    }

    protected final static void dumpFormData(final StringBuilder out, final HttpServletRequest _request)
    {
        out.append("<h2>Form Data:</h2>");

        out.append("method = ");
        out.append(_request.getMethod());

        out.append("<br>content length = ");
        out.append(_request.getContentLength());
        out.append("<br>");

        String key;
        String values[];

        final Enumeration<?> e = _request.getParameterNames();
        while (e.hasMoreElements())
        {
            key = (String) e.nextElement();
            values = _request.getParameterValues(key);

            final int length = values.length;

            for (int i = 0; i < length; i++)
            {
                out.append(key);
                out.append("(");
                out.append(i);
                out.append(") = ");
                out.append(values[i]);
                out.append("<br>");
            }
        }
    }

    public static String getIFrameUrl(final HttpServletRequest request, final Site s)
    {
        final String iframeUrl = s.jspGatewaySite + "/login" + getRedirectUrl(request);
        // iframeUrl += (iframeUrl.contains("?")?"&":"?") + "a=b";
        // String iframeUrl = "login" + getRedirectUrl(request);
        return iframeUrl;

    }

    public static String getRedirectUrl(final HttpServletRequest request)
    {
        final StringBuilder sb = new StringBuilder();
        String redirectUrl = request.getPathInfo();
        if ((null == redirectUrl) || redirectUrl.isEmpty())
        {
            redirectUrl = "/";
        }

        sb.append(redirectUrl);
        boolean firstParam = !redirectUrl.contains("?");
        final Map<?,?> params = request.getParameterMap();
        for (final Object o : params.keySet())
        {
            final String key = (String) o;
            if (key.startsWith("fb"))
            {
                continue;
            }
            final String values[] = (String[]) params.get(o);
            if (null == values)
            {
                continue;
            }
            for (final String value : values)
            {
                // System.out.println(String.format("Parameter %s = %s", key, value));
                sb.append(firstParam ? "?" : "&");
                sb.append(key);
                sb.append("=");
                sb.append(URLEncoder.encode(value));
                firstParam = false;
            }
        }

        final String url = sb.toString();
        return url;
    }

    public static TinySession getTinySession(final HttpServletRequest request)
    {
        String tinyId = request.getPathInfo();
        if (null == tinyId) return null;
        LOG.fine(String.format("pathinfo: %s", tinyId));
        tinyId = tinyId.substring(1);
        tinyId = tinyId.split("&")[0];
        if (null == tinyId) return null;
        final TinySession tinySession = TinySession.load(tinyId);
        if (null == tinySession) return null;
        LOG.fine(String.format("Session %s from tiny %s", tinySession.getSessionId(), tinySession.getTinyId()));
        return tinySession;
    }

    public static void redirect(final HttpServletRequest request, final HttpServletResponse response) throws IOException
    {
        final String url = getRedirectUrl(request);
        LOG.info(String.format("Redirect to %s", url));
        response.sendRedirect(url);
    }

    private JspLib()
    {
    }

    private void dumpFacebook(final StringBuilder out, final HttpServletRequest request)
    {
        final Site facebookId = FacebookUser.verifyFacebookSignature(request);
        out.append(String.format("<h2>Facebook</h2>"));

        final String user = FacebookUser.getFacebookId(request);
        if (null != user)
        {
            out.append(String.format("User ID: %s<br/>", user));
        }
        else
        {
            out.append(String.format("Error: No User ID<br/>"));
        }

        final String sigString = FacebookUser.getParamsToSign(request);
        out.append(String.format("Cannonical Signature Input: <input type=\"text\" value=\"" + sigString.toString() + "\" /><br/>"));

        final String md5 = FacebookUser.getCalculatedSignature(request, facebookId.apiSecretKey);
        if (null != md5)
        {
            out.append(String.format("Calculated Signature: %s<br/>", md5));
        }
        else
        {
            out.append(String.format("Error: No signature could be calculated<br/>"));
        }

        if (null != facebookId)
        {
            out.append(String.format("Valid Request<br/>"));
        }
        else
        {
            out.append(String.format("Invalid Request<br/>"));
        }
    }

    public final String dumpRequest(final HttpServletRequest _request)
    {
        final StringBuilder out = new StringBuilder();

        out.append("<h1>");
        out.append("Dump Form  Servlet");
        out.append("</h1>");

        dump_server_variables(out, _request);
        dump_headers(out, _request);
        dumpFormData(out, _request);
        dumpCookies(out, _request);
        dumpFacebook(out, _request);

        return out.toString();
    }

    public com.sawdust.engine.service.data.Account getAccount(final JspUser accessData)
    {
        final SessionToken validatedAccess = validateAccess(accessData);
        final com.sawdust.engine.service.data.Account account = validatedAccess.loadAccount();
        return account;
    }

    public ArrayList<GameSession> getGameSessions(final JspUser accessData, final String game) throws com.sawdust.engine.common.GameException
    {
        final ArrayList<GameSession> returnValue = new ArrayList<GameSession>();
        final SessionToken validatedAccess = validateAccess(accessData);
        final com.sawdust.engine.service.data.Account account = validatedAccess.loadAccount();
        final HashMap<InviteSearchParam, String> searchParameters = new HashMap<InviteSearchParam, String>();
        searchParameters.put(InviteSearchParam.MaxBid, Integer.toString(account.getBalance()));
        searchParameters.put(InviteSearchParam.Game, game);
        boolean saveData = false;
        try
        {
            for (final GameListing gameListing : GameListing.list(searchParameters, 10))
            {
                final GameSession gameSession = gameListing.getSession();
                gameSession.updateStatus();
                if (gameSession.getSessionStatus() == SessionStatus.Closed)
                {
                    saveData = true;
                    continue;
                }
                returnValue.add(gameSession);
            }
            if (saveData)
            {
                DataStore.Save();
            }
        }
        catch (final GameException e)
        {
            e.printStackTrace();
        }
        return returnValue;
    }

    public JspUser getUser() throws GameException
    {
        return new JspUser();
    }

    public ArrayList<GameSession> getUserSessions(final JspUser accessData)
    {
        final ArrayList<GameSession> returnValue = new ArrayList<GameSession>();
        final SessionToken validatedAccess = validateAccess(accessData);
        final Account account = ((com.sawdust.server.logic.SessionToken) validatedAccess).loadAccount();
        Set<Key> sessionKeys = ((com.sawdust.server.datastore.entities.Account) account).getSessionKeys();
        for (final Key sessionKey : sessionKeys)
        {
            final GameSession gameSession = GameSession.load(sessionKey, ((com.sawdust.server.datastore.entities.Account) account).getPlayer());
            if(null == gameSession)
            {
                sessionKeys.remove(sessionKeys);
            }
            else
            {
                returnValue.add(gameSession);
            }
        }
        return returnValue;
    }

    private SessionToken validateAccess(final JspUser accessData)
    {
        final SessionToken returnValue = new com.sawdust.server.logic.SessionToken(accessData.getAccessToken(), accessData.getUser());
        return returnValue;
    }
    
    public com.sawdust.engine.service.data.GameSession loadSessionFromTinyUrlRequest(HttpServletRequest request, com.sawdust.server.jsp.JspUser user)
    {
        TinySession tsession = getTinySession(request);
        if(null == tsession) return null;
        String sessionId = tsession.getSessionId();
        com.sawdust.server.datastore.entities.Account account = user.getAccount();
        Player player = (null == account)?null:account.getPlayer();
        return GameSession.load(sessionId, player);
    }

}
