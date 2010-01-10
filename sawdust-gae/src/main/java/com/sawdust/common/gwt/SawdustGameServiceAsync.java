package com.sawdust.common.gwt;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sawdust.engine.view.AccessToken;
import com.sawdust.engine.view.CommandResult;
import com.sawdust.engine.view.GameLocation;
import com.sawdust.engine.view.config.GameConfig;
import com.sawdust.engine.view.config.LeagueConfig;

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

    void updateGameConfig(AccessToken accessData, GameConfig game, AsyncCallback<Void> callback);
}
