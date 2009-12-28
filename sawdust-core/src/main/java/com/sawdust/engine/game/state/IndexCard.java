package com.sawdust.engine.game.state;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.sawdust.engine.common.cards.Card;
import com.sawdust.engine.game.players.Player;

public class IndexCard extends Token
{
    private Card _card;
    
    protected IndexCard()
    {
        super();
    }

    public IndexCard(final int id, final Player owner, final String isPublic, final boolean movable, final IndexPosition position, final Card card)
    {
        super(id, "CARD1", (null==card)?null:card.CardId(), owner, isPublic, movable, position);
        if(null != card) setText(card.toString());
        _card = card;
    }

    public Card getCard()
    {
        return _card;
    }

    public void setCard(final Card card)
    {
        _card = card;
    }
}
