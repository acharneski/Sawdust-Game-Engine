/**
 * 
 */
package com.sawdust.engine.game.stop;

import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.game.state.GameCommand;
import com.sawdust.engine.game.state.IndexPosition;
import com.sawdust.engine.service.data.GameSession;
import com.sawdust.engine.service.debug.GameException;

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
   public boolean doCommand(Participant p) throws com.sawdust.engine.common.GameException
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