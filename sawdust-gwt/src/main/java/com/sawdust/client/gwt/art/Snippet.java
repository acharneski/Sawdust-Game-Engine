package com.sawdust.client.gwt.art;

public class Queue
{
    static final int MAXSIZE = 100;
    int heap[] = new int[MAXSIZE];
    int begin = 0;
    int end = 0;
    
    Integer get()
    {
        if(begin == end) return null;
        int returnValue = heap[end];
        if(end++ >= MAXSIZE) end = 0;
        return returnValue;
    }
    
    void put(int x)
    {
        if(begin == (end-1)) throw new RuntimeException("Array out of bounds");
        heap[begin] = x;
        if(begin++ >= MAXSIZE) begin = 0;
    }
    
}

