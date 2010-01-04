package com.sawdust.engine.game.players;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.common.config.PropertyConfig;
import com.sawdust.engine.common.geometry.Position;
import com.sawdust.engine.common.geometry.Vector;
import com.sawdust.engine.game.AgentFactory;
import com.sawdust.engine.game.basetypes.BaseGame;
import com.sawdust.engine.game.basetypes.GameState;
import com.sawdust.engine.game.state.GameCommand;
import com.sawdust.engine.game.state.GameLabel;
import com.sawdust.engine.game.state.IndexPosition;
import com.sawdust.engine.service.data.GameSession;
import com.sawdust.engine.service.data.SessionMember;
import com.sawdust.engine.service.debug.GameException;
import com.sawdust.engine.service.debug.GameLogicException;
import com.sawdust.engine.service.debug.InputException;

public class MultiPlayer implements IMultiPlayer, Serializable
{
    protected static final int IDX_LOBBY_LABEL_PUBLIC = 3;

    protected static final int IDX_LOBBY_LABEL_STATUS = 0;
    protected static final int IDX_LOBBY_LABEL_TIP_INVITE = 4;

    private static final Logger LOG = Logger.getLogger(MultiPlayer.class.getName());
    private static final Position memberLabelP = new Position(300, 200);
    protected static final int POS_LOBBY_LABEL = -10;

    protected static final int POS_MEMBER_LIST = -11;
    private static final Position statusLabelP = new Position(300, 10);

    private static final Vector statusLabelV = new Vector(0, 20);

    private int _playerCount = 0;

    private PlayerManager _playerManager;

    private Agent<?> _timeoutAgent = null;

    protected MultiPlayer()
    {
    }

    public MultiPlayer(final int nPlayers)
    {
        super();
        _playerManager = new PlayerManager(nPlayers);
    }

    public void addMember(final GameState game, final Participant agent) throws GameException
    {
        _playerManager.addMember(agent);
//        if (_playerManager.isFull())
//        {
//            Log.info("Join-triggered deal!");
//            game.start();
//            game.update();
//        }
    }

    public void doForceMove(final BaseGame game, final Participant participant) throws GameException
    {
        if (participant instanceof Agent<?>)
        {
            ((Agent<BaseGame>) participant).Move(game, participant);
        }
        else
        {
            getAgent(participant.getId()).Move(game, participant);
        }
    }

    public Agent<BaseGame> getAgent(final String playerID)
    {
        final Participant findPlayer = _playerManager.findPlayer(playerID);
        if (findPlayer instanceof Agent<?>) return (Agent<BaseGame>) findPlayer;
        return getTimeoutAgent();
    }

    public ArrayList<GameCommand> getMoves(final BaseGame game, final Participant access) throws GameException
    {
        final ArrayList<GameCommand> returnValue = new ArrayList<GameCommand>();
        for (final AgentFactory<? extends Agent<?>> f : game.getAgentFactories())
        {
            returnValue.add(new GameCommand()
            {
                @Override
                public boolean doCommand(final Participant p, String commandText) throws GameException
                {
                    final int playerNumber = MultiPlayer.this.getPlayerManager().getPlayerCount() + 1;
                    Agent<?> agent = f.getAgent("AI " + playerNumber);
                    game.getSession().addPlayer(agent);
                    GameSession session = game.getSession();
                    if(null != session)
                    {
                        session.addAgent(f.getName());
                        game.saveState();
                    }
                    return true;
                }

                @Override
                public String getCommandText()
                {
                    return "Add AI - " + f.getName();
                }

                @Override
                public String getHelpText()
                {
                    return "Add an AI player to the game";
                }
            });
        }
        return returnValue;
    }

    public PlayerManager getPlayerManager()
    {
        return _playerManager;
    }

    public Position getPosition(final IndexPosition key, final Player access)
    {
        if (POS_LOBBY_LABEL == key.getCurveIndex()) return statusLabelP.add(statusLabelV.scale(key.getCardIndex()));
        else if (POS_MEMBER_LIST == key.getCurveIndex()) return memberLabelP.add(statusLabelV.scale(key.getCardIndex()));
        return null;
    }

    public Agent<BaseGame> getTimeoutAgent()
    {
        return (Agent<BaseGame>) _timeoutAgent;
    }

    public boolean isFull()
    {
        return _playerManager.isFull();
    }

    public ArrayList<GameLabel> memberLabels(final GameState game, final Player access)
    {
        final ArrayList<GameLabel> returnValue = new ArrayList<GameLabel>();
        for (final Participant player : _playerManager.getPlayers())
        {
            returnValue.add(new GameLabel("MemberList #" + _playerCount, new IndexPosition(POS_MEMBER_LIST, ++_playerCount), game.getDisplayName(player)));
        }
        final GameSession session = game.getSession();
        final SessionMember owner = (null == session)?null:session.getOwner();
        if (null != owner && access.equals(owner.getPlayer()))
        {
            int cnt = -1;
            for (final AgentFactory<?> f : game.getAgentFactories())
            {
                cnt += 2;
                final String buttonLabel = String.format("Add AI Player (%s)", f.getName());
                final GameLabel addAi = new GameLabel("Add AI " + (_playerCount + cnt), new IndexPosition(POS_MEMBER_LIST, _playerCount + cnt), buttonLabel);
                addAi.setCommand("Add AI - " + f.getName());
                returnValue.add(addAi);
            }
        }
        return returnValue;
    }

    public void removeMember(final GameState game, final Participant email) throws GameException
    {
        _playerManager.dropMember(email);
        _playerCount = 0;
    }

    public void setPlayerManager(final PlayerManager playerManager)
    {
        _playerManager = playerManager;
    }

    public void setTimeoutAgent(final Agent<?> timeoutAgent)
    {
        _timeoutAgent = timeoutAgent;
    }

    public ArrayList<GameLabel> setupLobbyLabels(final GameState game, final Player access) throws InputException
    {
        final ArrayList<GameLabel> returnValue = new ArrayList<GameLabel>();
        returnValue.addAll(memberLabels(game, access));
        final GameConfig config = game.getConfig();
        if (null == config) throw new InputException("Null Config");
        if (null == config.getProperties()) throw new InputException("No properties specified");
        final PropertyConfig propertyConfig = config.getProperties().get(com.sawdust.engine.common.config.GameConfig.GAME_NAME);
        if (null == propertyConfig) throw new InputException("Property not set: " + com.sawdust.engine.common.config.GameConfig.GAME_NAME);

        final GameLabel label_gameType = new GameLabel("StatusLabel Main", new IndexPosition(POS_LOBBY_LABEL, IDX_LOBBY_LABEL_STATUS + 1), String.format(
                "%s game", config.getGameName()));
        label_gameType.setWidth(600);
        returnValue.add(label_gameType);

        if (config.getProperties().get(com.sawdust.engine.common.config.GameConfig.PUBLIC_INVITES).getBoolean())
        {
            // <div style='width: 500px; position: relative; left: -100px;'>
            final GameLabel label2 = new GameLabel("StatusLabel Public", new IndexPosition(POS_LOBBY_LABEL, IDX_LOBBY_LABEL_PUBLIC), "This is a public game.");
            label2.setWidth(600);
            returnValue.add(label2);
        }
        else
        {
            final GameLabel label2 = new GameLabel("StatusLabel Public", new IndexPosition(POS_LOBBY_LABEL, IDX_LOBBY_LABEL_PUBLIC),
                    "This is a private game. Please send invites now.");
            label2.setWidth(600);
            returnValue.add(label2);
        }
        final GameLabel label3 = new GameLabel("StatusLabel GameListing Tip", new IndexPosition(POS_LOBBY_LABEL, IDX_LOBBY_LABEL_TIP_INVITE),
                "(To invite your friends, simply give them this web address.)");
        label3.setWidth(600);
        returnValue.add(label3);

        returnValue.add(new GameLabel("MemberList Heading", new IndexPosition(POS_MEMBER_LIST, 0), String.format("<u>%d of %d Players Joined:</u>",
                _playerManager._members.size(), _playerManager._maxSize)));

        return returnValue;
    }

    public void update(final BaseGame game) throws GameException
    {
        Participant currentPlayer = _playerManager.getCurrentPlayer();
        while (currentPlayer instanceof Agent<?>)
        {
            final Agent<BaseGame> agent = (Agent<BaseGame>) currentPlayer;
            agent.Move(game, agent);
            game._timeOffset += 1000;
            GameSession session = game.getSession();
            game.saveState();
            final Participant nextPlayer = _playerManager.getCurrentPlayer();
            if (nextPlayer.equals(agent))
            {
                if (game.isInPlay()) throw new GameLogicException("Wedged Agent!");
                else
                {
                    break;
                }
            }
            currentPlayer = nextPlayer;
        }
    }

    public boolean isSinglePlayer()
    {
        int numPlayers = 0;
        for(Participant p : _playerManager.getPlayers())
        {
            if(p instanceof Player)
            {
                numPlayers++;
            }
        }
        return (1==numPlayers);
    }
}
