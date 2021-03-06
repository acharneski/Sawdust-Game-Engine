package com.sawdust.common.gwt;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sawdust.engine.view.AccessToken;
import com.sawdust.engine.view.CommandResult;
import com.sawdust.engine.view.GameException;
import com.sawdust.engine.view.GameLocation;
import com.sawdust.engine.view.config.GameConfig;
import com.sawdust.engine.view.config.LeagueConfig;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("gwtService")
public interface SawdustGameService extends RemoteService
{
    GameLocation createGame(AccessToken accessData, GameConfig game) throws NumberFormatException, GameException;

    GameLocation createLeage(AccessToken accessData, GameConfig game, LeagueConfig league) throws NumberFormatException, GameException;

    CommandResult gameCmd(AccessToken accessData, String cmd);

    void updateGameConfig(AccessToken accessData, GameConfig game) throws GameException;

    CommandResult gameCmds(AccessToken accessData, ArrayList<String> cmd);

    GameConfig getGameTemplate(AccessToken accessData, String game);

    CommandResult getGameUpdate(AccessToken accessData, int gameVersion) throws NumberFormatException, GameException;

    CommandResult getState(AccessToken accessData);
}
