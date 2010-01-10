/**
 * 
 */
package com.sawdust.test.appengine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.AgentFactory;
import com.sawdust.engine.model.GameType;
import com.sawdust.engine.model.basetypes.GameState;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.players.Player;
import com.sawdust.engine.model.state.GameCommand;
import com.sawdust.engine.view.config.GameConfig;
import com.sawdust.engine.view.game.GameFrame;
import com.sawdust.engine.view.game.Message;
import com.sawdust.engine.view.game.Message.MessageType;
import com.sawdust.games.stop.StopGame.GamePhase;

final class DummyGame implements GameState, Serializable
{
    DummyGame()
    {
        super();
    }

    @Override
    public void update() throws GameException
    {
    }

    @Override
    public GameFrame toGwt(Player access) throws GameException
    {
        return null;
    }

    @Override
    public void start() throws GameException
    {
    }

    @Override
    public void setWidth(int width)
    {
    }

    @Override
    public void setVersionNumber(int i)
    {
    }

    @Override
    public void setTimeOffset(int timeOffset)
    {
    }

    @Override
    public void setSilent(boolean b)
    {
    }

    @Override
    public void setParentGame(GameState parentGame)
    {
    }

    @Override
    public void setHeight(int height)
    {
    }

    @Override
    public void saveState() throws GameException
    {
    }

    @Override
    public void reset()
    {
    }

    @Override
    public void doRemoveMember(Participant email) throws GameException
    {
    }

    @Override
    public boolean isInPlay()
    {
        return false;
    }

    @Override
    public int getWidth()
    {
        return 0;
    }

    @Override
    public int getTimeOffset()
    {
        return 0;
    }

    @Override
    public com.sawdust.engine.controller.entities.GameSession getSession()
    {
        return null;
    }

    @Override
    public GameState getParentGame()
    {
        return null;
    }

    @Override
    public ArrayList<Message> getMessages()
    {
        return null;
    }

    @Override
    public ArrayList<GameCommand> getMoves(Participant access) throws GameException
    {
        return null;
    }

    @Override
    public GameCommand getMove(String commandText, Participant access) throws GameException
    {
        return null;
    }

    @Override
    public String getKeywords()
    {
        return null;
    }

    @Override
    public int getHeight()
    {
        return 0;
    }

    @Override
    public GameType<?> getGameType()
    {
        return null;
    }

    @Override
    public Participant getCurrentPlayer()
    {
        return null;
    }

    @Override
    public GameConfig getConfig()
    {
        return null;
    }

    @Override
    public ArrayList<GameCommand> getCommands(Participant access2) throws GameException
    {
        return null;
    }

    @Override
    public List<AgentFactory<?>> getAgentFactories()
    {
        return null;
    }

    @Override
    public String getDisplayName(Participant userId)
    {
        return null;
    }

    @Override
    public void doAdvanceTime(int milliseconds)
    {
    }

    @Override
    public Message addMessage(String msg, Object... params)
    {
        return null;
    }

    @Override
    public Message addMessage(MessageType type, String msg, Object... params)
    {
        return null;
    }

    @Override
    public void addPlayer(Participant agent) throws GameException
    {
    }

    public int getUpdateTime()
    {
        return 90;
    }

    @Override
    public void updateConfig(GameConfig newConfig) throws GameException
    {
    }

}