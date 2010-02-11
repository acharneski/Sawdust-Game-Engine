package com.sawdust.test.game.immutable;

import java.util.Date;
import java.util.HashMap;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.IntegerGene;
import org.junit.Test;

import com.sawdust.games.DateUtil;
import com.sawdust.games.model.Agent;
import com.sawdust.games.model.Game;
import com.sawdust.games.model.Move;
import com.sawdust.games.model.Player;
import com.sawdust.games.model.ai.GameLost;
import com.sawdust.games.model.ai.GameWon;

public class GameModelTest
{
    
    @Test
    public void testCallPerformance() throws Exception
    {
        Game game = new com.sawdust.games.stop.immutable.GoBoard();
        HashMap<Player,Agent> agents = new HashMap<Player, Agent>();
        Player[] players = game.getPlayers();
        assert(2 == players.length);
        agents.put(players[0], new RandomAgent());
        agents.put(players[1], new RandomAgent());
        while(true)
        {
            for(Player p : players)
            {
                Move move = agents.get(p).selectMove(p, game, DateUtil.future(10000));
                System.out.println(move.toString());
                game = game.doMove(move);
                System.out.println(((com.sawdust.games.stop.immutable.GoBoard)game).toXmlString());
                if(null != game.getWinner()) break;
            }
            if(null != game.getWinner()) break;
        }
    }
    
    @Test
    public void testRandomVsBasicSearch() throws Exception
    {
        Game game = new com.sawdust.games.stop.immutable.GoBoard();
        HashMap<Player,Agent> agents = new HashMap<Player, Agent>();
        Player[] players = game.getPlayers();
        assert(2 == players.length);
        agents.put(players[0], new com.sawdust.games.stop.immutable.GoSearchAgent(15,8));
        agents.put(players[1], new RandomAgent());
        GameWon end = fight(game, agents, 2000);
        System.err.println(end);
    }
    
    @Test
    public void lab1() throws Exception
    {
        final Game game = new com.sawdust.games.stop.immutable.GoBoard();
        final HashMap<Player,Agent> agents = new HashMap<Player, Agent>();
        final Player[] players = game.getPlayers();
        assert(2 == players.length);

        Configuration conf = new DefaultConfiguration();
        conf.setFitnessFunction( new FitnessFunction()
        {
            @Override
            protected double evaluate(IChromosome c)
            {
                agents.put(players[0], new com.sawdust.games.stop.immutable.GoSearchAgent(15,8));
                agents.put(players[1], new com.sawdust.games.stop.immutable.GoSearchAgent(
                        (Integer) c.getGene(0).getAllele(),
                        (Integer) c.getGene(1).getAllele()));
                GameWon end = fight(game, agents, 2000);
                System.err.println(end);
                return Math.pow(2.7, (end.game.getScore(players[1]).getValue() - end.game.getScore(players[0]).getValue()));
            }
        });
        conf.setSampleChromosome(new Chromosome(conf, new Gene[] {
                new IntegerGene(conf, 0, 20 ),
                new IntegerGene(conf, 0, 20 )
        }));
        conf.setPopulationSize( 10 );

        Genotype population = Genotype.randomInitialGenotype( conf );
        for(int i=0;i<10;i++)
        {
            population.evolve();
            IChromosome best = population.getFittestChromosome();
            System.out.println(String.format("\n\nBest AI: Breadth=%d; Depth=%d\n\n", 
                    (Integer) best.getGene(0).getAllele(),
                    (Integer) best.getGene(1).getAllele()));
        }
        
    }

    private GameWon fight(Game game, HashMap<Player, Agent> agents, int timePerMoveMs)
    {
        Player[] players = game.getPlayers();
        try
        {
            while(true)
            {
                for(Player p : players)
                {
                    Date startTime = new Date();
                    Move move = agents.get(p).selectMove(p, game, DateUtil.future(timePerMoveMs));
                    if(null == move)
                    {
                        throw new GameLost(game, (p.equals(players[0])?players[1]:players[0]), p);
                    }
                    System.out.println(move.toString());
                    game = game.doMove(move);
                    //System.out.println(((com.sawdust.games.stop.immutable.GoBoard)game).toXmlString());
                    System.out.println(String.format("Move duration: %f sec", DateUtil.timeSince(startTime)));
                }
            }
        }
        catch (GameWon e)
        {
            return e;
        }
    }
}
