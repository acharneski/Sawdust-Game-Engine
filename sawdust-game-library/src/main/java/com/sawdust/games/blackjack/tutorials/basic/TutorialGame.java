package com.sawdust.games.blackjack.tutorials.basic;

import com.sawdust.engine.controller.entities.GameSession;
import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.LoadedDeck;
import com.sawdust.engine.model.SessionFactory;
import com.sawdust.engine.model.basetypes.GameState;
import com.sawdust.engine.model.basetypes.TutorialGameBase;
import com.sawdust.engine.model.players.Agent;
import com.sawdust.engine.model.players.Player;
import com.sawdust.engine.view.cards.Ranks;
import com.sawdust.engine.view.cards.Suits;
import com.sawdust.engine.view.config.GameConfig;
import com.sawdust.engine.view.game.GameFrame;
import com.sawdust.games.blackjack.BlackjackGame;

public class TutorialGame extends TutorialGameBase<BlackjackGame>
{
    protected TutorialGame()
    {
        super();
    }
    
    @Override
    public TutorialGame doReset()
    {
        return (TutorialGame) super.doReset();
    }
    
    protected LoadedDeck _deck = null;

    @Override
    public TutorialGameBase<BlackjackGame> doStart() throws GameException
    {
       if(null == _deck)
       {
          _deck = new LoadedDeck();
          _deck.doClearMemory();
          // First 2 player cards
          _deck.doAddCard(Ranks.Two, Suits.Hearts);
          _deck.doAddCard(Ranks.Three, Suits.Hearts);
          // Then 2 dealer cards
          _deck.doAddCard(Ranks.Jack, Suits.Spades);
          _deck.doAddCard(Ranks.Seven, Suits.Spades);
          // Then player hits 4 times!
          _deck.doAddCard(Ranks.Four, Suits.Hearts);
          _deck.doAddCard(Ranks.Five, Suits.Hearts);
          _deck.doAddCard(Ranks.Six, Suits.Hearts);
          _deck.doAddCard(Ranks.Ace, Suits.Spades);
          _phase = Phases.Card0;
       }
        
       getInnerGame().setDeck(_deck);
       return super.doStart();
    }
    
    @Override
    public GameFrame getView(Player access) throws GameException
    {
        GameFrame gwt = super.getView(access);
        gwt = _phase.getFilteredDisplay(gwt);
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
   protected Agent<BlackjackGame> getInitAgent()
   {
      return null;
   }

   @Override
   public TutorialGameBase<BlackjackGame> setParentGame(GameState parentGame)
   {
      throw new RuntimeException("Not Implemented");
   }

@Override
public boolean isIntermediateState()
{
    // TODO Auto-generated method stub
    return false;
}
    
}
