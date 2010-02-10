package com.sawdust.games;

import java.util.Date;

public class DateUtil
{

    public static Date future(int ms)
    {
        return new Date(new Date().getTime()+ms);
    }

    public static Date future(long startMs, int ms)
    {
        return new Date(startMs + ms);
    }

    public static double timeSince(Date startTime)
    {
        double seconds = ((double)(new Date().getTime() - startTime.getTime()))/1000.0;
        return seconds;
    }

}
