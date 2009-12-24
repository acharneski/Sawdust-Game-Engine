package com.sawdust.engine.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class MarkovPredictor implements Serializable
{

    private int _depth;
    private HashMap<String, HashMap<String, Short>> _memory = new HashMap<String, HashMap<String, Short>>();
    private LanguageProvider _language = null;

    public MarkovPredictor(final int d, LanguageProvider language)
    {
        _depth = d;
        _language  = language;
    }

    protected MarkovPredictor()
    {
    }

    public void learn(final String data)
    {
        ArrayList<String> letters = _language.getWordCharacterSet();
        for (int windowSize = 0; windowSize <= _depth; windowSize++)
        {
            for (int i = 0; i < (data.length() - windowSize - 1); i++)
            {
                String a = data.substring(i, i + windowSize);
                String b = data.substring(i + windowSize, i + windowSize + 1);
                a = _language.normalizeString(a);
                b = _language.normalizeString(b);
                if(!letters.contains(b)) 
                {
                    i = data.length();
                }
                else
                {
                    if (!_memory.containsKey(a))
                    {
                        _memory.put(a, new HashMap<String, Short>());
                    }
                    int v2 = 0;
                    if (_memory.get(a).containsKey(b))
                    {
                        v2 = _memory.get(a).get(b);
                    }
                    v2 += 1;
                    _memory.get(a).put(b, (short)v2);
                    // System.out.println(String.format("[%s] -> [%s] %d", a, b, v2));
                }
            }
        }
    }

    public String predict(final String string)
    {
        if (_memory.containsKey(string))
        {
            Short wieght = 0;
            final HashMap<String, Short> hashMap = _memory.get(string);
            for (final Entry<String, Short> e : hashMap.entrySet())
            {
                wieght = (short) (wieght + e.getValue());
            }
            int destiny = (int) (Math.random() * wieght);
            for (final Entry<String, Short> e : hashMap.entrySet())
            {
                destiny -= e.getValue();
                if (destiny < 0) // System.out.println(String.format("[%s] ---> [%s] %d %d", string, e.getKey(), e.getValue(), destiny));
                return e.getKey();
            }
        }
        else if (!string.isEmpty()) 
        {
            return predict(string.substring(1));
        }
        return "X";
    }

}
