package com.sawdust.engine.model.basetypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.players.Agent;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.players.Player;
import com.sawdust.engine.model.state.GameLabel;
import com.sawdust.engine.model.state.IndexPosition;
import com.sawdust.engine.model.state.Token;
import com.sawdust.engine.view.config.GameConfig;
import com.sawdust.engine.view.game.GameFrame;
import com.sawdust.engine.view.geometry.Position;

public abstract class TokenGame extends BaseGame {

    protected HashMap<Participant, String> _displayFilter = new HashMap<Participant, String>();
    
	public TokenGame() {
		super();
	}

	public TokenGame(BaseGame obj) {
		super(obj);
	}

	public TokenGame(GameConfig config) {
		super(config);
	}

	public ArrayList<Token> getCurveCards(final int curve) {
	    final ArrayList<Token> returnValue = new ArrayList<Token>();
	    for (final Token card : getTokens())
	    {
	        if (card.getPosition().getCurveIndex() == curve)
	        {
	            returnValue.add(card);
	        }
	    }
	    return returnValue;
	}

	public abstract Collection<GameLabel> getLabels(Player access) throws GameException;

	public abstract Position getPosition(IndexPosition key, Player access)
			throws GameException;

	public Token getToken(final IndexPosition p) {
	    for (final Token card : getTokens())
	    {
	        if (card.getPosition().equals(p)) return card;
	    }
	    return null;
	}

	public HashMap<String, Token> getTokenIndexByArt() {
	    final HashMap<String, Token> cardIndex = new HashMap<String, Token>();
	    for (final Token card : getTokens())
	    {
	        final String cardId = card.getArt();
	        cardIndex.put(cardId, card);
	    }
	    return cardIndex;
	}

	public HashMap<Integer, Token> getTokenIndexById() {
	    final HashMap<Integer, Token> cardIndex = new HashMap<Integer, Token>();
	    for (final Token card : getTokens())
	    {
	        cardIndex.put(card.getId(), card);
	    }
	    return cardIndex;
	}

	public HashMap<IndexPosition, Token> getTokenIndexByPosition() {
	    final HashMap<IndexPosition, Token> cardIndex = new HashMap<IndexPosition, Token>();
	    for (final Token card : getTokens())
	    {
	        final IndexPosition cardId = card.getPosition();
	        cardIndex.put(cardId, card);
	    }
	    return cardIndex;
	}

	/**
	 * @return the absolutePositionTokens
	 * @throws GameException 
	 */
	public abstract ArrayList<Token> getTokens();

	@Override
	public GameFrame toGwt(final Player access) throws GameException {
	    final GameFrame returnValue = super.toGwt(access);
	    for (final Token t : getTokens())
	    {
	        final com.sawdust.engine.view.game.Token g = t.toGwt(access, this);
	        if (null != g)
	        {
	            returnValue.add(g);
	        }
	    }
	    for (final GameLabel s : getLabels(access))
	    {
	        returnValue.add(s.toGwt(access, this));
	    }
	    return returnValue;
	}

    @Override
    public void addPlayer(final Participant agent) throws GameException
    {
        if (agent instanceof Player)
        {
            _displayFilter.put(agent, ((Player) agent).loadAccount().getName());
        }
        else
        {
            String id = ((Agent<?>) agent).getId();
            _displayFilter.put(agent, id);
        }
        super.addPlayer(agent);
    }

    @Override
    public String getDisplayName(final Participant participant)
    {
        if (null == participant) return null;
        if (!_displayFilter.containsKey(participant)) return participant.getId();
        return _displayFilter.get(participant);
    }
}