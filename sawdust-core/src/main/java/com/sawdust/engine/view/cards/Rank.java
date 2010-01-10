/**
 * 
 */
package com.sawdust.engine.view.cards;

public interface Rank
{
    int getOrder();

    int getRank();

    boolean lessThan(Rank r);
}
