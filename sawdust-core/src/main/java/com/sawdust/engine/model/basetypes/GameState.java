package com.sawdust.engine.model.basetypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sawdust.engine.controller.entities.GameSession;
import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.AgentFactory;
import com.sawdust.engine.model.GameType;
import com.sawdust.engine.model.players.Agent;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.players.Player;
import com.sawdust.engine.model.state.GameCommand;
import com.sawdust.engine.view.config.GameConfig;
import com.sawdust.engine.view.game.GameFrame;
import com.sawdust.engine.view.game.Message;

public interface GameState extends Serializable, Cloneable
{
    @Deprecated
    Message doAddMessage(final Message.MessageType type, final String msg, final Object... params);

    @Deprecated
    Message doAddMessage(final String msg, final Object... params);

    void doAddPlayer(final Participant agent) throws GameException;

    public void doAdvanceTime(int milliseconds);

    void doRemoveMember(final Participant email) throws GameException;

    void doReset();

    void doUpdate() throws GameException;
    
    List<AgentFactory<? extends Agent<?>>> getAgentFactories();

    GameConfig getConfig();

    Participant getCurrentPlayer();

    String getDisplayName(Participant userId);

    GameType<?> getGameType();

    int getHeight();

    String getKeywords();

    ArrayList<Message> getMessages();

    GameCommand getMove(String commandText, Participant access) throws GameException;

    ArrayList<GameCommand> getMoves(Participant access) throws GameException;

    GameState getParentGame();

    GameSession getSession();

    int getTimeOffset();

    int getUpdateTime();

    GameFrame getView(final Player access) throws GameException;

    int getWidth();

    boolean isInPlay();

    void saveState() throws GameException;

    void setConfig(GameConfig newConfig) throws GameException;

    void setHeight(final int height);

    void setParentGame(GameState _parentGame);

    void setSilent(boolean b);

    void setTimeOffset(int timeOffset);

    void setVersionNumber(int i);

    void setWidth(final int width);

    void doStart() throws GameException;

}