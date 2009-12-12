/**
 * 
 */
package com.sawdust.engine.common.cards;

public interface Rank
{
    int getOrder();

    int getRank();

    boolean lessThan(Rank r);
}
