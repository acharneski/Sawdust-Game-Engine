package com.sawdust.games.euchre.mod;

import com.sawdust.engine.model.GameModification;
import com.sawdust.engine.view.cards.Ranks;
import com.sawdust.engine.view.config.GameModConfig;
import com.sawdust.games.euchre.EuchreDeck;
import com.sawdust.games.euchre.EuchreGame;

public class Mod78 extends GameModification<EuchreGame>
{

    private static final String DESCRIPTION = "Cards dealt will include 7's and 8's";
    private static final String TITLE = "7's and 8's";

    public static GameModConfig getConfig()
    {
        final GameModConfig sevensandeightsModule = new GameModConfig(TITLE, DESCRIPTION);
        // sevensandeightsModule.addProperty(new PropertyConfig(PropertyType.Boolean, "Can you see me?"));
        return sevensandeightsModule;
    }

    public static GameModification<EuchreGame> getNewModule(final GameModConfig config)
    {
        // return null;
        if (!TITLE.equals(config.getName())) return null;
        return new Mod78();
    }

    @Override
    public EuchreGame apply(final EuchreGame game)
    {
        ((EuchreDeck) game.getDeck()).setFilterMax(Ranks.Six);
        return game;
    }

}
