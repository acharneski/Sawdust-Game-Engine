package com.sawdust.games.go;

import java.util.ArrayList;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.state.GameCommand;
import com.sawdust.games.stop.StopGame;
import com.sawdust.games.stop.StopGame.GamePhase;

public class Stupid1 extends com.sawdust.games.stop.Stupid1
{

   public Stupid1(String s)
   {
      super(s);
   }

   @Override
   public void Move(StopGame game, Participant player) throws GameException
   {
      if(game.getCurrentPhase() == GamePhase.Playing &&  Math.random() > 0.9)
      {
         ArrayList<GameCommand> commands = game.getMoves(player);
         for (GameCommand gameCommand : commands)
         {
            String commandText = gameCommand.getCommandText();
            if(commandText.equals("Pass"))
            {
               gameCommand.doCommand(player, commandText);
               return;
            }
         }
         throw new RuntimeException("Pass command not found");
      }
      else
      {
         super.Move(game, player);
      }
   }
   
}
