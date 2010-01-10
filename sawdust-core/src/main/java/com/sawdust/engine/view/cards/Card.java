package com.sawdust.engine.view.cards;

import java.io.Serializable;

public class Card implements Serializable
{
    private int deckNumber = 0;
    private Ranks rank = Ranks.Two;
    private SpecialCard special = SpecialCard.Null;
    private Suits suit = Suits.Spades;

    private Card()
    {
    }

    public Card(final Ranks rankP, final Suits suitP, final int deckN)
    {
        rank = rankP;
        suit = suitP;
        deckNumber = deckN;
    }

    public Card(final SpecialCard specialP)
    {
        special = specialP;
    }

    public String CardId()
    {
        if (null == rank) return null;
        if (null == suit) return null;
        return rank.toString() + suit.toString();
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final Card other = (Card) obj;
        if (deckNumber != other.deckNumber) return false;
        if (rank == null)
        {
            if (other.rank != null) return false;
        }
        else if (!rank.equals(other.rank)) return false;
        if (special == null)
        {
            if (other.special != null) return false;
        }
        else if (!special.equals(other.special)) return false;
        if (suit == null)
        {
            if (other.suit != null) return false;
        }
        else if (!suit.equals(other.suit)) return false;
        return true;
    }

    public int getDeckNumber()
    {
        return deckNumber;
    }

    public Ranks getRank()
    {
        return rank;
    }

    public SpecialCard getSpecial()
    {
        return special;
    }

    public Suits getSuit()
    {
        return suit;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + deckNumber;
        result = prime * result + ((rank == null) ? 0 : rank.hashCode());
        result = prime * result + ((special == null) ? 0 : special.hashCode());
        result = prime * result + ((suit == null) ? 0 : suit.hashCode());
        return result;
    }

    @Override
    public String toString()
    {
        return rank.name() + " of " + suit.fullString();
    }

    public void setRank(Ranks hackRank)
    {
        rank = hackRank;
        
    }
}
