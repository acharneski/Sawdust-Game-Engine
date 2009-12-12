package com.sawdust.engine.common.game;

import java.io.Serializable;
import java.util.HashMap;

public class Notification implements Serializable
{
    public String notifyText = "";
    public HashMap<String,String> commands = new HashMap<String, String>();
    public void add(String key, String value)
    {
        commands.put(key, value);
    }
}
