package com.sawdust.engine.service;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;



public class LanguageChain
{
    private static final Logger LOG = Logger.getLogger(LanguageChain.class.getName());
    public static final LanguageChain INSTANCE = new LanguageChain();

    static final String letters[] =
    {
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    };
    HashMap<String, Double> firstOrder = new HashMap<String, Double>();

    public LanguageChain()
    {
        firstOrder.put("A", 8.1);
        firstOrder.put("B", 1.49);
        firstOrder.put("C", 2.78);
        firstOrder.put("D", 4.25);
        firstOrder.put("E", 12.70);
        firstOrder.put("F", 2.23);
        firstOrder.put("G", 2.02);
        firstOrder.put("H", 6.09);
        firstOrder.put("I", 6.97);
        firstOrder.put("J", 0.15);
        firstOrder.put("K", 0.77);
        firstOrder.put("L", 4.03);
        firstOrder.put("M", 2.41);
        firstOrder.put("N", 6.75);
        firstOrder.put("O", 7.51);
        firstOrder.put("P", 1.93);
        firstOrder.put("Q", 0.10);
        firstOrder.put("R", 5.99);
        firstOrder.put("S", 6.33);
        firstOrder.put("T", 9.06);
        firstOrder.put("U", 2.76);
        firstOrder.put("V", 0.98);
        firstOrder.put("W", 2.36);
        firstOrder.put("X", 0.15);
        firstOrder.put("Y", 1.97);
        firstOrder.put("Z", 0.07);
    }

    public String[] getLetters()
    {
        return letters;
    }

    public double getNormalizationFactor()
    {
        double returnValue = 0.0;
        for (final double d : firstOrder.values())
        {
            returnValue += d;
        }
        return returnValue;
    }

    public String getRandomLetter()
    {
        double destiny = getNormalizationFactor() * Math.random();
        for (final Entry<String, Double> e : firstOrder.entrySet())
        {
            destiny -= e.getValue();
            if (0 >= destiny) return e.getKey();
        }
        LOG.warning("Destiny out of bounds!");
        return Util.randomMember(letters);
    }
}
