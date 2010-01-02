package com.sawdust.engine.game;

import java.util.ArrayList;

import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.game.state.Token;

public abstract class PersistantTokenGame extends TokenGame
{
    protected ArrayList<Token> _tokens = new ArrayList<Token>();
    protected int cardIdCounter = 0;

    protected PersistantTokenGame()
    {
        super();
    }

    protected PersistantTokenGame(PersistantTokenGame obj)
    {
        super(obj);
        _tokens = new ArrayList<Token>(obj._tokens);
        cardIdCounter = obj.cardIdCounter;
    }

    public PersistantTokenGame(final GameConfig config)
    {
        super(config);
    }

    public void add(final Token state)
    {
        if (cardIdCounter < state.getId())
        {
            cardIdCounter = 1 + state.getId();
        }
        _tokens.add(state);
    }

    public void clearTokens()
    {
        _tokens.clear();
    }

    public void removeToken(final Token state)
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
    }

    @Override
    public ArrayList<Token> getTokens() {
	    return new ArrayList<Token>(_tokens);
	}
    
}
