package com.sawdust.engine.common.config;

import java.io.Serializable;
import java.util.HashMap;

public class ConfigSet implements Serializable
{
    protected HashMap<String, PropertyConfig> _properties = new HashMap<String, PropertyConfig>();

    public ConfigSet()
    {

    }

    public ConfigSet(ConfigSet obj)
    {
    	_properties.putAll(obj._properties);
    }

    public ConfigSet(final PropertyConfig[] properties)
    {
        for (final PropertyConfig property : properties)
        {
            addProperty(property);
        }
    }

    public void addProperty(final PropertyConfig property)
    {
        _properties.put(property.key, property);
    }

    public HashMap<String, PropertyConfig> getProperties()
    {
        return _properties;
    }

    public String getRules()
    {
        final StringBuilder sb = new StringBuilder();
        for (final PropertyConfig property : _properties.values())
        {
            sb.append(property.key + " = " + property.value + "<br/>");
        }
        return sb.toString();
    }
}
