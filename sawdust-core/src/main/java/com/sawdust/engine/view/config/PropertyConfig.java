package com.sawdust.engine.view.config;

import java.io.Serializable;

public class PropertyConfig implements Serializable
{
    public enum PropertyType
    {
        Boolean, Null, Number, Text
    }

    public enum DetailLevel
    {
        Startup(1),
        Runtime(2),
        Spam(3);
        public final int value;
        DetailLevel(int i)
        {
            value = i;
        }
    }

    public static final String FALSE = "false";

    public static final String TRUE = "true";
    public String defaultValue = "";
    public String description = null;
    public String key = "";
    public String suffix = null;

    public PropertyType type = PropertyType.Null;;
    public DetailLevel levelOfDetail = DetailLevel.Startup;
    public String value = "";

    public PropertyConfig()
    {
    }

    /**
     * @param pkey
     * @param _value
     * @param ptype
     */
    public PropertyConfig(final PropertyType ptype, final String pkey)
    {
        super();
        key = pkey;
        type = ptype;
    }

    public boolean getBoolean()
    {
        return value.equals(PropertyConfig.TRUE);
    }

    public int getInteger()
    {
        return Integer.parseInt(value);
    }

}
