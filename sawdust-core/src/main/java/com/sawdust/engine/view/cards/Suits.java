/**
 * 
 */
package com.sawdust.engine.view.cards;

import com.sawdust.engine.view.GameException;

public enum Suits
{
    Clubs
    {
        @Override
        public String color()
        {
            return "Black";
        }

        @Override
        public String fullString()
        {
            return "Clubs";
        }

        @Override
        public int index()
        {
            return 2;
        }

        @Override
        public String toString()
        {
            return "C";
        }
    },
    Diamonds
    {
        @Override
        public String color()
        {
            return "Red";
        }

        @Override
        public String fullString()
        {
            return "Diamonds";
        }

        @Override
        public int index()
        {
            return 3;
        }

        @Override
        public String toString()
        {
            return "D";
        }
    },
    Hearts
    {
        @Override
        public String color()
        {
            return "Red";
        }

        @Override
        public String fullString()
        {
            return "Hearts";
        }

        @Override
        public int index()
        {
            // TODO Auto-generated method stub
            return 1;
        }

        @Override
        public String toString()
        {
            return "H";
        }
    },
    Null
    {
        @Override
        public String color()
        {
            return "Null";
        }

        @Override
        public String fullString()
        {
            return "Null";
        }

        @Override
        public int index()
        {
            return 0;
        }

        @Override
        public String toString()
        {
            return "NULL";
        }
    },
    Spades
    {
        @Override
        public String color()
        {
            return "Black";
        }

        @Override
        public String fullString()
        {
            return "Spades";
        }

        @Override
        public int index()
        {
            // TODO Auto-generated method stub
            return 4;
        }

        @Override
        public String toString()
        {
            return "S";
        }
    };
    public static Suits getSuitFromFullString(final String suitString) throws GameException
    {
        if ((null == suitString) || suitString.isEmpty()) return Null;
        for (final Suits suit : Suits.values())
        {
            if (suit.fullString().equals(suitString)) return suit;
        }
        throw new GameException("Cannot interperet suit name: " + suitString);
    }

    public abstract String color();

    public abstract String fullString();

    public abstract int index();
}
