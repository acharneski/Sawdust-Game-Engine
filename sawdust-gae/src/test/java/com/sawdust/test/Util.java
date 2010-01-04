package com.sawdust.test;


import java.util.Date;
import java.util.logging.Logger;

import com.sawdust.engine.common.game.GameLabel;
import com.sawdust.engine.common.game.GameFrame;
import com.sawdust.engine.common.game.Message;
import com.sawdust.engine.game.basetypes.GameState;
import com.sawdust.engine.game.basetypes.PersistantTokenGame;
import com.sawdust.engine.game.basetypes.TokenGame;
import com.sawdust.engine.game.players.Player;
import com.sawdust.engine.game.state.GameCommand;
import com.sawdust.engine.game.state.Token;
import com.sawdust.engine.service.data.GameSession;
import com.sawdust.engine.service.debug.GameException;

public class Util
{
	private static final Logger LOG = Logger.getLogger(Util.class.getName());

    public static void assertEqual(final int actual, final int expected)
    {
        if (expected != actual) { throw new AssertionError(String.format("Unexpected value: %d (expected %d)", actual,
                expected)); }
        // TODO Auto-generated method stub

    }

    public static void assertMessageFound(GameState blackjackGame, String expected) throws AssertionError
    {
        for (Message message : blackjackGame.getMessages())
        {
            if (message.getText().contains(expected))
            {
                LOG.fine("Message found: " + expected);
                return;
            }
        }
        LOG.fine("Message not found: " + expected);
        throw new AssertionError();
    }

    public static boolean hasGuiCommand(GameFrame gwt, String cmd)
    {
        for (GameLabel l : gwt.getLabels())
        {
            if (cmd.equals(l.command)) return true;
        }
        LOG.fine("No gui button: " + cmd);
        for (com.sawdust.engine.common.game.Token t : gwt.getTokens())
        {
            for (String c : t.getMoveCommands().values())
            {
                if (cmd.equals(c)) return true;
            }
        }
        LOG.fine("No gui access: " + cmd);
        return false;
    }

    public static Date printNewMessages(GameState blackjackGame, Date now)
    {
        Date next = now;
        for (Message message : blackjackGame.getMessages())
        {
            if (next.before(message.getDateTime()))
            {
                next = message.getDateTime();
            }
            if (now.before(message.getDateTime()))
            {
                System.out.println(message.getText());
            }
        }
        try
        {
            Thread.sleep(10);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return next;
    }

    public static String printVisibleCards(GameSession game, Player access) throws GameException
    {
        PersistantTokenGame state = (PersistantTokenGame) game.getState();
        GameFrame gwt = state.toGwt(access);
        StringBuilder sb = new StringBuilder();
        for (com.sawdust.engine.common.game.Token t : gwt.getTokens())
        {
            if (0 < sb.length()) sb.append(", ");
            sb.append(t.getBaseImageId());
        }
        return sb.toString();
    }

    public static String printMyCards(GameSession game, Player access) throws GameException
    {
        TokenGame state = (TokenGame) game.getState();
        StringBuilder sb = new StringBuilder();
        for (Token t : state.getTokens())
        {
            if (t.getOwner().equals(access))
            {
                if (0 < sb.length()) sb.append(", ");
                sb.append(t.getArt());
            }
        }
        return sb.toString();
    }

    public static void runCommand(GameSession gameSession, Player access, String cmd) throws com.sawdust.engine.common.GameException
    {
        GameState game = gameSession.getState();
        for (GameCommand command : game.getCommands(access))
        {
        	if(cmd.startsWith(command.getCommandText()))
        	{
        		command.doCommand(access, cmd);
        	}
        }
    }

    public static void testGuiCommand(GameSession game, Player access, String cmd) throws com.sawdust.engine.common.GameException
    {
        GameState latestState = game.getState();
        GameFrame gwt = latestState.toGwt(access);
        if (!hasGuiCommand(gwt, cmd))
        {
            // throw new AssertionError("No gui command: " + cmd);
        }
        runCommand(game, access, cmd);
    }

}
