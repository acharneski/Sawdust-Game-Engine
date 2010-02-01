package com.sawdust.games.stop.immutable;

public class Player
{

    public final int value;

    public Player()
    {
        value = 0;
    }

    public Player(int i)
    {
        assert(i>0);
        value = i;
    }

    @Override
    public String toString()
    {
        return Integer.toString(value);
    }

    public static Player parse(String token)
    {
        if(token.equals("0")) return new Player();
        if(token.equals("1")) return new Player(1);
        if(token.equals("2")) return new Player(2);
        return null;
    }

    public boolean isNull()
    {
        return 0 == value;
    }
    

}
