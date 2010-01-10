/**
 * 
 */
package com.sawdust.games.stop;

import com.sawdust.engine.controller.entities.GameSession;
import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.state.GameCommand;
import com.sawdust.engine.model.state.IndexPosition;

public final class MoveCommand extends GameCommand
{
   
   private final StopGame      stopGame;
   private final String        cmd;
   private final ArrayPosition pos;
   
   public MoveCommand(StopGame stopGame, String cmd, ArrayPosition pos)
   {
      this.stopGame = stopGame;
      this.cmd = cmd;
      this.pos = pos;
   }
   
   @Override
   public String getHelpText()
   {
      return "Place your piece on the given location";
   }
   
   @Override
   public String getCommandText()
   {
      return cmd;
   }
   
   @Override
   public boolean doCommand(Participant p, String commandText) throws GameException
   {
      final IndexPosition position = new IndexPosition(pos.row, pos.col);
      this.stopGame.doMove(position, p);
      this.stopGame.saveState();
      return true;
   }
   
   public ArrayPosition getPosition()
   {
      return pos;
   }
}