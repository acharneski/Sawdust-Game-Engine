package com.sawdust.engine.view.config;

import java.util.ArrayList;
import java.util.HashMap;

import com.sawdust.engine.view.IGameType;

public class GameConfig extends ConfigSet
{
    public static final String ANTE = "Ante";
    public static final String GAME_NAME = "Game Name";
    public static final String MOVE_TIMEOUT = "Move Timeout";

    public static final String NUM_PLAYERS = "Number of Players";
    public static final String PUBLIC_INVITES = "Public Invites";
    public static final String RANDOM_SEED = "Random Seed";
    private String _gameDescription = null;
    private String _gameName = null;
    private String _keywords = null;

    private ArrayList<GameModConfig> _mods = new ArrayList<GameModConfig>();

    public GameConfig()
    {
    }

    public GameConfig(GameConfig obj)
    {
    	super(obj);
    	_gameDescription = obj._gameDescription;
    	_gameName = obj._gameName;
    	_keywords = obj._keywords;
    }

    /**
     * @param _gameName
     * @param properties
     */
    public GameConfig(final IGameType game, final PropertyConfig... properties)
    {
        super(properties);
        _gameName = game.getName();
        _gameDescription = game.getDescription();
    }

    public String getGameDescription()
    {
        return _gameDescription;
    }

    public String getGameName()
    {
        return _gameName;
    }

    public String getKeywords()
    {
        return (_keywords != null) ? _keywords : getGameDescription().replaceAll("[^\\w]", " ");
    }

    public ArrayList<GameModConfig> getModules()
    {
        return _mods;
    }

    @Override
    public String getRules()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.getRules());
        for (final GameModConfig mod : _mods)
        {
            if (mod.isEnabled())
            {
                sb.append("<span class=\"sdge-game-rule-modheader\">");
                sb.append(mod.getName());
                sb.append("</span> - ");
                sb.append(mod.getDescription());
                sb.append("<br/>");
                sb.append("<div class=\"sdge-game-rule-mod\">");
                sb.append(mod.getRules());
                sb.append("</div>");
            }
        }
        return sb.toString();
    }

    public void setGameDescription(final String gameDescription)
    {
        _gameDescription = gameDescription;
    }

    public void setKeywords(final String keywords)
    {
        _keywords = keywords;
    }

    public void setProperties(final HashMap<String, PropertyConfig> properties)
    {
        _properties = properties;
    }

    private void setMods(ArrayList<GameModConfig> pmods)
    {
        this._mods = pmods;
    }

    private ArrayList<GameModConfig> getMods()
    {
        return _mods;
    }
}
