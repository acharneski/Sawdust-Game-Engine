package com.sawdust.gae;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sawdust.common.gwt.SawdustGameService;
import com.sawdust.engine.controller.Util;
import com.sawdust.engine.controller.entities.GameSession.SessionStatus;
import com.sawdust.engine.controller.exceptions.GameLogicException;
import com.sawdust.engine.controller.exceptions.InputException;
import com.sawdust.engine.model.GameType;
import com.sawdust.engine.model.basetypes.BaseGame;
import com.sawdust.engine.model.basetypes.GameState;
import com.sawdust.engine.model.players.Player;
import com.sawdust.engine.model.state.GameCommand;
import com.sawdust.engine.view.AccessToken;
import com.sawdust.engine.view.CommandResult;
import com.sawdust.engine.view.GameException;
import com.sawdust.engine.view.GameLocation;
import com.sawdust.engine.view.config.GameConfig;
import com.sawdust.engine.view.config.LeagueConfig;
import com.sawdust.engine.view.game.GameFrame;
import com.sawdust.gae.datastore.DataStore;
import com.sawdust.gae.datastore.Games;
import com.sawdust.gae.datastore.entities.Account;
import com.sawdust.gae.datastore.entities.GameLeague;
import com.sawdust.gae.datastore.entities.GameSession;
import com.sawdust.gae.datastore.entities.SessionMember;
import com.sawdust.gae.datastore.entities.TinySession;
import com.sawdust.gae.logic.GameTypes;
import com.sawdust.gae.logic.SessionManagementCommands;
import com.sawdust.gae.logic.SessionToken;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class SawdustGameService_Google extends RemoteServiceServlet implements SawdustGameService
{
    private static final Logger LOG = Logger.getLogger(SawdustGameService_Google.class.getName());

    static final UserService userService = com.google.appengine.api.users.UserServiceFactory.getUserService();

    public static void doCommand(final String cmd, final com.sawdust.engine.controller.entities.GameSession gameSession, final GameState tokenGame,
            final Player player) throws GameException
    {
        if ((null != cmd) && (null != tokenGame))
        {
            boolean handled = false;
            for (final SessionManagementCommands thidcmd : com.sawdust.gae.logic.SessionManagementCommands.values())
            {
                GameCommand gameCommand = thidcmd.getGameCommand(tokenGame);
                if (cmd.startsWith(gameCommand.getCommandText()) && gameCommand.doCommand(player, cmd))
                {
                    handled = true;
                    break;
                }
            }
            if (!handled)
            {
                for (final GameCommand thisCmd : tokenGame.getCommands(player))
                {
                    if (cmd.startsWith(thisCmd.getCommandText()) && thisCmd.doCommand(player, cmd))
                    {
                        handled = true;
                    }
                }
            }
            if (!handled)
            {
                SessionManagementCommands.Say.doCommand(player, gameSession, cmd);
            }
            if (handled)
            {
                tokenGame.update();
            }
        }
    }

    private static com.sawdust.gae.logic.SessionToken getSessionToken2(final AccessToken accessData,
            final HttpServletRequest threadLocalRequest, final HttpServletResponse threadLocalResponse)
    {
        final com.sawdust.gae.logic.User user = com.sawdust.gae.logic.User.getUser(threadLocalRequest, threadLocalResponse,
                accessData);
        final com.sawdust.gae.logic.SessionToken returnValue = new com.sawdust.gae.logic.SessionToken(accessData, user);
        return returnValue;
    }

    public GameLocation createGame(final AccessToken access2, final GameConfig game) throws NumberFormatException
    {
        try
        {
            return createGame2(access2, GameTypes.findById(game.getGameName()), game, getThreadLocalRequest(), getThreadLocalResponse());
        }
        catch (final Throwable e)
        {
            handleException(e);
            return null;
        }
    }

    public static GameLocation createGame2(final AccessToken access2, final GameType<?> gameToCreate, final GameConfig game,
            HttpServletRequest request, HttpServletResponse response) throws GameException
    {
        try
        {
            DataStore.Clear();
            final com.sawdust.gae.logic.SessionToken access = getSessionToken2(access2, request, response);
            final Account account = access.doLoadAccount();

            final com.sawdust.gae.datastore.entities.GameSession newSession = new com.sawdust.gae.datastore.entities.GameSession(account);
            final GameState gameObj = Games.NewGame(gameToCreate, game, newSession, access2);
            
            final TinySession tinySession = TinySession.load(newSession);
            final GameLocation gameLocation = new GameLocation(tinySession.getTinyId(), access.getUser().getSite());
            if (gameLocation.site.startsWith("http://apps.facebook.com"))
            {
                gameLocation.page = "f/";
            }
            else
            {
                gameLocation.page = "p/";
            }
            String redirectUrl = gameLocation.getRedirectUrl();
            if(redirectUrl.startsWith("/")) redirectUrl = "http://sawdust-games.appspot.com" + redirectUrl;
            newSession.setUrl(redirectUrl);

            if (gameObj instanceof BaseGame)
            {
                ((BaseGame) gameObj).postStartActivity();
            }

            newSession.doUpdateConfig(game);
            gameObj.saveState();
            final Player player = account.getPlayer();
            newSession.addPlayer(player);

            if (newSession.getStatus() != SessionStatus.Playing)
            {
                if (newSession.getReadyPlayers() >= newSession.getRequiredPlayers())
                {
                    newSession.doStart();
                }
                else
                {
                    newSession.setStatus(com.sawdust.engine.controller.entities.GameSession.SessionStatus.Inviting, gameObj);
                    if (game.getProperties().get(GameConfig.PUBLIC_INVITES).getBoolean())
                    {
                        newSession.createOpenInvite(player);
                    }
                }

            }

            DataStore.Save();
            return gameLocation;
        }
        catch (final GameException e)
        {
            throw new GameException(e.getMessage(), e);
        }
    }

    public GameLocation createLeage(final AccessToken accessData, final GameConfig game, final LeagueConfig league)
            throws GameLogicException
    {
        try
        {
            final GameLeague l = GameLeague.load(league.name);
            if (null != l) throw new GameLogicException("Name already taken: " + league.name);
            // l = new GameLeague(accessData);
            return null;
        }
        catch (final Throwable e)
        {
            handleException(e);
            return null;
        }
    }

    public CommandResult gameCmd(final AccessToken access2, final String cmd)
    {
        try
        {
            DataStore.Clear();
            final com.sawdust.gae.logic.SessionToken access = getSessionToken(access2);
            LOG.info(String.format("Command=%s;\tUser=%s", cmd, access.getUserId()));

            com.sawdust.engine.controller.entities.GameSession gameSession;
            gameSession = access.doLoadSession();
            if (null == gameSession) throw new InputException("Cannot load session");
            final int gameVersion = gameSession.getLatestVersionNumber();
            final GameState tokenGame = gameSession.getState();
            final Account loadAccount = access.doLoadAccount();
            final Player player = loadAccount.getPlayer();
            doCommand(cmd, gameSession, tokenGame, player);

            final CommandResult commandResult = new CommandResult();
            for (final GameState intermediateState : gameSession.doGetStatesSince(gameVersion))
            {
                final GameFrame gwtGame = (null == intermediateState) ? null : intermediateState.toGwt(player);
                commandResult.addState(gwtGame);
            }
            commandResult._bankBalance = loadAccount.getBalance();
            DataStore.Save();
            return commandResult;
        }
        catch (final Throwable e)
        {
            return handleException(e);
        }
    }

    public CommandResult gameCmds(final AccessToken accessData, final ArrayList<String> cmds)
    {
        try
        {
            CommandResult returnValue = null;
            for (final String cmd : cmds)
            {
                returnValue = gameCmd(accessData, cmd);
            }
            return returnValue;
        }
        catch (final Throwable e)
        {
            return handleException(e);
        }
    }

    public GameConfig getGameTemplate(final AccessToken access2, final String gameName)
    {
        try
        {
            final com.sawdust.gae.logic.SessionToken access = getSessionToken(access2);
            final GameType<?> gameObj = GameTypes.findById(gameName);
            if (null == gameObj) return null;
            final GameConfig game = gameObj.getPrototypeConfig(access.doLoadAccount());
            DataStore.Save();
            return game;
        }
        catch (final Throwable e)
        {
            handleException(e);
            return null;
        }
    }

    @Override
    public CommandResult getGameUpdate(final AccessToken accessData, final int gameVersion)
    {
        try
        {
            DataStore.Clear();
            final com.sawdust.gae.logic.SessionToken access = getSessionToken(accessData);
            // System.out.println(String.format("Update=%s;\tUser=%s",
            // gameVersion, access.getUserId()));

            GameSession gameSession = access.doLoadSession();
            if (null == gameSession) throw new InputException("Cannot load session");
            final Account playerAccount = access.doLoadAccount();
            final Player player = playerAccount.getPlayer();

            SessionMember findMember = gameSession.findMember(access.getUserId());
            if (null != findMember)
            {
                findMember.setLastUpdate();
            }
            gameSession.doUpdateStatus();

            final CommandResult commandResult = new CommandResult();
            for (final GameState tokenGame : gameSession.doGetStatesSince(gameVersion))
            {
                final GameFrame gwtGame = (null == tokenGame) ? null : tokenGame.toGwt(player);
                commandResult.addState(gwtGame);
            }
            commandResult._bankBalance = playerAccount.getBalance();
            DataStore.Save();
            return commandResult;
        }
        catch (final GameException e)
        {
            return new CommandResult(e);
        }
    }

    private com.sawdust.gae.logic.SessionToken getSessionToken(final AccessToken accessData)
    {
        return getSessionToken2(accessData, getThreadLocalRequest(), getThreadLocalResponse());
    }

    public CommandResult getState(final AccessToken access2)
    {
        try
        {
            DataStore.Clear();
            final SessionToken access = getSessionToken(access2);
            com.sawdust.engine.controller.entities.GameSession gameSession;
            gameSession = access.doLoadSession();
            if (null == gameSession) throw new InputException("Unknown session id");
            final GameState tokenGame = gameSession.getState();
            final Player player = (access.doLoadAccount()).getPlayer();
            final GameFrame gwtGame = (null == tokenGame) ? null : tokenGame.toGwt(player);
            DataStore.Save();
            return new CommandResult(gwtGame);
        }
        catch (final Throwable e)
        {
            return handleException(e);
        }
    }

    private CommandResult handleException(final Throwable e)
    {
        CommandResult commandResult;
        if (e instanceof GameException)
        {
            commandResult = new CommandResult((GameException) e);
        }
        else
        {
            LOG.warning(String.format("Exception: %s", Util.getFullString(e)));
            commandResult = new CommandResult(new GameException("Fatal Exception"));
        }
        return commandResult;
    }

    @Override
    public void updateGameConfig(AccessToken accessData, GameConfig game) throws GameException
    {
        try
        {
            DataStore.Clear();
            final com.sawdust.gae.logic.SessionToken access = getSessionToken(accessData);
            LOG.info(String.format("ConfigUpdate\tUser=%s", access.getUserId()));

            com.sawdust.engine.controller.entities.GameSession gameSession;
            gameSession = access.doLoadSession();
            if (null == gameSession) throw new InputException("Cannot load session");

            gameSession.doUpdateConfig(game);
            DataStore.Save();
        }
        catch (GameException e)
        {
            throw new GameException(e);
        }
    }

}
