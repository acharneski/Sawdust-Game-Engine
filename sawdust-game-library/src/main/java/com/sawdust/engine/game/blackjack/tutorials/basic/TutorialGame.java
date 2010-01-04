package com.sawdust.engine.game.blackjack.tutorials.basic;

import com.sawdust.engine.common.cards.Ranks;
import com.sawdust.engine.common.cards.Suits;
import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.common.game.GameFrame;
import com.sawdust.engine.game.LoadedDeck;
import com.sawdust.engine.game.SessionFactory;
import com.sawdust.engine.game.basetypes.GameState;
import com.sawdust.engine.game.basetypes.TutorialGameBase;
import com.sawdust.engine.game.blackjack.BlackjackGame;
import com.sawdust.engine.game.players.Agent;
import com.sawdust.engine.game.players.Player;
import com.sawdust.engine.service.data.GameSession;
import com.sawdust.engine.service.debug.GameException;

public class TutorialGame extends TutorialGameBase<BlackjackGame>
{
    protected TutorialGame()
    {
        super();
    }
    
    @Override
    public void reset()
    {
        super.reset();
    }
    
    protected LoadedDeck _deck = null;

    @Override
    public void start() throws GameException
    {
       if(null == _deck)
       {
          _deck = new LoadedDeck();
          _deck.clearMemory();
          // First 2 player cards
          _deck.addCard(Ranks.Two, Suits.Hearts);
          _deck.addCard(Ranks.Three, Suits.Hearts);
          // Then 2 dealer cards
          _deck.addCard(Ranks.Jack, Suits.Spades);
          _deck.addCard(Ranks.Seven, Suits.Spades);
          // Then player hits 4 times!
          _deck.addCard(Ranks.Four, Suits.Hearts);
          _deck.addCard(Ranks.Five, Suits.Hearts);
          _deck.addCard(Ranks.Six, Suits.Hearts);
          _deck.addCard(Ranks.Ace, Suits.Spades);
          _phase = Phases.Card0;
       }
        
       getInnerGame().setDeck(_deck);
       super.start();
    }
    
    @Override
    public GameFrame toGwt(Player access) throws GameException
    {
        GameFrame gwt = super.toGwt(access);
        gwt = _phase.filterDisplay(gwt);
        return gwt;
    }
    
    public TutorialGame(GameConfig config, final SessionFactory sessionF)
    {
        super(new BlackjackGame(config) {

         @Override
         public GameSession getSession()
         {
            return sessionF.getSession();
         }});
    }

   @Override
   protected Agent<BlackjackGame> initAgent()
   {
      return null;
   }

   @Override
   public void setParentGame(GameState parentGame)
   {
      throw new RuntimeException("Not Implemented");
   }
    
}
