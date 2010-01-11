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
import com.sawdust.engine.model.state.CommandResult;
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
         Agent<S> agent = getInitAgent();
         this.getSession().addPlayer(agent);
         _innerGame.doAddPlayer(agent);
      }
      catch (GameException e)
      {
         e.printStackTrace();
      }
   }

   @Deprecated @Override
   public Message doAddMessage(MessageType type, String msg, Object... params)
   {
      return _innerGame.doAddMessage(type, msg, params);
   }
   
   @Deprecated
   @Override
   public Message doAddMessage(String msg, Object... params)
   {
      return _innerGame.doAddMessage(msg, params);
   }
   
   @Override
   public TutorialGameBase<S> doAddPlayer(Participant agent) throws GameException
   {
      _innerGame.doAddPlayer(agent);
      return this;
   }
   
   public TutorialGameBase<S> doAdvanceTime(int milliseconds)
   {
      _innerGame.doAdvanceTime(milliseconds);
      return this;
   }
   
   @Override
   public TutorialGameBase<S> doRemoveMember(Participant email) throws GameException
   {
      _innerGame.doRemoveMember(email);
      return this;
   }
   
   @Override
   public TutorialGameBase<S> doReset()
   {
      getInnerGame().doReset();
      _isFirstPlay = false;
      return this;
   }
   
   public TutorialGameBase<S> doSaveState() throws GameException
   {
      getSession().setState(this);
      return this;
   }
   
   @Override
   public TutorialGameBase<S> doStart() throws GameException
   {
      getInnerGame().doStart();
      _isFirstPlay = false;
      return this;
   }
   
   @Override
   public TutorialGameBase<S> doUpdate() throws GameException
   {
      getInnerGame().doUpdate();
      return this;
   }

   public Agent<S> getAgent()
   {
      return _agent;
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
   public Participant getCurrentPlayer()
   {
      return getInnerGame().getCurrentPlayer();
   }

   @Override
   public String getDisplayName(Participant userId)
   {
      return getInnerGame().getDisplayName(userId);
   }

   @Override
   public GameType<?> getGameType()
   {
      return getInnerGame().getGameType();
   }

   @Override
   public int getHeight()
   {
      return _innerGame.getHeight();
   }

   protected abstract Agent<S> getInitAgent();

   public S getInnerGame()
   {
      return _innerGame;
   }

   @Override
   public String getKeywords()
   {
      return _innerGame.getKeywords();
   }

   @Override
   public ArrayList<Message> getMessages()
   {
      return _innerGame.getMessages();
   }

   @Override
   public GameCommand getMove(String commandText, Participant access) throws GameException
   {
      return _innerGame.getMove(commandText, access);
   }

   public ArrayList<GameCommand> getMoves(Participant access) throws GameException
   {
      ArrayList<GameCommand> moves = getInnerGame().getMoves(access);
      ArrayList<GameCommand> returnValue = new ArrayList<GameCommand>();
      final TutorialPhase<S> phase = getPhase();
      if (null != phase)
      {
         for (final GameCommand m : moves)
         {
            if (phase.getAllowCommand(TutorialGameBase.this, m))
            {
               returnValue.add(new GameCommand<TutorialGameBase<S>>()
               {
                  @Override
                  public CommandResult doCommand(Participant p, String commandText) throws GameException
                  {
                     setPhase(phase.doOnPreCommand(TutorialGameBase.this, m, p));
                     CommandResult cmdResult = m.doCommand(p, commandText);
                     setPhase(phase.doOnPostCommand(TutorialGameBase.this, m, p));
                     TutorialGameBase.this.doSaveState();
                     return cmdResult;
                  }
                  
                  @Override
                  public String getCommandText()
                  {
                     return m.getCommandText();
                  }
                  
                  @Override
                  public String getHelpText()
                  {
                     return m.getHelpText();
                  }
               });
            }
         }
      }
      return returnValue;
   }

   public GameState getParentGame()
   {
      throw new RuntimeException("Not Implemented");
   }

   public TutorialPhase<S> getPhase()
   {
      return _phase;
   }

   @Override
   public GameSession getSession()
   {
      return getInnerGame().getSession();
   }

   @Override
   public int getTimeOffset()
   {
      return _innerGame.getTimeOffset();
   }

   public int getUpdateTime()
   {
       return 90;
   }

   @Override
   public GameFrame getView(Player access) throws GameException
   {
      GameFrame gwt = _innerGame.getView(access);
      TutorialPhase<S> phase = getPhase();
      if(null != phase) gwt = phase.getFilteredDisplay(gwt);
      return gwt;
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

   public TutorialGameBase<S> setAgent(Agent<S> agent)
   {
      this._agent = agent;
    return this;
   }

   @Override
   public TutorialGameBase<S> setConfig(GameConfig newConfig) throws GameException
   {
       return this;
   }

   @Override
   public TutorialGameBase<S> setHeight(int height)
   {
      _innerGame.setHeight(height);
      return this;
   }

   public TutorialGameBase<S> setParentGame(GameState _parentGame)
   {
      throw new RuntimeException("Not Implemented");
   }

   protected TutorialGameBase<S> setPhase(TutorialPhase<S> phase) throws GameException
   {
      if (null != phase && _phase != phase)
      {
         this._phase = phase;
         this._phase.doOnStartPhase(TutorialGameBase.this);
      }
    return this;
   }

   @Override
   public TutorialGameBase<S> setSilent(boolean b)
   {
      _innerGame.setSilent(b);
      return this;
   }

   @Override
   public TutorialGameBase<S> setTimeOffset(int timeOffset)
   {
      _innerGame.setTimeOffset(timeOffset);
      return this;
   }

   @Override
   public TutorialGameBase<S> setVersionNumber(int i)
   {
      _innerGame.setVersionNumber(i);
      return this;
   }

   @Override
   public TutorialGameBase<S> setWidth(int width)
   {
      _innerGame.setWidth(width);
      return this;
   }

}
