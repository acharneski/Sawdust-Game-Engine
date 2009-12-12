package com.sawdust.engine.common.cards;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

public class CardDeck implements Serializable
{
   protected HashSet<Card>  _discards         = new HashSet<Card>();
   
   protected HashSet<Card>  _inPlay           = new HashSet<Card>();
   private static final int _numberOfDecks    = 1;
   
   private boolean          _reshuffleEnabled = true;
   
   public String            _seed             = null;
   
   public int               randomIndex       = 0;
   
   /**
	 * 
	 */
   public CardDeck()
   {
      super();
   }
   
   public void clearMemory()
   {
      _inPlay.clear();
      _discards.clear();
   }
   
   public Card dealNewCard()
   {
      final ArrayList<Card> cardsLeft = getAvailibleCards();
      final Card randomCard = (Card) randomMember(cardsLeft.toArray());
      if (null == randomCard)
      {
         if (_reshuffleEnabled && !_discards.isEmpty())
         {
            _discards.clear();
            return dealNewCard();
         }
         else
         {
            return null;
         }
      }
      _inPlay.add(randomCard);
      return randomCard;
   }
   
   private ArrayList<Card> getAvailibleCards()
   {
      final ArrayList<Card> cardsLeft = new ArrayList<Card>();
      for (int deckN = 0; deckN < _numberOfDecks; deckN++)
      {
         for (final Ranks rank : Ranks.values())
         {
            for (final Suits suit : Suits.values())
            {
               if (suit == Suits.Null)
               {
                  continue;
               }
               final Card c = new Card(rank, suit, deckN);
               if (isCardAvailible(c))
               {
                  cardsLeft.add(c);
               }
            }
         }
      }
      return cardsLeft;
   }
   
   protected int getRandom(final int maxValue)
   {
      // This uses string's hash code to determine pseudorandom output.
      // Very unsafe and unrandom. However, this is only for testing under admin
      // mode.
      // If ever used for general use, this should be improved to use a stronger
      // and
      // more randomly distributed hash function.
      if (null == _seed) return (int) Math.floor(Math.random() * maxValue);
      return Math.abs((_seed + randomIndex++).hashCode() % maxValue);
   }
   
   protected boolean isCardAvailible(final Card card)
   {
      if (card.getRank() == Ranks.Null) return false;
      if (card.getSuit() == Suits.Null) return false;
      if (_discards.contains(card)) return false;
      if (_inPlay.contains(card)) return false;
      return true;
   }
   
   public boolean isReshuffleEnabled()
   {
      return _reshuffleEnabled;
   }
   
   /**
	 * 
	 */
   protected <T> T randomMember(final T[] values)
   {
      if (0 == values.length) return null;
      final int i = getRandom(values.length);
      return values[i];
   }
   
   public void removeCardFromMemory(final Card card)
   {
      _inPlay.remove(card);
      _discards.add(card);
   }
   
   public void setReshuffleEnabled(final boolean preshuffleEnabled)
   {
      _reshuffleEnabled = preshuffleEnabled;
   }
   
   public void setSeed(final String value)
   {
      _seed = value;
   }
   
   public String getSeed()
   {
      return _seed;
   }
}
