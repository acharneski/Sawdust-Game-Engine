package com.sawdust.engine.model.basetypes;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.sawdust.engine.controller.entities.GameSession;
import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.AgentFactory;
import com.sawdust.engine.model.GameType;
import com.sawdust.engine.model.TutorialPhase;
import com.sawdust.engine.model.players.Agent;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.players.Player;
import com.sawdust.engine.model.state.GameCommand;
import com.sawdust.engine.view.config.GameConfig;
import com.sawdust.engine.view.game.GameFrame;
import com.sawdust.engine.view.game.Message;
import com.sawdust.engine.view.game.Message.MessageType;

public abstract class TutorialGameBase<S extends GameState> implements GameState
{
   private static final Logger LOG = Logger.getLogger(TutorialGameBase.class.getName());
   protected TutorialPhase<S> _phase = null;
   protected Agent<S> _agent = null;
   private S _innerGame = null;
   protected boolean _isFirstPlay = true;
   
   protected TutorialGameBase()
   {
      super();
   }

   public TutorialGameBase(S game)
   {
      this();
      _innerGame = game;
      game.setParentGame(this);
      try
      {
         Agent<S> agent = initAgent();
         this.getSession().addPlayer(agent);
         _innerGame.doAddPlayer(agent);
      }
      catch (GameException e)
      {
         e.printStackTrace();
      }
   }

   protected abstract Agent<S> initAgent();
   
   public ArrayList<GameCommand> getMoves(Participant access) throws GameException
   {
      ArrayList<GameCommand> moves = getInnerGame().getMoves(access);
      ArrayList<GameCommand> returnValue = new ArrayList<GameCommand>();
      final TutorialPhase<S> phase = getPhase();
      if (null != phase)
      {
         for (final GameCommand m : moves)
         {
            if (phase.allowCommand(TutorialGameBase.this, m))
            {
               returnValue.add(new GameCommand()
               {
                  @Override
                  public String getHelpText()
                  {
                     return m.getHelpText();
                  }
                  
                  @Override
                  public String getCommandText()
                  {
                     return m.getCommandText();
                  }
                  
                  @Override
                  public boolean doCommand(Participant p, String commandText) throws GameException
                  {
                     setPhase(phase.preCommand(TutorialGameBase.this, m, p));
                     boolean cmdResult = m.doCommand(p, commandText);
                     setPhase(phase.postCommand(TutorialGameBase.this, m, p));
                     TutorialGameBase.this.saveState();
                     return cmdResult;
                  }
               });
            }
         }
      }
      return returnValue;
   }
   
   @Override
   public void doReset()
   {
      getInnerGame().doReset();
      _isFirstPlay = false;
   }
   
   @Override
   public void doStart() throws GameException
   {
      getInnerGame().doStart();
      _isFirstPlay = false;
   }
   
   @Override
   public GameFrame getView(Player access) throws GameException
   {
      GameFrame gwt = _innerGame.getView(access);
      TutorialPhase<S> phase = getPhase();
      if(null != phase) gwt = phase.filterDisplay(gwt);
      return gwt;
   }
   
   public void setAgent(Agent<S> agent)
   {
      this._agent = agent;
   }
   
   public Agent<S> getAgent()
   {
      return _agent;
   }
   
   protected void setPhase(TutorialPhase<S> phase) throws GameException
   {
      if (null != phase && _phase != phase)
      {
         this._phase = phase;
         this._phase.onStartPhase(TutorialGameBase.this);
      }
   }
   
   public TutorialPhase<S> getPhase()
   {
      return _phase;
   }

   @Override
   public String getDisplayName(Participant userId)
   {
      return getInnerGame().getDisplayName(userId);
   }

   @Override
   public Participant getCurrentPlayer()
   {
      return getInnerGame().getCurrentPlayer();
   }

   @Override
   public GameType<?> getGameType()
   {
      return getInnerGame().getGameType();
   }

   @Override
   public GameSession getSession()
   {
      return getInnerGame().getSession();
   }

   @Override
   public void doUpdate() throws GameException
   {
      getInnerGame().doUpdate();
   }

   public S getInnerGame()
   {
      return _innerGame;
   }
   

   @Override
   public void doAddPlayer(Participant agent) throws GameException
   {
      _innerGame.doAddPlayer(agent);
   }

   @Override
   public Message doAddMessage(MessageType type, String msg, Object... params)
   {
      return _innerGame.doAddMessage(type, msg, params);
   }

   @Override
   public Message doAddMessage(String msg, Object... params)
   {
      return _innerGame.doAddMessage(msg, params);
   }

   @Override
   public List<AgentFactory<? extends Agent<?>>> getAgentFactories()
   {
      return _innerGame.getAgentFactories();
   }

   @Override
   public GameConfig getConfig()
   {
      return _innerGame.getConfig();
   }

   @Override
   public int getHeight()
   {
      return _innerGame.getHeight();
   }

   @Override
   public String getKeywords()
   {
      return _innerGame.getKeywords();
   }

   @Override
   public GameCommand getMove(String commandText, Participant access) throws GameException
   {
      return _innerGame.getMove(commandText, access);
   }

   @Override
   public ArrayList<Message> getMessages()
   {
      return _innerGame.getMessages();
   }

   @Override
   public int getWidth()
   {
      return _innerGame.getWidth();
   }

   @Override
   public boolean isInPlay()
   {
      return _innerGame.isInPlay();
   }

   @Override
   public void doRemoveMember(Participant email) throws GameException
   {
      _innerGame.doRemoveMember(email);
   }

   @Override
   public void setHeight(int height)
   {
      _innerGame.setHeight(height);
   }

   @Override
   public void setSilent(boolean b)
   {
      _innerGame.setSilent(b);
   }

   @Override
   public void setWidth(int width)
   {
      _innerGame.setWidth(width);
   }
   @Override
   public int getTimeOffset()
   {
      return _innerGame.getTimeOffset();
   }

   @Override
   public void setTimeOffset(int timeOffset)
   {
      _innerGame.setTimeOffset(timeOffset);
      
   }

   @Override
   public void setVersionNumber(int i)
   {
      _innerGame.setVersionNumber(i);
   }

   public void saveState() throws GameException
   {
      getSession().setState(this);
   }

   public void setParentGame(GameState _parentGame)
   {
      throw new RuntimeException("Not Implemented");
   }

   public GameState getParentGame()
   {
      throw new RuntimeException("Not Implemented");
   }

   public void doAdvanceTime(int milliseconds)
   {
      _innerGame.doAdvanceTime(milliseconds);
   }

   public int getUpdateTime()
   {
       return 90;
   }

   @Override
   public void setConfig(GameConfig newConfig) throws GameException
   {
   }

}
