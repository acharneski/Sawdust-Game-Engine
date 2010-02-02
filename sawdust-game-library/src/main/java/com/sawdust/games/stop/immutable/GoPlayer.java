package com.sawdust.games.stop.immutable;


public class GoPlayer implements com.sawdust.games.model.Player
{
    public final int value;

    public GoPlayer()
    {
        value = 0;
    }

    public GoPlayer(int i)
    {
        assert(i>0);
        value = i;
    }

    @Override
    public String toString()
    {
        return Integer.toString(value);
    }

    public static GoPlayer parse(String token)
    {
        if(token.equals("0")) return new GoPlayer();
        if(token.equals("1")) return new GoPlayer(1);
        if(token.equals("2")) return new GoPlayer(2);
        return null;
    }

    public boolean isNull()
    {
        return 0 == value;
    }

    public String getName()
    {
        return "Player " + value;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + value;
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        GoPlayer other = (GoPlayer) obj;
        if (value != other.value) return false;
        return true;
    }
    

}
