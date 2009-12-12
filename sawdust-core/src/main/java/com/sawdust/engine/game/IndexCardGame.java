package com.sawdust.engine.game;

import com.sawdust.engine.common.cards.CardDeck;
import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.game.state.IndexCard;
import com.sawdust.engine.game.state.IndexPosition;

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

    public IndexCard dealNewCard(final IndexPosition indexPosition)
    {
        final IndexCard newCard = new IndexCard(++cardIdCounter, null, "VR", false, indexPosition, getDeck().dealNewCard());
        add(newCard);
        return newCard;
    }

    public CardDeck getDeck()
    {
        return _deck;
    }

    public void setDeck(final CardDeck deck)
    {
        _deck = deck;
    }

}
