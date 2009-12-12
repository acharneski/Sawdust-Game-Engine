package com.sawdust.engine.game.go.tutorial.basic;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.common.game.GameState;
import com.sawdust.engine.common.game.Message;
import com.sawdust.engine.common.game.Message.MessageType;
import com.sawdust.engine.game.AgentFactory;
import com.sawdust.engine.game.Game;
import com.sawdust.engine.game.SessionFactory;
import com.sawdust.engine.game.TutorialGameBase;
import com.sawdust.engine.game.TutorialPhase;
import com.sawdust.engine.game.players.Agent;
import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.game.players.Player;
import com.sawdust.engine.game.state.GameCommand;
import com.sawdust.engine.game.stop.StopGame.GamePhase;
import com.sawdust.engine.game.go.GoAgent1;
import com.sawdust.engine.game.go.GoGame;
import com.sawdust.engine.service.Util;
import com.sawdust.engine.service.data.GameSession;
import com.sawdust.engine.service.debug.GameException;

public class TutorialGame extends TutorialGameBase<GoGame>
{
   private static final Logger LOG = Logger.getLogger(TutorialGame.class.getName());

   
   protected TutorialGame()
   {
       super();
   }

   public TutorialGame(GameConfig c, final SessionFactory sessionF)
   {
      super(new GoGame(c) {

         @Override
         public GameSession getSession()
         {
            return sessionF.getSession();
         }});
   }

   @Override
   public void reset()
   {
      super.reset();
      try
      {
         setPhase(Welcome1.INSTANCE);
      }
      catch (com.sawdust.engine.common.GameException e)
      {
         LOG.warning(Util.getFullString(e));
      }
   }
   
   @Override
   public void start() throws com.sawdust.engine.common.GameException
   {
      super.start();
      setPhase(Welcome1.INSTANCE);
   }
   
   @Override
   public ArrayList<GameCommand> getMoves(Participant access) throws GameException
   {
      ArrayList<GameCommand> moves = new ArrayList<GameCommand>();
      
      if (getInnerGame().getCurrentState() == GamePhase.Lobby)
      {
         moves.addAll(getInnerGame().getMoves(access));
      }
      else if (getInnerGame().getCurrentState() == GamePhase.Playing)
      {
         moves.addAll(super.getMoves(access));
         moves.add(new GameCommand()
         {
            
            @Override
            public String getHelpText()
            {
               return "Return to the original state of this phase of tutorial.";
            }
            
            @Override
            public String getCommandText()
            {
               return "Reset";
            }
            
            @Override
            public boolean doCommand(Participant p) throws GameException, com.sawdust.engine.common.GameException
            {
               getPhase().onStartPhase(TutorialGame.this);
               TutorialGame.this.saveState();
               return true;
            }
         });
      }
      
      return moves;
   }
   
   
   @Override
   protected Agent<GoGame> initAgent()
   {
      return new Agent<GoGame>("Instructor")
      {
         @Override
         public void Move(GoGame game, Participant participant) throws GameException, com.sawdust.engine.common.GameException
         {
            if(null != _agent) 
            {
               _agent.Move(game, participant);
            }
            else
            {
                game.finishTurn(participant);
            }
         }
      };
   }
}
