/**
 * 
 */
package com.sawdust.server.logic;

import com.sawdust.engine.game.Game;
import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.game.players.Player;
import com.sawdust.engine.game.state.GameCommand;
import com.sawdust.engine.service.data.SessionToken;
import com.sawdust.engine.service.data.GameSession.SessionStatus;
import com.sawdust.engine.service.debug.GameException;
import com.sawdust.engine.service.debug.GameLogicException;
import com.sawdust.server.datastore.entities.GameSession;
import com.sawdust.server.datastore.entities.SessionMember;
import com.sawdust.server.datastore.entities.SessionMember.MemberStatus;

public enum SessionManagementCommands
{
   Deal
   {
      
      public void doCommand(final Player user, final com.sawdust.engine.service.data.GameSession gameSession, final String param)
            throws com.sawdust.engine.common.GameException
      {
         com.sawdust.engine.service.data.SessionMember sessionOwner = gameSession.getOwner();
         if (null == sessionOwner) throw new GameLogicException("The owner has disconnected from the game");
         final Player owner = sessionOwner.getPlayer();
         if (owner.equals(user))
         {
            gameSession.start(null);
         }
         else
         {
            final Game baseGame = gameSession.getLatestState();
            throw new GameLogicException("Command restricted to owner: " + baseGame.displayName(owner));
         }
      }
      
      public String getCommandText()
      {
         return "Deal";
      }
      
      public String getHelpText()
      {
         return "Deals another round of the game.";
      }
   },
   InviteOpenPlayers
   {
      
      public void doCommand(final Player user, final com.sawdust.engine.service.data.GameSession gameSession, final String param)
            throws GameException
      {
         final Game baseGame = ((SessionToken) user).loadSession().getLatestState();
         
         ((GameSession) gameSession).createOpenInvite(user);
         baseGame.addMessage("Created public game listing at the request of %s", baseGame.displayName(user));
         baseGame.saveState();
      }
      
      public String getCommandText()
      {
         return "Create Open Listing";
      }
      
      public String getHelpText()
      {
         return "Adds this game to the public game listing.";
      }
      
   },
   JoinTable
   {
      
      public void doCommand(final Player user, final com.sawdust.engine.service.data.GameSession gameSession, final String param)
            throws GameException
      {
         gameSession.addPlayer(user);
      }
      
      public String getCommandText()
      {
         return "Join Table";
      }
      
      public String getHelpText()
      {
         return "Adds the player to the table.";
      }
      
   },
   Leave
   {
      
      public void doCommand(final Player user, final com.sawdust.engine.service.data.GameSession gameSession, final String param)
            throws GameException
      {
         final com.sawdust.engine.service.data.Account account = user.loadAccount();
         account.removeSession(gameSession);
         final SessionMember member = ((GameSession) gameSession).findMember(user);
         member.setMemberStatus(MemberStatus.Quit);
         final Game baseGame = gameSession.getLatestState();
         baseGame.addMessage("%s leaves the game.", baseGame.displayName(user));
         baseGame.saveState();
      }
      
      public String getCommandText()
      {
         return "Leave Table";
      }
      
      public String getHelpText()
      {
         return "Removes the player to the table.";
      }
      
   },
   Lobby
   {
      
      public void doCommand(final Player user, final com.sawdust.engine.service.data.GameSession gameSession, final String param)
            throws GameException
      {
         final Player owner = ((com.sawdust.engine.service.data.GameSession) gameSession).getOwner().getPlayer();
         final Game baseGame = gameSession.getLatestState();
         if (owner.equals(user))
         {
            gameSession.setSessionStatus(SessionStatus.Inviting, baseGame);
         }
         else throw new GameLogicException("Command restricted to owner: " + baseGame.displayName(owner));
         baseGame.reset();
         baseGame.saveState();
         
      }
      
      public String getCommandText()
      {
         return "Lobby Mode";
      }
      
      public String getHelpText()
      {
         return "Returns the game to 'Lobby Mode' where players can the game.";
      }
   },
   Say
   {
      
      public void doCommand(final Player user, final com.sawdust.engine.service.data.GameSession gameSession, String param)
            throws GameException
      {
         param = param.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
         final Game baseGame = gameSession.getLatestState();
         com.sawdust.engine.service.data.SessionMember owner = ((com.sawdust.engine.service.data.GameSession) gameSession).getOwner();
         Player player = (null == owner) ? null : owner.getPlayer();
         if (null == player || player.equals(user))
         {
            baseGame.addMessage("%s (game owner) says \"%s\"", baseGame.displayName(user), param);
            baseGame.saveState();
         }
         else
         {
            baseGame.addMessage("%s says \"%s\"", baseGame.displayName(user), param);
            baseGame.saveState();
         }
      }
      
      public String getCommandText()
      {
         return "Say";
      }
      
      public String getHelpText()
      {
         return "Post a message to the game";
      }
   },
   Util_Update
   {
      public void doCommand(final Player user, final com.sawdust.engine.service.data.GameSession game, final String param)
            throws com.sawdust.engine.common.GameException
      {
         final Game state = game.getLatestState();
         state.update();
         state.saveState();
         
         SessionMember findMember = ((GameSession) game).findMember(user.getId());
         if (null != findMember)
         {
            findMember.setLastUpdate();
         }
         game.updateStatus();
      }
      
      public String getCommandText()
      {
         return "Update";
      }
      
      public String getHelpText()
      {
         return "Forces an update to be pulled from the server.";
      }
      
   };
   
   public abstract void doCommand(final Player user, final com.sawdust.engine.service.data.GameSession game, final String param)
         throws GameException, com.sawdust.engine.common.GameException;
   
   public abstract String getCommandText();
   
   public abstract String getHelpText();
   
   public GameCommand getGameCommand(final Game game)
   {
      return new GameCommand()
      {
         
         @Override
         public String getHelpText()
         {
            return SessionManagementCommands.this.getHelpText();
         }
         
         @Override
         public String getCommandText()
         {
            return SessionManagementCommands.this.getCommandText();
         }
         
         @Override
         public boolean doCommand(Participant p, String commandText) throws com.sawdust.engine.common.GameException
         {
            com.sawdust.engine.service.data.GameSession session = game.getSession();
            SessionManagementCommands.this.doCommand((Player) p, session, "");
            return true;
         }
      };
   }
}
