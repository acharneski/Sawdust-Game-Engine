package com.sawdust.test.game;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import com.sawdust.engine.game.Game;
import com.sawdust.engine.game.players.ActivityEvent;
import com.sawdust.engine.game.players.Agent;
import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.game.players.Player;
import com.sawdust.engine.game.state.GameCommand;
import com.sawdust.engine.service.data.Account;
import com.sawdust.engine.service.data.GameSession;
import com.sawdust.engine.service.debug.GameException;
import com.sawdust.test.mock.MockSessionToken;

import junit.framework.TestCase;

public abstract class GenericPlayTest extends TestCase
{
    private static final Logger LOG = Logger.getLogger(GenericPlayTest.class.getName());

    public GenericPlayTest()
    {
        super();
    }

    public GenericPlayTest(String name)
    {
        super(name);
    }

    public static <T extends Game> HashMap<Participant, Double> testGame(T game, Participant... players) throws Exception
    {
        return testGame(game, null, players);
        
    }
    public static <T extends Game> HashMap<Participant, Double> testGame(T game, HashMap<Participant, Double> timeouts, Participant... players) throws Exception
    {
        HashMap<Participant,Double> sessionTimers = new HashMap<Participant,Double>(); 
        for (Participant p : players)
        {
            sessionTimers.put(p, 0.0);
            game.addMember(p);
            game.getSession().addPlayer(p);
        }
        startGame(game.getSession(), players[0]);

        int playerIdx = -1;
        int nPlayers = players.length;
        Participant lastPlayer = null;
        while (game.isInPlay())
        {
            Participant player = game.getCurrentPlayer();
            assert(!player.equals(lastPlayer)) : "The player has not changed"; 
            double currentTime = sessionTimers.get(player);
            long startTime = new java.util.Date().getTime();
            if(player instanceof Agent<?>)
            {
                LOG.info("Agent Move: " + player.getId());
                ((Agent<T>)player).Move(game, player);
            }
            else
            {
                LOG.warning("Unknown Participant! Random Move: " + player.getId());
                doRandomMove(game, player);
            }
            lastPlayer = player;
            double thisTime = new java.util.Date().getTime() - startTime;
            double newTotal = thisTime + currentTime;
            sessionTimers.put(player, newTotal);
            if(     (null != timeouts) && 
                    (timeouts.containsKey(player)) && 
                    (timeouts.get(player) < newTotal))
            {
                break;
            }
        }
        return sessionTimers;
    }

    private static <T extends Game> void doRandomMove(T game, Participant player) throws GameException
    {
        ArrayList<GameCommand> moves = game.getMoves(player);
        while (true)
        {
            GameCommand randomMember = com.sawdust.engine.service.Util.randomMember(moves.toArray(new GameCommand[] {}));
            try
            {
                String commandText = randomMember.getCommandText();
                System.out.println("Command: " + commandText);
                randomMember.doCommand(player, commandText);
                break;
            }
            catch (GameException e)
            {
                System.out.println("Game Exception: " + e.getMessage());
            }
        }
    }

    public <T extends Game> void testGame(T game, int nPlayers) throws Exception
    {
        ArrayList<Participant> players = new ArrayList<Participant>();
        for (int i = 0; i < nPlayers; i++)
        {
            //players.add(newPlayer(game, "test1"));
            players.add(newAgent("test1"));
        }
        testGame(game, players.toArray(new Participant[] {}));
    }

    protected <T extends Game> Participant newAgent(String userId)
    {
        return new Agent<T>(userId)
        {
            @Override
            public void Move(T game, Participant participant) throws GameException
            {
                doRandomMove(game, this);
            }
        };
    }

    protected <T extends Game> Participant newPlayer(T game, String userId)
    {
        final MockSessionToken access1 = new MockSessionToken(userId, game.getSession());
        Player player1 = new Player(access1.getUserId(), false)
        {
            @Override
            public Account loadAccount()
            {
                return access1.loadAccount();
            }

            @Override
            public void logActivity(ActivityEvent event)
            {
                // TODO Auto-generated method stub
                
            }
        };
        return player1;
    }

    private static void startGame(GameSession session, Participant players2) throws com.sawdust.engine.common.GameException
    {
        ArrayList<Participant> players = new ArrayList<Participant>();
        players.add(players2);
        session.start(players);
    }

}