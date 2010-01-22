package com.sawdust.test.gae;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.basetypes.GameState;
import com.sawdust.engine.model.players.Agent;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.state.GameCommand;

public class GenericPlayUtil<T extends GameState>
{
    private static final Logger LOG = Logger.getLogger(GenericPlayUtil.class.getName());
    final T _game;
    final Participant _players[];
    
    public GenericPlayUtil(T game, Participant... players)
    {
        super();
        this._game = game;
        this._players = players;
    }

    public GenericPlayUtil(T game, int numberOfPlayers)
    {
        this(game, generatePlayers(game, numberOfPlayers));
    }

    public int runGame() throws Exception
    {
        for (Participant p : _players)
        {
            _game.getSession().addPlayer(p);
        }
        _game.getSession().doStart();

        Participant lastPlayer = null;
        while (_game.isInPlay())
        {
            Participant player = _game.getCurrentPlayer();
            assert(!player.equals(lastPlayer)) : "The player has not changed";
            LOG.info("Agent Move: " + player.getId());
            GameCommand<T> move = ((Agent<T>)player).getMove(_game, player);
            assert(player.equals(_game.getCurrentPlayer())) : "getMove changed the game...";
            preMove(move);
            System.out.println("Command: " + move.getCommandText());
            move.doCommand(player, null);
            lastPlayer = player;
            postMove(move);
        }
        return -1;
    }

    protected void postMove(GameCommand<T> move)
    {
        // Do nothing by default
    }

    protected void preMove(GameCommand<T> move)
    {
        // Do nothing by default
    }

    protected static <T extends GameState> Participant[] generatePlayers(T game, int nPlayers)
    {
        ArrayList<Participant> players = new ArrayList<Participant>();
        for (int i = 0; i < nPlayers; i++)
        {
            players.add(newRandomPlayer("test"+i));
        }
        return players.toArray(new Participant[]{});
    }

    protected static <T extends GameState> Participant newRandomPlayer(String userId)
    {
        return new Agent<T>(userId){

            @Override
            public GameCommand<T> getMove(T game, Participant participant)
                    throws GameException
            {
                ArrayList<GameCommand<?>> moves = game.getMoves(participant);
                GameCommand randomMove = com.sawdust.engine.controller.Util.randomMember(moves.toArray(new GameCommand[] {}));
                return randomMove;
            }
            
        };
    }

}