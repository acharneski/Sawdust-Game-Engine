package com.sawdust.server.jsp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.sawdust.engine.common.AccessToken;
import com.sawdust.engine.common.game.ClientCommand;
import com.sawdust.engine.common.game.GameLabel;
import com.sawdust.engine.common.game.GameState;
import com.sawdust.engine.common.game.Message;
import com.sawdust.engine.game.BaseGame;
import com.sawdust.engine.game.Game;
import com.sawdust.engine.game.players.Player;
import com.sawdust.engine.service.debug.GameException;
import com.sawdust.engine.service.debug.InputException;
import com.sawdust.server.appengine.SawdustGameService_Google;
import com.sawdust.server.datastore.DataStore;
import com.sawdust.server.logic.SessionToken;

public class JspGame implements Serializable
{

    private volatile Game _game;
    private volatile com.sawdust.engine.service.data.GameSession _gameSession;

    private volatile GameState _gwtGame = null;

    private volatile boolean _isInitialized = false;
    private Player _player = null;
    private volatile HttpServletRequest _request = null;
    private String _sessionId = "";

    public void doCommand(final String cmd) throws com.sawdust.engine.common.GameException
    {
        init();
        SawdustGameService_Google.doCommand(cmd, _gameSession, _game, _player);
        _game.saveState();
        _gwtGame = (null == _game) ? null : _game.toGwt(_player);
        DataStore.Save();
    }

    public ArrayList<ClientCommand> getCommands() throws GameException
    {
        init();
        return _gwtGame.getCommands();
    }

    public List<String> getMajorCommands() throws GameException
    {
        init();
        final ArrayList<String> arrayList = new ArrayList<String>();
        for (final GameLabel l : _gwtGame.getLabels())
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
        final List<Message> messagesSince = _gwtGame.getMessagesSince(0);
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
            DataStore.Clear();
            final SessionToken sessionToken = getSessionToken();
            _gameSession = sessionToken.loadSession();
            if (null == _gameSession) throw new InputException("Unknown session id");
            _game = _gameSession.getLatestState();
            _player = (sessionToken.loadAccount()).getPlayer();
            _gwtGame = (null == _game) ? null : _game.toGwt(_player);
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
}
