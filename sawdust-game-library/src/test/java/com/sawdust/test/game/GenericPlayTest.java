package com.sawdust.test.game;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import com.sawdust.engine.controller.entities.Account;
import com.sawdust.engine.controller.entities.GameSession;
import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.basetypes.GameState;
import com.sawdust.engine.model.players.AccountFactory;
import com.sawdust.engine.model.players.Agent;
import com.sawdust.engine.model.players.MoveFactory;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.players.Player;
import com.sawdust.engine.model.state.GameCommand;
import com.sawdust.engine.view.game.ActivityEvent;
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

    public static <T extends GameState> HashMap<Participant, Double> testGame(T game, Participant... players) throws Exception
    {
        return testGame(game, null, players);
        
    }
    public static <T extends GameState> HashMap<Participant, Double> testGame(T game, HashMap<Participant, Double> timeouts, Participant... players) throws Exception
    {
        HashMap<Participant,Double> sessionTimers = new HashMap<Participant,Double>(); 
        for (Participant p : players)
        {
            sessionTimers.put(p, 0.0);
            game.doAddPlayer(p);
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
                ((Agent<T>)player).getMove(game, player).doCommand(player, null);
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

    private static <T extends GameState> GameCommand<T> doRandomMove(T game, Participant player) throws GameException
    {
        ArrayList<GameCommand> moves = game.getMoves(player);
        GameCommand randomMember = com.sawdust.engine.controller.Util.randomMember(moves.toArray(new GameCommand[] {}));
        String commandText = randomMember.getCommandText();
        System.out.println("Command: " + commandText);
        return randomMember;
    }

    public <T extends GameState> void testGame(T game, int nPlayers) throws Exception
    {
        ArrayList<Participant> players = new ArrayList<Participant>();
        for (int i = 0; i < nPlayers; i++)
        {
            //players.add(newPlayer(game, "test1"));
            players.add(newAgent("test1"));
        }
        testGame(game, players.toArray(new Participant[] {}));
    }

    protected <T extends GameState> Participant newAgent(String userId)
    {
        return new Agent<T>(userId, new MoveFactory<T>()
        {
            @Override
            public GameCommand<T> getMove(T game, Participant participant) throws GameException
            {
                return doRandomMove(game, participant);
            }
        });
    }

    protected <T extends GameState> Participant newPlayer(T game, String userId)
    {
        final MockSessionToken access1 = new MockSessionToken(userId, game.getSession());
        Player player1 = new Player(access1.getUserId(), false, new AccountFactory()
        {
            
            @Override
            public Account getAccount()
            {
                return access1.doLoadAccount();
            }
        });
        return player1;
    }

    private static void startGame(GameSession session, Participant players2) throws com.sawdust.engine.view.GameException
    {
        ArrayList<Participant> players = new ArrayList<Participant>();
        players.add(players2);
        session.doStart();
    }

}