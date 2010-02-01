package com.sawdust.games.stop.immutable;

public class player
{

    public final int value;

    public player()
    {
        value = 0;
    }

    public player(int i)
    {
        assert(i>0);
        value = i;
    }

    @Override
    public String toString()
    {
        return Integer.toString(value);
    }

    public static player parse(String token)
    {
        if(token.equals("0")) return new player();
        if(token.equals("1")) return new player(1);
        if(token.equals("2")) return new player(2);
        return null;
    }
    

}
