package com.sawdust.engine.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.common.game.GameState;
import com.sawdust.engine.common.game.Message;
import com.sawdust.engine.game.players.Agent;
import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.game.players.Player;
import com.sawdust.engine.game.state.GameCommand;
import com.sawdust.engine.service.data.GameSession;
import com.sawdust.engine.service.debug.GameException;

public interface Game extends Serializable, Cloneable
{
   
   Participant getCurrentPlayer();
   
   void addMember(final Participant agent) throws GameException;
   
   Message addMessage(final Message.MessageType type, final String msg, final Object... params);
   
   Message addMessage(final String msg, final Object... params);
   
   String displayName(Participant userId);
   
   List<AgentFactory<? extends Agent<?>>> getAgentFactories();
   
   /**
    * @param access2
    * @return the commands
    * @throws GameException
    */
   ArrayList<GameCommand> getCommands(final Participant access2) throws GameException;
   
   GameConfig getConfig();
   
   GameType<?> getGameType();
   
   int getHeight();
   
   String getKeywords();
   
   GameCommand getMove(String commandText, Participant access) throws GameException;
   
   ArrayList<GameCommand> getMoves(Participant access) throws GameException;
   
   /**
    * @return the newMessages
    */
   ArrayList<Message> getNewMessages();
   
   GameSession getSession();
   
   int getWidth();
   
   boolean isInPlay();
   
   void removeMember(final Participant email) throws GameException;
   
   void reset();
   
   void setHeight(final int height);
   
   void setSilent(boolean b);
   
   void setWidth(final int width);
   
   void start() throws GameException;
   
   GameState toGwt(final Player access) throws GameException;
   
   void update() throws GameException;

   void setVersionNumber(int i);

   int getTimeOffset();

   void setTimeOffset(int timeOffset);

   void saveState() throws GameException;   

   void setParentGame(Game _parentGame);
   
   Game getParentGame();

   public void advanceTime(int milliseconds);

   int getUpdateTime();

   void updateConfig(GameConfig newConfig) throws GameException;
   
}