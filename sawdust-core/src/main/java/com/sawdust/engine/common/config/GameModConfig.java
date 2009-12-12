package com.sawdust.engine.common.config;

public class GameModConfig extends ConfigSet
{
    private String _description = "";
    private boolean _enabled = false;
    private String _name = "";

    private GameModConfig()
    {
        
    }
    
    public GameModConfig(final String name, final String description)
    {
        super();
        _name = name;
        _description = description;
    }

    public String getDescription()
    {
        return _description;
    }

    public String getName()
    {
        return _name;
    }

    public boolean isEnabled()
    {
        return _enabled;
    }

    public void setDescription(final String description)
    {
        _description = description;
    }

    public void setEnabled(final boolean enabled)
    {
        _enabled = enabled;
    }

    public void setName(final String name)
    {
        _name = name;
    }
}
