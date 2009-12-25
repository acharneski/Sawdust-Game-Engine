package com.sawdust.engine.common.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.common.geometry.Position;

public class GameState implements Serializable
{

    private ArrayList<Token> _cardDeck = new ArrayList<Token>();
    private int _cardIdCounter = 0;
    private ArrayList<ClientCommand> _commands = new ArrayList<ClientCommand>();
    private GameConfig _config = null;
    private int _height = 400;
    private ArrayList<GameLabel> _labels = new ArrayList<GameLabel>();
    private ArrayList<Message> _newMessages = new ArrayList<Message>();
    private Notification _notification = null;
    private int _width = 600;
    public int timeOffset = 0;
    public int updateTime = 5;
    public int versionNumber = 0;
    public String html = "";

    public GameState()
    {
    }

    public GameState(final GameConfig g)
    {
        setConfig(g);
    }

    public void add(final GameLabel gwt)
    {
        _labels.add(gwt);
    }

    public void add(final Token token)
    {
        if (_cardIdCounter < token.getId())
        {
            _cardIdCounter = 1 + token.getId();
        }
        _cardDeck.add(token);
    };

    public void addMessage(final Message s)
    {
        if (null != s)
        {
            _newMessages.add(s);
        }
    };

    public void clearMessages()
    {
        if (null != _newMessages)
        {
            _newMessages.clear();
        }
    }

    public void clearTokens()
    {
        _cardDeck.clear();
    }

    public ArrayList<ClientCommand> getCommands()
    {
        return _commands;
    }

    public GameConfig getConfig()
    {
        return _config;
    }

    public int getHeight()
    {
        return _height;
    }

    public List<GameLabel> getLabels()
    {
        // TODO Auto-generated method stub
        return _labels;
    }

    /**
     * @return the newMessages
     */
    public int getLastMessageID()
    {
        int max = -1;
        for (final Message m : _newMessages)
        {
            if (m.getId() > max)
            {
                max = m.getId();
            }
        }
        return max;
    }

    /**
     * @return the newMessages
     */
    public Date getLastUpdatedTime()
    {
        Date maxTime = new Date(0);
        for (final Message m : _newMessages)
        {
            if (m.getDateTime().after(maxTime))
            {
                maxTime = m.getDateTime();
            }
        }
        return maxTime;
    }

    /**
     * @return the newMessages
     */
    public List<Message> getMessagesSince(final Date since)
    {
        final ArrayList<Message> returnValue = new ArrayList<Message>();
        for (final Message m : _newMessages)
        {
            if (m.getDateTime().after(since))
            {
                returnValue.add(m);
            }
        }
        return returnValue;
    }

    /**
     * @return the newMessages
     */
    public List<Message> getMessagesSince(final int since)
    {
        final ArrayList<Message> returnValue = new ArrayList<Message>();
        for (final Message m : _newMessages)
        {
            if (m.getId() > since)
            {
                returnValue.add(m);
            }
        }
        return returnValue;
    }

    public Token getTokenByPosition(final Position s, final double tolerance)
    {
        final double x = s.getX();
        final double y = s.getY();
        double winningDistance = -1;
        Token winningCard = null;
        for (final Token card : getTokens())
        {
            final double d = card.getDistance(x, y);
            if ((winningDistance < 0) || (winningDistance > d))
            {
                winningDistance = d;
                winningCard = card;
            }
        }
        return (winningDistance < tolerance) ? winningCard : null;
    }

    /**
     * @return
     */
    public HashMap<Integer, Token> getTokenIndexById()
    {
        final HashMap<Integer, Token> cardIndex = new HashMap<Integer, Token>();
        for (final Token card : getTokens())
        {
            cardIndex.put(card.getId(), card);
        }
        return cardIndex;
    }

    /**
     * @return the absolutePositionTokens
     */
    public List<Token> getTokens()
    {
        return _cardDeck;
    }

    public int getWidth()
    {
        return _width;
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
            _cardDeck.remove(card);
        }
    }

    public void setCommands(final ArrayList<ClientCommand> commands)
    {
        _commands = commands;
    }

    public void setConfig(final GameConfig config)
    {
        _config = config;
    }

    public void setHeight(final int height)
    {
        _height = height;
    }

    public void setWidth(final int width)
    {
        _width = width;
    }

    private void setLabels(ArrayList<GameLabel> plabels)
    {
        this._labels = plabels;
    }

    private void setMessages(ArrayList<Message> pnewMessages)
    {
        this._newMessages = pnewMessages;
    }

    private ArrayList<Message> getMessages()
    {
        return _newMessages;
    }

    private void setCardDeck(ArrayList<Token> pcardDeck)
    {
        this._cardDeck = pcardDeck;
    }

    private ArrayList<Token> getCardDeck()
    {
        return _cardDeck;
    }

    public void setNotification(Notification _notification)
    {
        this._notification = _notification;
    }

    public Notification getNotification()
    {
        return _notification;
    }
}
