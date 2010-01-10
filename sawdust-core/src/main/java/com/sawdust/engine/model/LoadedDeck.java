package com.sawdust.engine.model;

import java.util.LinkedList;

import com.sawdust.engine.view.cards.Card;
import com.sawdust.engine.view.cards.CardDeck;
import com.sawdust.engine.view.cards.Ranks;
import com.sawdust.engine.view.cards.Suits;

public class LoadedDeck extends CardDeck
{
	private LinkedList<Card> _loadedCards = new LinkedList<Card>();

	public LoadedDeck()
	{
		super();
	}

	@Override
   public void clearMemory()
   {
      super.clearMemory();
      _loadedCards.clear();
   }

   public void addCard(Ranks ace, Suits clubs)
	{
		_loadedCards.add(new Card(ace, clubs, 0));
	}

	@Override
	public Card dealNewCard()
	{
		if(_loadedCards.isEmpty())
		{
			Card dealNewCard = super.dealNewCard();
			System.out.println(String.format("deck.add(Ranks.%s, Suits.%s);", 
					dealNewCard.getRank().name(), 
					dealNewCard.getSuit().name()));
			return dealNewCard;
		}
		else
		{
			Card card = _loadedCards.pop();
			_inPlay.add(card);
			return card;
		}
	}
}
