package com.sawdust.games.go.tutorial.basic;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.sawdust.engine.controller.Util;
import com.sawdust.engine.controller.entities.GameSession;
import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.SessionFactory;
import com.sawdust.engine.model.basetypes.TutorialGameBase;
import com.sawdust.engine.model.players.Agent;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.state.CommandResult;
import com.sawdust.engine.model.state.GameCommand;
import com.sawdust.engine.view.config.GameConfig;
import com.sawdust.games.go.GoGame;
import com.sawdust.games.stop.StopGame.GamePhase;

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
         }

        @Override
        public void doOnPostStartActivity()
        {
            // Supress
        }
         
      });
   }

   @Override
   public TutorialGameBase doReset()
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
   public TutorialGameBase<GoGame> doStart() throws GameException
   {
      super.doStart();
      setPhase(Welcome1.INSTANCE);
      return this;
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
            public CommandResult doCommand(Participant p, String commandText) throws GameException
            {
               getPhase().doOnStartPhase(TutorialGame.this);
               TutorialGame.this.doSaveState();
               return new CommandResult<TutorialGame>(TutorialGame.this);
            }
         });
      }
      
      return moves;
   }
   
   
   @Override
   protected Agent<GoGame> getInitAgent()
   {
      return new Agent<GoGame>("Instructor")
      {
         @Override
         public GameCommand<GoGame> getMove(final GoGame game, final Participant participant) throws GameException
         {
            return new GameCommand<GoGame>()
            {
                @Override
                public CommandResult<GoGame> doCommand(Participant p, String parameters) throws GameException
                {
                    if(null != _agent) 
                    {
                       _agent.getMove(game, participant).doCommand(participant, null);
                    }
                    else
                    {
                        game.doFinishTurn(participant);
                    }
                    return new CommandResult<GoGame>(game);
                }
            };
         }
      };
   }
}
