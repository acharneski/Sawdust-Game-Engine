package com.sawdust.games.poker;

import java.util.ArrayList;

import com.sawdust.engine.model.ComparableList;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.view.cards.Card;

public class PokerHand extends ComparableList<Integer>
{
    static public PokerHand GetHighest(final ArrayList<PokerHand> allHands)
    {
        PokerHand winningHand = null;
        for (final PokerHand hand : allHands)
        {
            if ((null == winningHand) || (0 > winningHand.compareTo(hand)))
            {
                winningHand = hand;
            }
        }
        return winningHand;
    }

    private ArrayList<Card> _cards = new ArrayList<Card>();

    private String _name;

    private int _odds = 1;

    private Participant _owner;

    /**
     * @param name
     */
    public PokerHand(final String name)
    {
        super();
        _name = name;
    }

    public void add(final ArrayList<Card> cards)
    {
        for (final Card primary : cards)
        {
            add(primary);
        }
    }

    public void add(final Card card)
    {
        _cards.add(card);

    }

    public ArrayList<Card> getCards()
    {
        return _cards;
    }

    public String getName()
    {
        return _name;
    }

    public int getOdds()
    {
        return _odds;
    }

    public Participant getOwner()
    {
        return _owner;
    }

    public void setCards(final ArrayList<Card> cards)
    {
        _cards = cards;
    }

    public void setName(final String name)
    {
        _name = name;
    }

    public void setOdds(final int odds)
    {
        _odds = odds;
    }

    public void setOwner(final Participant participant)
    {
        _owner = participant;
    }

}
