/**
 * 
 */
package com.sawdust.engine.view.cards;

public enum Ranks implements Rank
{
    Ace
    {
        public int getOrder()
        {
            return 14;
        }

        public int getRank()
        {
            return 11;
        }

        @Override
        public String toString()
        {
            return "A";
        }
    },
    Eight
    {
        public int getOrder()
        {
            return 8;
        }

        public int getRank()
        {
            return 8;
        }

        @Override
        public String toString()
        {
            return "8";
        }
    },
    Five
    {
        public int getOrder()
        {
            return 5;
        }

        public int getRank()
        {
            return 5;
        }

        @Override
        public String toString()
        {
            return "5";
        }
    },
    Four
    {
        public int getOrder()
        {
            return 4;
        }

        public int getRank()
        {
            return 4;
        }

        @Override
        public String toString()
        {
            return "4";
        }
    },
    Jack
    {
        public int getOrder()
        {
            return 11;
        }

        public int getRank()
        {
            return 10;
        }

        @Override
        public String toString()
        {
            return "J";
        }
    },
    King
    {
        public int getOrder()
        {
            return 13;
        }

        public int getRank()
        {
            return 10;
        }

        @Override
        public String toString()
        {
            return "K";
        }
    },
    Nine
    {
        public int getOrder()
        {
            return 9;
        }

        public int getRank()
        {
            return 9;
        }

        @Override
        public String toString()
        {
            return "9";
        }
    },
    Null
    {
        public int getOrder()
        {
            return 0;
        }

        public int getRank()
        {
            return 0;
        }

        @Override
        public String toString()
        {
            return "NULL";
        }
    },
    Queen
    {
        public int getOrder()
        {
            return 12;
        }

        public int getRank()
        {
            return 10;
        }

        @Override
        public String toString()
        {
            return "Q";
        }
    },
    Seven
    {
        public int getOrder()
        {
            return 7;
        }

        public int getRank()
        {
            return 7;
        }

        @Override
        public String toString()
        {
            return "7";
        }
    },
    Six
    {
        public int getOrder()
        {
            return 6;
        }

        public int getRank()
        {
            return 6;
        }

        @Override
        public String toString()
        {
            return "6";
        }
    },
    Ten
    {
        public int getOrder()
        {
            return 10;
        }

        public int getRank()
        {
            return 10;
        }

        @Override
        public String toString()
        {
            return "T";
        }
    },
    Three
    {
        public int getOrder()
        {
            return 3;
        }

        public int getRank()
        {
            return 3;
        }

        @Override
        public String toString()
        {
            return "3";
        }
    },
    Two
    {
        public int getOrder()
        {
            return 2;
        }

        public int getRank()
        {
            return 2;
        }

        @Override
        public String toString()
        {
            return "2";
        }
    };

    public boolean lessThan(final Rank r)
    {
        return (getRank() < r.getRank());
    }
}
