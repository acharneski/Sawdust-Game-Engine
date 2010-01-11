package com.sawdust.games.euchre;

import com.sawdust.engine.controller.entities.GameSession;
import com.sawdust.engine.model.SessionFactory;
import com.sawdust.engine.model.basetypes.BaseGame;
import com.sawdust.engine.view.config.GameConfig;
import com.sawdust.engine.view.config.GameModConfig;
import com.sawdust.games.blackjack.BlackjackGame;
import com.sawdust.games.euchre.mod.Mod78;

public class EuchreGameType extends com.sawdust.engine.model.GameType<EuchreGame>
{
    public static final EuchreGameType INSTANCE = new EuchreGameType();

    private EuchreGameType()
    {
    }

    @Override
    public String getDescription()
    {
        return "<p>This is a simplified version of <b>Euchre</b> with individual rounds. " + "Many rules have been removed such as score-keeping, "
                + "sticking the dealer, farmer hands, going at alone, etc.</p>\r\n" + "<p>For more information, please see the <a href=\"/rules/Euchre.jsp\">"
                + "detailed rules</a> or the Euchre page on " + "<a href=\"http://en.wikipedia.org/wiki/Euchre\" target=\"_new\">Wikipedia</a>" + "";
    }

    @Override
    public String getName()
    {
        return "Euchre";
    }

    @Override
    public String getID()
    {
        return "Euchre";
    }

    @Override
    public GameModConfig[] getModules()
    {
        return new GameModConfig[]
        {
            Mod78.getConfig()
        };
    };

    @Override
    public String getLinks()
    {
        return "<a href=\"http://en.wikipedia.org/wiki/Euchre\" target=\"_new\">Wikipedia</a>";
    }

    @Override
    public BaseGame getNewGame(final com.sawdust.engine.view.config.GameConfig c, final SessionFactory sessionFactory)
    {
        return new EuchreGame(c)
        {
            @Override
            public GameSession getSession()
            {
                return sessionFactory.getSession();
            }
        };
    }

};
