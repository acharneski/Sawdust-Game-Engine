package com.sawdust.engine.game.wordHunt;

import java.util.HashMap;
import java.util.Map.Entry;

public class MarkovPredictor
{

    private final int _depth;
    private final HashMap<String, HashMap<String, Integer>> _memory = new HashMap<String, HashMap<String, Integer>>();

    public MarkovPredictor(final int d)
    {
        _depth = d;
    }

    public void learn(final String data)
    {
        for (int windowSize = 0; windowSize <= _depth; windowSize++)
        {
            for (int i = 0; i < (data.length() - windowSize - 1); i++)
            {
                final String a = data.substring(i, i + windowSize);
                final String b = data.substring(i + windowSize, i + windowSize + 1);
                if (!_memory.containsKey(a))
                {
                    _memory.put(a, new HashMap<String, Integer>());
                }
                int v2 = 0;
                if (_memory.get(a).containsKey(b))
                {
                    v2 = _memory.get(a).get(b);
                }
                v2 += 1;
                _memory.get(a).put(b, v2);
                // System.out.println(String.format("[%s] -> [%s] %d", a, b, v2));
            }
        }
    }

    public String predict(final String string)
    {
        if (_memory.containsKey(string))
        {
            Integer wieght = 0;
            final HashMap<String, Integer> hashMap = _memory.get(string);
            for (final Entry<String, Integer> e : hashMap.entrySet())
            {
                wieght += e.getValue();
            }
            int destiny = (int) (Math.random() * wieght);
            for (final Entry<String, Integer> e : hashMap.entrySet())
            {
                destiny -= e.getValue();
                if (destiny < 0) // System.out.println(String.format("[%s] ---> [%s] %d %d", string, e.getKey(), e.getValue(), destiny));
                return e.getKey();
            }
        }
        else if (!string.isEmpty()) return predict(string.substring(1));
        return "X";
    }

}
