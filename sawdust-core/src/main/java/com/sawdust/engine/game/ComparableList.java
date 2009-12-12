package com.sawdust.engine.game;

import java.util.ArrayList;

public class ComparableList<I extends Comparable<I>> extends ArrayList<I> implements Comparable<ArrayList<I>>
{
    /*
     * -1 means this is "less than" obj Normal usage will be based on indexOf functions with the highest rank at the front of the list
     * Therefore negative results will often mean "superior than parameter obj"
     */
    public int compareTo(ArrayList<I> s)
    {
        final Integer a = size();
        final Integer b = s.size();
        final int m = (b > a) ? a : b;
        for (int i = 0; i < m; i++)
        {
            final I t = s.get(i);
            final I x = get(i);
            final int r = x.compareTo(t);
            if (0 != r) return r;
        }
        return b.compareTo(a);
    }

    public ComparableList<I> popItem()
    {
        final ComparableList<I> comparableList = new ComparableList<I>();
        comparableList.addAll(this);
        comparableList.remove(0);
        return comparableList;
    }

}
