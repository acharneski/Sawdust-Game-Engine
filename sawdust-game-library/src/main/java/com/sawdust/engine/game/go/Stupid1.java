package com.sawdust.engine.game.go;

import java.util.ArrayList;

import com.sawdust.engine.common.GameException;
import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.game.state.GameCommand;
import com.sawdust.engine.game.stop.StopGame;
import com.sawdust.engine.game.stop.StopGame.GamePhase;

public class Stupid1 extends com.sawdust.engine.game.stop.Stupid1
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
         ArrayList<GameCommand> commands = game.getCommands(player);
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
