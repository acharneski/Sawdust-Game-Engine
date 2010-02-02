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
    
}
