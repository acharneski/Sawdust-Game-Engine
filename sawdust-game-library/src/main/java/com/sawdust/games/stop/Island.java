package com.sawdust.games.stop;

import java.util.ArrayList;
import java.util.HashSet;


public interface Island
{
    public abstract boolean contains(ArrayPosition p);

    public abstract ArrayList<ArrayPosition> getAllPositions();

    public abstract HashSet<ArrayPosition> getPerimiter();

    public abstract int getPlayer();

    public abstract boolean isImmortal();

    public abstract boolean isSurrounded();
}
