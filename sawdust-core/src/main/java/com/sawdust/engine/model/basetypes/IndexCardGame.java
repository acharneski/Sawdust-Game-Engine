package com.sawdust.engine.model.basetypes;

import com.sawdust.engine.model.state.IndexCard;
import com.sawdust.engine.model.state.IndexPosition;
import com.sawdust.engine.view.cards.CardDeck;
import com.sawdust.engine.view.config.GameConfig;

public abstract class IndexCardGame extends PersistantTokenGame
{
    private CardDeck _deck = new CardDeck();

    protected IndexCardGame()
    {
        super();
    }

    public IndexCardGame(final GameConfig config)
    {
        super(config);
    }

    @Deprecated
    public IndexCard doDealNewCard(final IndexPosition indexPosition)
    {
        final IndexCard newCard = new IndexCard(++cardIdCounter, null, "VR", false, indexPosition, getDeck().dealNewCard());
        doAddToken(newCard);
        return newCard;
    }

    public CardDeck getDeck()
    {
        return _deck;
    }

    public IndexCardGame setDeck(final CardDeck deck)
    {
        _deck = deck;
        return this;
    }

}
