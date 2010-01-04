package com.sawdust.engine.service;

public class PromotionConfig
{
    public final int _maxRedemption;
    public final String _name;
    public final int _value;
    public final String _msg;
    public final String _attachment;
    
    public PromotionConfig(int maxRedemption, String name, int value, String msg, String attachment)
    {
        super();
        _maxRedemption = maxRedemption;
        _name = name;
        _value = value;
        _msg = msg;
        _attachment = attachment;
    }
    
}
