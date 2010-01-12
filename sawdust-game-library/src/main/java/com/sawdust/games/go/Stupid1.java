package com.sawdust.games.go;

import java.util.ArrayList;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.players.Agent;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.state.GameCommand;
import com.sawdust.games.stop.StopGame;
import com.sawdust.games.stop.StopGame.GamePhase;

public class Stupid1 extends com.sawdust.games.stop.Stupid1
{

   public static Agent<StopGame> getAgent(String s)
   {
      return new Agent<StopGame>(s, new Stupid1());
   }

   @Override
   public GameCommand<StopGame> getMove(StopGame game, Participant player) throws GameException
   {
      if(game.getCurrentPhase() == GamePhase.Playing &&  Math.random() > 0.9)
      {
         ArrayList<GameCommand> commands = game.getMoves(player);
         for (GameCommand gameCommand : commands)
         {
            String commandText = gameCommand.getCommandText();
            if(commandText.equals("Pass"))
            {
               return gameCommand;
            }
         }
         throw new RuntimeException("Pass command not found");
      }
      else
      {
         return super.getMove(game, player);
      }
   }
   
}
