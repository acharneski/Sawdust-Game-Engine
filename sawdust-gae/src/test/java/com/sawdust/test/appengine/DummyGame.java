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
    @Override
	public int hashCode() {
		return 957933997;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}

	DummyGame()
    {
        super();
    }

    @Override
    public Message doAddMessage(MessageType type, String msg, Object... params)
    {
        return null;
    }

    @Override
    public Message doAddMessage(String msg, Object... params)
    {
        return null;
    }

    @Override
    public GameState doAddPlayer(Participant agent) throws GameException
    {
        return this;
    }

    @Override
    public GameState doAdvanceTime(int milliseconds)
    {
        return this;
        
    }

    @Override
    public GameState doRemoveMember(Participant email) throws GameException
    {
        return this;
    }

    @Override
    public GameState doReset()
    {
        return this;
    }

    @Override
    public GameState doSaveState() throws GameException
    {
        return this;
    }

    @Override
    public GameState doStart() throws GameException
    {
        return this;
    }

    @Override
    public List<AgentFactory<?>> getAgentFactories()
    {
        return null;
    }

    @Override
    public GameConfig getConfig()
    {
        return null;
    }

    @Override
    public Participant getCurrentPlayer()
    {
        return null;
    }

    @Override
    public String getDisplayName(Participant userId)
    {
        return null;
    }

    @Override
    public GameType<?> getGameType()
    {
        return null;
    }

    @Override
    public int getHeight()
    {
        return 0;
    }

    @Override
    public String getKeywords()
    {
        return null;
    }

    @Override
    public ArrayList<Message> getMessages()
    {
        return null;
    }

    @Override
    public GameCommand getMove(String commandText, Participant access) throws GameException
    {
        return null;
    }

    @Override
    public ArrayList<GameCommand> getMoves(Participant access) throws GameException
    {
        return null;
    }

    @Override
    public GameState getParentGame()
    {
        return null;
    }

    @Override
    public com.sawdust.engine.controller.entities.GameSession getSession()
    {
        return null;
    }

    @Override
    public int getTimeOffset()
    {
        return 0;
    }

    public int getUpdateTime()
    {
        return 90;
    }

    @Override
    public GameFrame getView(Player access) throws GameException
    {
        return null;
    }

    @Override
    public int getWidth()
    {
        return 0;
    }

    @Override
    public boolean isInPlay()
    {
        return false;
    }

    @Override
    public GameState setConfig(GameConfig newConfig) throws GameException
    {
        return this;
    }

    @Override
    public GameState setHeight(int height)
    {
        return this;
    }

    @Override
    public GameState setParentGame(GameState parentGame)
    {
        return this;
    }

    @Override
    public GameState setSilent(boolean b)
    {
        return this;
    }

    @Override
    public GameState setTimeOffset(int timeOffset)
    {
        return this;
    }

    @Override
    public GameState setVersionNumber(int i)
    {
        return this;
    }

    @Override
    public GameState setWidth(int width)
    {
        return this;
    }

    @Override
    public GameState doUpdate() throws GameException
    {
        throw new RuntimeException("Not Implemented");
    }

}