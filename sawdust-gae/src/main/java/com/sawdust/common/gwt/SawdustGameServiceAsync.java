package com.sawdust.common.gwt;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sawdust.engine.common.AccessToken;
import com.sawdust.engine.common.CommandResult;
import com.sawdust.engine.common.GameLocation;
import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.common.config.LeagueConfig;

/**
 * The async counterpart of <code>SawdustGameService</code>.
 */
public interface SawdustGameServiceAsync
{
    void createGame(AccessToken accessData, GameConfig game, AsyncCallback<GameLocation> callback);

    void createLeage(AccessToken accessData, GameConfig game, LeagueConfig league, AsyncCallback<GameLocation> callback);

    void gameCmd(AccessToken accessData, String cmd, AsyncCallback<CommandResult> callback);

    void gameCmds(AccessToken accessData, ArrayList<String> cmd, AsyncCallback<CommandResult> callback);

    void getGameTemplate(AccessToken accessData, String game, AsyncCallback<GameConfig> callback);

    void getGameUpdate(AccessToken accessData, int gameVersion, AsyncCallback<CommandResult> callback);

    void getState(AccessToken accessData, AsyncCallback<CommandResult> callback);
}
