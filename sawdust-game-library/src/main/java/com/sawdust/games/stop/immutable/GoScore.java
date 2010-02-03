package com.sawdust.games.stop.immutable;

import com.sawdust.games.model.Score;

public class GoScore implements Score
{
    final int prisoners;
    final int territory;
    public GoScore(int prisoners, int territory)
    {
        super();
        this.prisoners = prisoners;
        this.territory = territory;
    }
    @Override
    public double getValue()
    {
        return territory - prisoners;
    }
    @Override
    public String toString()
    {
        return "GoScore [prisoners=" + prisoners + ", territory=" + territory + "]";
    }
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + prisoners;
        result = prime * result + territory;
        return result;
    }
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        GoScore other = (GoScore) obj;
        if (prisoners != other.prisoners) return false;
        if (territory != other.territory) return false;
        return true;
    }
    
}
