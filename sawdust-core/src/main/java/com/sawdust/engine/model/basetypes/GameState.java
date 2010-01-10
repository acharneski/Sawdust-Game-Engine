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
    Message addMessage(final Message.MessageType type, final String msg, final Object... params);

    Message addMessage(final String msg, final Object... params);

    void addPlayer(final Participant agent) throws GameException;

    public void doAdvanceTime(int milliseconds);

    void doRemoveMember(final Participant email) throws GameException;

    List<AgentFactory<? extends Agent<?>>> getAgentFactories();

    ArrayList<GameCommand> getCommands(final Participant access2) throws GameException;

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

    int getWidth();

    boolean isInPlay();

    void reset();

    void saveState() throws GameException;

    void setHeight(final int height);

    void setParentGame(GameState _parentGame);

    void setSilent(boolean b);

    void setTimeOffset(int timeOffset);

    void setVersionNumber(int i);

    void setWidth(final int width);

    void start() throws GameException;

    GameFrame toGwt(final Player access) throws GameException;

    void update() throws GameException;

    void updateConfig(GameConfig newConfig) throws GameException;

}