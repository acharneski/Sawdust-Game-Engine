package com.sawdust.engine.common.config;

import java.io.Serializable;

public class PropertyConfig implements Serializable
{
    public enum PropertyType
    {
        Boolean, Null, Number, Text
    }

    public static final String FALSE = "false";

    public static final String TRUE = "true";
    public String defaultValue = "";
    public String description = null;
    public String key = "";
    public String suffix = null;

    public PropertyType type = PropertyType.Null;;
    public String value = "";

    public PropertyConfig()
    {
    }

    /**
     * @param pkey
     * @param value
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
