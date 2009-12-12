package com.sawdust.engine.game.euchre;

import com.sawdust.engine.common.cards.Card;
import com.sawdust.engine.common.cards.CardDeck;
import com.sawdust.engine.common.cards.Ranks;

public class EuchreDeck extends CardDeck
{

    private Ranks _filterMax = Ranks.Eight;

    public Ranks getFilterMax()
    {
        return _filterMax;
    }

    @Override
    protected boolean isCardAvailible(final Card card)
    {
        if (card.getRank().getRank() <= _filterMax.getRank()) return false;
        return super.isCardAvailible(card);
    }

    public void setFilterMax(final Ranks filterMax)
    {
        _filterMax = filterMax;
    }

}
