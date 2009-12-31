package com.sawdust.engine.game;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.sawdust.engine.common.IGameType;
import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.common.config.GameModConfig;
import com.sawdust.engine.common.config.PropertyConfig;
import com.sawdust.engine.common.config.PropertyConfig.PropertyType;
import com.sawdust.engine.service.data.Account;
import com.sawdust.engine.service.data.GameSession;
import com.sawdust.engine.service.debug.GameException;

public abstract class GameType<T extends Game> implements IGameType
{
    public abstract String getDescription();

    public abstract String getLinks();

    public String getShortDescription()
    {
       String description = getDescription();
       int indexOf = description.indexOf(".");
       if(0 <= indexOf)
       {
           if(indexOf < description.length()) indexOf += 1;
           description = description.substring(0, indexOf);
       }
       return description;
    }

    public abstract String getName();

    public abstract String getID();

    public String getIcon()
    {
        return "/media/unknown.png";
    }

    private String getKeywords()
    {
        return null;
    }

    public GameModConfig[] getModules()
    {
        return new GameModConfig[] {};
    }

    public boolean isSubtype()
    {
        return false;
    }

    public GameConfig getPrototypeConfig(final Account loadAccount)
    {
        final ArrayList<PropertyConfig> configs = new ArrayList<PropertyConfig>();

        final PropertyConfig gameName = new PropertyConfig(PropertyType.Text, GameConfig.GAME_NAME);
        gameName.defaultValue = "My Game";
        configs.add(gameName);

        if (loadAccount.isAdmin())
        {
            final PropertyConfig seed = new PropertyConfig(PropertyType.Text, GameConfig.RANDOM_SEED);
            seed.suffix = "credits";
            seed.defaultValue = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date());
            configs.add(seed);
        }

        final PropertyConfig ante = new PropertyConfig(PropertyType.Number, GameConfig.ANTE);
        ante.suffix = "credits";
        ante.defaultValue = "1";
        configs.add(ante);

        final PropertyConfig timeout = new PropertyConfig(PropertyType.Number, GameConfig.MOVE_TIMEOUT);
        timeout.suffix = "seconds";
        timeout.defaultValue = "0";
        timeout.description = "If positive, this game will force moves after the game has been stale for the specified number of seconds.";
        configs.add(timeout);

        // if(access.loadAccount().)
        final PropertyConfig publicInvites = new PropertyConfig(PropertyType.Boolean, GameConfig.PUBLIC_INVITES);
        publicInvites.defaultValue = "true";
        publicInvites.description = "If enabled, this game will be advertized for public invites. If disabled, other players can only join the game via the url.";
        configs.add(publicInvites);

        final GameConfig gameConfig = new GameConfig(this, configs.toArray(new PropertyConfig[] {}));
        for (final GameModConfig mod : getModules())
        {
            gameConfig.getModules().add(mod);
        }
        final String keywords = getKeywords();
        if (null != keywords)
        {
            gameConfig.setKeywords(keywords);
        }

        return gameConfig;
    }

    public abstract Game createNewGame(final com.sawdust.engine.common.config.GameConfig c, final SessionFactory sessionFactory) throws GameException;
    
    public List<GameType<?>> getTutorialSequence()
    {
        ArrayList<GameType<?>> arrayList = new ArrayList<GameType<?>>();
        return arrayList;
    }
    
    public Collection<GameType<?>> getScenarios()
    {
        ArrayList<GameType<?>> arrayList = new ArrayList<GameType<?>>();
        return arrayList;
    }
}
