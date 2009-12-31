/**
 * 
 */
package com.sawdust.test.appengine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.common.game.GameState;
import com.sawdust.engine.common.game.Message;
import com.sawdust.engine.common.game.Message.MessageType;
import com.sawdust.engine.game.AgentFactory;
import com.sawdust.engine.game.Game;
import com.sawdust.engine.game.GameType;
import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.game.players.Player;
import com.sawdust.engine.game.state.GameCommand;
import com.sawdust.engine.game.stop.StopGame.GamePhase;
import com.sawdust.engine.service.debug.GameException;

final class DummyGame implements Game, Serializable
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
    public GameState toGwt(Player access) throws GameException
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
    public void setParentGame(Game parentGame)
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
    public void removeMember(Participant email) throws GameException
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
    public com.sawdust.engine.service.data.GameSession getSession()
    {
        return null;
    }

    @Override
    public Game getParentGame()
    {
        return null;
    }

    @Override
    public ArrayList<Message> getNewMessages()
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
    public String displayName(Participant userId)
    {
        return null;
    }

    @Override
    public void advanceTime(int milliseconds)
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
    public void addMember(Participant agent) throws GameException
    {
    }

    public int getUpdateTime()
    {
        return 90;
    }
}