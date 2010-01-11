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
    public LoadedDeck doClearMemory()
    {
        super.doClearMemory();
        _loadedCards.clear();
        return this;
    }

    public LoadedDeck doAddCard(Ranks ace, Suits clubs)
    {
        _loadedCards.add(new Card(ace, clubs, 0));
        return this;
    }

    @Override
    public Pair<CardDeck, Card> doDealNewCard()
    {
        if (_loadedCards.isEmpty())
        {
            Pair<CardDeck, Card> doDealNewCard = super.doDealNewCard();
            Card dealNewCard = doDealNewCard.second;
            System.out.println(String.format("deck.add(Ranks.%s, Suits.%s);", dealNewCard.getRank().name(), dealNewCard.getSuit().name()));
            return new Pair<CardDeck, Card>(this, dealNewCard);
        }
        else
        {
            Card card = _loadedCards.pop();
            _inPlay.add(card);
            return new Pair<CardDeck, Card>(this, card);
        }
    }
}
