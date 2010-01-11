package com.sawdust.games.stop.tutorial.basic;

import java.util.logging.Logger;

import com.sawdust.engine.controller.Util;
import com.sawdust.engine.controller.entities.GameSession;
import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.SessionFactory;
import com.sawdust.engine.model.basetypes.TutorialGameBase;
import com.sawdust.engine.model.players.Agent;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.view.config.GameConfig;
import com.sawdust.games.stop.StopGame;

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
         }
         
      });
   }

   @Override
   public TutorialGame doReset()
   {
      super.doReset();
      try
      {
         setPhase(Welcome1.INSTANCE);
      }
      catch (com.sawdust.engine.view.GameException e)
      {
         LOG.warning(Util.getFullString(e));
      }
      return this;
   }
   
   @Override
   public TutorialGameBase<StopGame> doStart() throws GameException
   {
      setPhase(Welcome1.INSTANCE);
      return super.doStart();
   }
   
   @Override
   protected Agent<StopGame> getInitAgent()
   {
      return new Agent<StopGame>("Instructor")
      {
         @Override
         public void Move(StopGame game, Participant participant) throws GameException
         {
            if(null != _agent) 
            {
               _agent.Move(game, participant);
            }
            else
            {
               game.doFinishTurn(participant);
            }
         }
      };
   }
}
