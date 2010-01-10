package com.sawdust.engine.model.state;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;

import com.sawdust.engine.model.players.Player;
import com.sawdust.engine.view.cards.Card;

public class IndexCard extends Token
{
    private Card _card;
    
    protected static class SerialForm extends Token.SerialForm
    {
        Card _card;
        
        protected SerialForm(){}
        protected SerialForm(IndexCard obj)
        {
            super(obj);
            _card = obj._card;
        }
        private Object readResolve()
        {
            return new IndexCard(this);
        }
    }
    
    private Object writeReplace()
    {
        return new SerialForm(this);
    }
    
    private void readObject(ObjectInputStream s) throws  IOException, ClassNotFoundException
    {
        throw new NotSerializableException();
    }

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

    public IndexCard(com.sawdust.engine.model.state.IndexCard.SerialForm serialForm)
    {
        super(serialForm);
        _card = serialForm._card;
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
