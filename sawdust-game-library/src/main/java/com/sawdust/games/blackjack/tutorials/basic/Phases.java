package com.sawdust.games.blackjack.tutorials.basic;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.controller.exceptions.GameLogicException;
import com.sawdust.engine.model.TutorialPhase;
import com.sawdust.engine.model.basetypes.TutorialGameBase;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.state.GameCommand;
import com.sawdust.engine.view.cards.Ranks;
import com.sawdust.engine.view.cards.Suits;
import com.sawdust.engine.view.game.GameFrame;
import com.sawdust.engine.view.game.Notification;
import com.sawdust.games.blackjack.BlackjackGame;

public enum Phases implements TutorialPhase<BlackjackGame>
{
   Null, Card0
   {
      
      @Override
      public TutorialPhase<BlackjackGame> doOnPreCommand(TutorialGameBase<BlackjackGame> game, GameCommand m, Participant p) throws GameLogicException
      {
         if (m.getCommandText().equals("Hit 0"))
         {
            return Card1;
         }
         else
         {
            throw new GameLogicException("Please press 'Hit Me' to take another card.");
         }
      }
      
      @Override
      public GameFrame getFilteredDisplay(GameFrame gwt)
      {
         Notification notification = new Notification();
         notification.notifyText = "Welcome to Blackjack! Please draw another card by using the \"Hit Me\" command.";
         notification.commands.put("Hit 0", "Hit Me");
         gwt.setNotification(notification);
         return gwt;
      }
      
   },
   Card1
   {
      
      @Override
      public TutorialPhase<BlackjackGame> doOnPreCommand(TutorialGameBase<BlackjackGame> game, GameCommand m, Participant p) throws GameLogicException
      {
         if (m.getCommandText().equals("Hit 0"))
         {
            return Card2;
         }
         else
         {
            throw new GameLogicException("Please press 'Hit Me' to take another card.");
         }
      }
      
      @Override
      public GameFrame getFilteredDisplay(GameFrame gwt)
      {
         Notification notification = new Notification();
         notification.notifyText = "The goal of this game is to get a higher score than the dealer, without going over 21.";
         notification.commands.put("Hit 0", "Hit Me");
         gwt.setNotification(notification);
         return gwt;
      }
      
   },
   Card2
   {
      
      @Override
      public TutorialPhase<BlackjackGame> doOnPreCommand(TutorialGameBase<BlackjackGame> game, GameCommand m, Participant p) throws GameLogicException
      {
         if (m.getCommandText().equals("Hit 0"))
         {
            return Card3;
         }
         else
         {
            throw new GameLogicException("Please press 'Hit Me' to take another card.");
         }
      }
      
      @Override
      public GameFrame getFilteredDisplay(GameFrame gwt)
      {
         Notification notification = new Notification();
         notification.notifyText = "Press 'Hit Me' as needed to deal cards, and then 'Stay'";
         notification.commands.put("Hit 0", "Hit Me");
         gwt.setNotification(notification);
         return gwt;
      }
      
   },
   Card3
   {
      
      @Override
      public TutorialPhase<BlackjackGame> doOnPreCommand(TutorialGameBase<BlackjackGame> game, GameCommand m, Participant p) throws GameLogicException
      {
         if (m.getCommandText().equals("Stay"))
         {
            return this;
         }
         else
         {
            throw new GameLogicException("Please press 'Hit Me' to take another card.");
         }
      }
      
      @Override
      public TutorialPhase<BlackjackGame> doOnPostCommand(TutorialGameBase<BlackjackGame> game, GameCommand m, Participant p) throws GameLogicException
      {
         if (m.getCommandText().equals("Stay"))
         {
            return Double1;
         }
         else
         {
            throw new GameLogicException("Please press 'Hit Me' to take another card.");
         }
      }
      
      @Override
      public GameFrame getFilteredDisplay(GameFrame gwt)
      {
         Notification notification = new Notification();
         notification.notifyText = "Royal cards are worth 10, aces are worth either 11 or 1, and number cards are face value. It would be a good idea to stay now!";
         notification.commands.put("Stay", "Stay");
         gwt.setNotification(notification);
         return gwt;
      }
   },
   Double1
   {
      
      @Override
      public void doOnStartPhase(TutorialGameBase<BlackjackGame> game) throws GameException
      {
         super.doOnStartPhase(game);
         ((TutorialGame) game)._deck.doClearMemory();
         
         // First 2 player cards
         ((TutorialGame) game)._deck.doAddCard(Ranks.Ace, Suits.Spades);
         ((TutorialGame) game)._deck.doAddCard(Ranks.Ten, Suits.Spades);
         
         // Then 2 dealer cards
         ((TutorialGame) game)._deck.doAddCard(Ranks.Ten, Suits.Spades);
         ((TutorialGame) game)._deck.doAddCard(Ranks.Ten, Suits.Spades);
         
         // Then the next player cards
         ((TutorialGame) game)._deck.doAddCard(Ranks.Ten, Suits.Spades);
         
         game.doStart();
      }
      
      @Override
      public TutorialPhase<BlackjackGame> doOnPreCommand(TutorialGameBase<BlackjackGame> game, GameCommand m, Participant p) throws GameLogicException
      {
         if (m.getCommandText().equals("Double Down"))
         {
            return this;
         }
         else
         {
            throw new GameLogicException("Please press 'Double Down' to take another card.");
         }
      }
      
      @Override
      public TutorialPhase<BlackjackGame> doOnPostCommand(TutorialGameBase<BlackjackGame> game, GameCommand m, Participant p) throws GameLogicException
      {
         if (m.getCommandText().equals("Double Down"))
         {
            return Split1;
         }
         else
         {
            throw new GameLogicException("Please press 'Double Down' to take another card.");
         }
      }
      
      @Override
      public GameFrame getFilteredDisplay(GameFrame gwt)
      {
         Notification notification = new Notification();
         notification.notifyText = "At the start of each hand, the player has the option of 'Doubling Down', where the bet is doubled in exchange for recieving exactly 1 more card.";
         notification.commands.put("Double Down", "Double Down");
         gwt.setNotification(notification);
         return gwt;
      }
   },
   Split1
   {
      
      @Override
      public void doOnStartPhase(TutorialGameBase<BlackjackGame> game) throws GameException
      {
         super.doOnStartPhase(game);
         ((TutorialGame) game)._deck.doClearMemory();
         
         // First 2 player cards
         ((TutorialGame) game)._deck.doAddCard(Ranks.Ace, Suits.Spades);
         ((TutorialGame) game)._deck.doAddCard(Ranks.Ace, Suits.Spades);
         
         // Then 2 dealer cards
         ((TutorialGame) game)._deck.doAddCard(Ranks.Seven, Suits.Spades);
         ((TutorialGame) game)._deck.doAddCard(Ranks.Ten, Suits.Spades);
         
         // Then the next player cards
         ((TutorialGame) game)._deck.doAddCard(Ranks.Ten, Suits.Spades);
         ((TutorialGame) game)._deck.doAddCard(Ranks.Ten, Suits.Spades);

         game.doStart();
      }
      
      @Override
      public TutorialPhase<BlackjackGame> doOnPreCommand(TutorialGameBase<BlackjackGame> game, GameCommand m, Participant p) throws GameLogicException
      {
         if (m.getCommandText().equals("Split Pair"))
         {
            return this;
         }
         else
         {
            throw new GameLogicException("Please press 'Split Pair' to take another card.");
         }
      }
      
      @Override
      public TutorialPhase<BlackjackGame> doOnPostCommand(TutorialGameBase<BlackjackGame> game, GameCommand m, Participant p) throws GameLogicException
      {
         if (m.getCommandText().equals("Split Pair"))
         {
            return Null;
         }
         else
         {
            throw new GameLogicException("Please press 'Split Pair' to take another card.");
         }
      }
      
      @Override
      public GameFrame getFilteredDisplay(GameFrame gwt)
      {
         Notification notification = new Notification();
         notification.notifyText = "If you are dealt a pair, you also have the option to 'Split' where each card is used to start a new hand, which are played simultaneously against the dealer. The bet is doubled, with identical bets riding on each hand.";
         notification.commands.put("Split Pair", "Split Pair");
         gwt.setNotification(notification);
         return gwt;
      }
   };
   
   @Override
   public boolean getAllowCommand(TutorialGameBase<BlackjackGame> game, GameCommand m)
   {
      return true;
   }
   
   @Override
   public GameFrame getFilteredDisplay(GameFrame gwt)
   {
      return gwt;
   }
   
   @Override
   public TutorialPhase<BlackjackGame> doOnPreCommand(TutorialGameBase<BlackjackGame> game, GameCommand m, Participant p)
         throws GameLogicException
   {
      return this;
   }
   
   @Override
   public TutorialPhase<BlackjackGame> doOnPostCommand(TutorialGameBase<BlackjackGame> game, GameCommand m, Participant p)
         throws GameLogicException
   {
      return null;
   }
   
   @Override
   public void doOnStartPhase(TutorialGameBase<BlackjackGame> game) throws GameException
   {
   }
   
}
