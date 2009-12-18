package com.sawdust.server.datastore;

import java.util.Date;

public class DataStats
{
    public Date minTime = null;
    public Date maxTime = null;
    public int nullTimeCount = 0;
    public int deleted = 0;
    public String terminationCause = "Running";
    public int inspected = 0;

    void registerTime(Date when)
    {
        if(null == minTime || minTime.after(when)) minTime = when;
        if(null == maxTime || maxTime.before(when)) maxTime = when;
    }

    public void registerNullTime()
    {
        nullTimeCount++;
        
    }

    public void incrementInspected()
    {
        inspected++;
        
    }

    public void end(String string)
    {
        terminationCause = string;
        
    }

    public void incrementDeleted()
    {
        deleted++;
        
    }
   
    
}
