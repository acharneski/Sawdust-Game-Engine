package com.sawdust.engine.game.stop.tutorial.basic;

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
import com.sawdust.engine.game.stop.StopAgent1;
import com.sawdust.engine.game.stop.StopGame;
import com.sawdust.engine.service.Util;
import com.sawdust.engine.service.data.GameSession;
import com.sawdust.engine.service.debug.GameException;

public class TutorialGame extends TutorialGameBase<StopGame>
{
   private static final Logger LOG = Logger.getLogger(TutorialGame.class.getName());

   
   protected TutorialGame()
   {
       super();
   }

   public TutorialGame(GameConfig c, final SessionFactory sessionF)
   {
      super(new StopGame(c) {

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
   protected Agent<StopGame> initAgent()
   {
      return new Agent<StopGame>("Instructor")
      {
         @Override
         public void Move(StopGame game, Participant participant) throws GameException, com.sawdust.engine.common.GameException
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
