package com.sawdust.server.jsp;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.sawdust.engine.common.AccessToken;
import com.sawdust.engine.common.game.ClientCommand;
import com.sawdust.engine.common.game.GameLabel;
import com.sawdust.engine.common.game.GameState;
import com.sawdust.engine.common.game.Message;
import com.sawdust.engine.game.Game;
import com.sawdust.engine.game.players.Player;
import com.sawdust.engine.service.Util;
import com.sawdust.engine.service.debug.GameException;
import com.sawdust.engine.service.debug.InputException;
import com.sawdust.server.appengine.SawdustGameService_Google;
import com.sawdust.server.datastore.DataStore;
import com.sawdust.server.logic.SessionToken;

public class JspGame implements Serializable
{
    private static final Logger LOG = Logger.getLogger(JspGame.class.getName());

    private volatile Game _game;
    private volatile com.sawdust.engine.service.data.GameSession _gameSession;
    private volatile GameState _gwtGame = null;
    private volatile boolean _isInitialized = false;
    private volatile HttpServletRequest _request = null;

    private Player _player = null;
    private String _sessionId = "";

    public void doCommand(final String cmd) throws com.sawdust.engine.common.GameException
    {
        init();
        SawdustGameService_Google.doCommand(cmd, _gameSession, _game, _player);
        _game.saveState();
        setGame((null == _game) ? null : _game.toGwt(_player));
        DataStore.Save();
    }

    public ArrayList<ClientCommand> getCommands() throws GameException
    {
        init();
        return getGame().getCommands();
    }

    public String renderToHtml() throws GameException
    {
        init();
        StringBuffer sb = new StringBuffer();
        sb.append(getGame().html);
        replaceCommandTags(sb);
        return sb.toString();
    }

    private void replaceCommandTags(StringBuffer sb)
    {
        int start = 0;
        while(start >= 0)
        {
            start = replaceCommandTag(sb, start);
        }
    }

    private int replaceCommandTag(StringBuffer sb, int start)
    {
        final String token1 = "<command txt=\"";
        final String token2 = "\">";
        final String token3 = "</command>";
        int index1 = sb.indexOf(token1, start );
        if(0 > index1) return index1;
        int index2 = sb.indexOf(token2, index1);
        if(0 > index2) return index2;
        int index3 = sb.indexOf(token3, index1);
        if(0 > index3) return index3;
        String cmdTxt = sb.substring(index1+token1.length(), index2);
        String innerHtml = sb.substring(index2+token2.length(), index3);
        String nexText = String.format("<a href=\"%s?command=%s\">%s</a>", 
                _request.getRequestURI(), 
                URLEncoder.encode(cmdTxt), 
                innerHtml);
        sb.replace(index1, index3+token3.length(), nexText);
        return index3+token3.length();
    }

    public List<String> getMajorCommands() throws GameException
    {
        init();
        final ArrayList<String> arrayList = new ArrayList<String>();
        for (final GameLabel l : getGame().getLabels())
        {
            if (null == l.command)
            {
                continue;
            }
            if (l.command.isEmpty())
            {
                continue;
            }
            arrayList.add(l.command);
        }
        return arrayList;
    }

    public List<Message> getMessageList() throws GameException
    {
        init();
        final List<Message> messagesSince = getGame().getMessagesSince(0);
        Collections.sort(messagesSince, new Comparator<Message>()
        {
            public int compare(final Message o1, final Message o2)
            {
                final int compareTo = o2.getDateTime().compareTo(o1.getDateTime());
                if (0 != compareTo) return compareTo;
                return ((Integer) o2.getId()).compareTo(o1.getId());
            }
        });
        return messagesSince;
    }

    public Player getPlayer()
    {
        return _player;
    }

    public HttpServletRequest getRequest()
    {
        return _request;
    }

    public String getSessionId()
    {
        return _sessionId;
    }

    private com.sawdust.server.logic.SessionToken getSessionToken()
    {
        final AccessToken accessData = new AccessToken();
        accessData.setSessionId(_sessionId);
        final com.sawdust.server.logic.User user = com.sawdust.server.logic.User.getUser(_request, null, accessData);
        final com.sawdust.server.logic.SessionToken returnValue = new com.sawdust.server.logic.SessionToken(accessData, user);
        return returnValue;
    }

    void init() throws GameException
    {
        if (!_isInitialized)
        {
            _isInitialized = true;
            //DataStore.Clear();
            final SessionToken sessionToken = getSessionToken();
            _gameSession = sessionToken.loadSession();
            if (null == _gameSession) throw new InputException("Unknown session id");
            _game = _gameSession.getLatestState();
            _player = (sessionToken.loadAccount()).getPlayer();
            setGame((null == _game) ? null : _game.toGwt(_player));
            // DataStore.Save();
        }
    }

    public void setPlayer(final Player player)
    {
        _player = player;
    }

    public void setRequest(final HttpServletRequest request)
    {
        _request = request;
    }

    public void setSessionId(final String sessionId)
    {
        _sessionId = sessionId;
    }

    private void setGame(GameState _gwtGame)
    {
        this._gwtGame = _gwtGame;
    }

    private GameState getGame()
    {
        if(null == _gwtGame && null != _game && null != _player) 
        {
            try
            {
                _gwtGame = _game.toGwt(_player);
            }
            catch (GameException e)
            {
                LOG.warning(Util.getFullString(e));
            }
        }
        return _gwtGame;
    }
}
