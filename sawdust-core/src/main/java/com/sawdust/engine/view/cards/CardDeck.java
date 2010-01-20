package com.sawdust.engine.view.cards;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

public class CardDeck implements Serializable
{
    private static final int _numberOfDecks = 1;
    
    protected final HashSet<Card> _inPlay = new HashSet<Card>();
    protected final HashSet<Card> _discards = new HashSet<Card>();

    private boolean _reshuffleEnabled = true;
    public String _seed = null;
    public int randomIndex = 0;
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (_reshuffleEnabled ? 1231 : 1237);
        result = prime * result + ((_seed == null) ? 0 : _seed.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        CardDeck other = (CardDeck) obj;
        if (_reshuffleEnabled != other._reshuffleEnabled) return false;
        if (_seed == null)
        {
            if (other._seed != null) return false;
        }
        else if (!_seed.equals(other._seed)) return false;
        return true;
    }

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
        // Very unsafe and unrandom. However, this is only for testing under
        // admin
        // mode.
        // If ever used for general use, this should be improved to use a
        // stronger
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

    protected <T> T randomMember(final T[] values)
    {
        if (0 == values.length) return null;
        final int i = getRandom(values.length);
        return values[i];
    }

    public void discard(final Card card)
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
