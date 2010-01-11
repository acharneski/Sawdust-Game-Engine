package com.sawdust.engine.model.basetypes;

import java.util.ArrayList;

import com.sawdust.engine.model.state.Token;
import com.sawdust.engine.view.config.GameConfig;

public abstract class PersistantTokenGame extends TokenGame
{
    protected ArrayList<Token> _tokens = new ArrayList<Token>();
    protected int cardIdCounter = 0;

    protected PersistantTokenGame()
    {
        super();
    }

    public PersistantTokenGame(final GameConfig config)
    {
        super(config);
    }

    protected PersistantTokenGame(PersistantTokenGame obj)
    {
        super(obj);
        _tokens = new ArrayList<Token>(obj._tokens);
        cardIdCounter = obj.cardIdCounter;
    }

    public PersistantTokenGame doAddToken(final Token state)
    {
        if (cardIdCounter < state.getId())
        {
            cardIdCounter = 1 + state.getId();
        }
        _tokens.add(state);
        return this;
    }

    public PersistantTokenGame doClearTokens()
    {
        _tokens.clear();
        return this;
    }

    public PersistantTokenGame doRemoveToken(final Token state)
    {
        final int cardId = state.getId();
        final ArrayList<Token> cardsToRemove = new ArrayList<Token>();
        for (final Token card : getTokens())
        {
            if (card.getId() == cardId)
            {
                cardsToRemove.add(card);
            }
        }
        for (final Token card : cardsToRemove)
        {
            _tokens.remove(card);
        }
        return this;
    }

    @Override
    public ArrayList<Token> getTokens() {
	    return new ArrayList<Token>(_tokens);
	}
    
}
