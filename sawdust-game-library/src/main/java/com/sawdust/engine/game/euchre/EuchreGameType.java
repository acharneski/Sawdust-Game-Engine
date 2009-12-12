package com.sawdust.engine.game.euchre;

import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.common.config.GameModConfig;
import com.sawdust.engine.game.BaseGame;
import com.sawdust.engine.game.SessionFactory;
import com.sawdust.engine.game.blackjack.BlackjackGame;
import com.sawdust.engine.game.euchre.mod.Mod78;
import com.sawdust.engine.service.data.GameSession;

public class EuchreGameType extends com.sawdust.engine.game.GameType<EuchreGame>
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
    public BaseGame createNewGame(final com.sawdust.engine.common.config.GameConfig c, final SessionFactory sessionFactory)
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
