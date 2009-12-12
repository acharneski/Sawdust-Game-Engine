package com.sawdust.test.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Logger;

import org.junit.Test;

import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.game.PersistantTokenGame;
import com.sawdust.engine.game.blackjack.BlackjackGame;
import com.sawdust.engine.game.euchre.EuchreGame;
import com.sawdust.engine.game.euchre.ai.Normal1;
import com.sawdust.engine.game.go.GoAgent1;
import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.game.poker.PokerGame;
import com.sawdust.engine.game.poker.ai.Regular1;
import com.sawdust.engine.game.stop.StopGame;
import com.sawdust.engine.service.Util;
import com.sawdust.engine.service.data.GameSession;
import com.sawdust.test.mock.MockGameSession;

public class Genetic_Go extends GenericPlayTest
{
    
    private static final Logger LOG = Logger.getLogger(Genetic_Go.class.getName());
    
    static class GoEntity extends GoAgent1 implements Comparable<GoEntity>
    {
        static final HashMap<GoEntity, HashMap<GoEntity, Integer>> cache = new HashMap<GoEntity, HashMap<GoEntity, Integer>>();
        
        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + _depth;
            result = prime * result + _expansion;
            return result;
        }
        
        @Override
        public String toString()
        {
            return "GoEntity [_depth=" + _depth + ", _expansion=" + _expansion + "]";
        }
        
        @Override
        public boolean equals(Object obj)
        {
            if (this == obj) return true;
            if (!super.equals(obj)) return false;
            if (getClass() != obj.getClass()) return false;
            GoEntity other = (GoEntity) obj;
            if (_depth != other._depth) return false;
            if (_expansion != other._expansion) return false;
            return true;
        }
        
        private static final Logger LOG = Logger.getLogger(GoEntity.class.getName());
        
        public GoEntity(String s, int depth, int expansion)
        {
            super(s, depth, expansion);
        }
        
        public GoEntity(GoEntity mommy, GoEntity daddy)
        {
            super("["+mommy.getId() + "+" + daddy.getId()+"]", combineForDepth(mommy, daddy), combineForExpansion(mommy, daddy));
        }
        
        private static int combineForDepth(GoEntity mommy, GoEntity daddy)
        {
            return (Math.random() < 0.5) ? mommy._depth : daddy._depth;
        }
        
        private static int combineForExpansion(GoEntity mommy, GoEntity daddy)
        {
            double spreadFactor = 2.5;
            double avg = (daddy._expansion + mommy._expansion) / spreadFactor;
            double spread = Math.abs(daddy._expansion - mommy._expansion);
            return (int) (avg + (Math.random() - 0.5) * spread * spreadFactor);
        }
        
        @Override
        public int compareTo(GoEntity o)
        {
            try
            {
                if (!cache.containsKey(this)) cache.put(this, new HashMap<GoEntity, Integer>());
                if (cache.get(this).containsKey(o))
                {
                    LOG.warning(String.format("History: %s VS %s => %d", this.getId(), o.getId(), cache.get(this).get(o)));
                    return cache.get(this).get(o);
                }
                LOG.warning(String.format("%s VS %s", this.getId(), o.getId()));
                double times[] =
                {
                        0, 0
                };
                int wins[] =
                {
                        0, 0
                };
                int maxMatchTimer = 60000;
                int maxGameTimer = 30000;
                while (times[0] < maxMatchTimer && times[1] < maxMatchTimer)
                {
                    StopGame game = new StopGame(new GameConfig())
                    {
                        MockGameSession session;
                        
                        @Override
                        public GameSession getSession()
                        {
                            if (null == session)
                            {
                                session = new MockGameSession(this);
                            }
                            return session;
                        }
                    };
                    HashMap<Participant, Double> timeOuts = new HashMap<Participant, Double>();
                    timeOuts.put(this, (double) maxGameTimer);
                    timeOuts.put(o, (double) maxGameTimer);
                    HashMap<Participant, Double> timers = testGame(game, timeOuts, this, o);
                    times[0] += timers.get(this);
                    times[1] += timers.get(o);
                    if (timers.get(this) >= timeOuts.get(this))
                    {
                        wins[1]++;
                        LOG.warning(String.format("%s won by default, timers = %f/%f", o.getId(), times[0], times[1]));
                        LOG.warning(String.format("%s won by default, timers = %f/%f", o.getId(), times[0], times[1]));
                    }
                    else if (timers.get(o) >= timeOuts.get(o))
                    {
                        wins[0]++;
                        LOG.warning(String.format("%s won by default, timers = %f/%f", this.getId(), times[0], times[1]));
                    }
                    else
                    {
                        wins[game.getLastWinner()]++;
                        LOG.warning(String.format("%s won, timers = %f/%f", (0 == game.getLastWinner()) ? this.getId() : o.getId(), times[0], times[1]));
                    }
                }
                int returnValue = 0;
                if (wins[0] == wins[1]) returnValue = 0;
                returnValue = (wins[0] > wins[1]) ? -1 : 1;
                cache.get(this).put(o, returnValue);
                return returnValue;
            }
            catch (Throwable e)
            {
                LOG.warning(Util.getFullString(e));
            }
            return 0;
        }
    }
    
    //@Test(timeout = 10000)
    public void testGo() throws Exception
    {
        int populationSize = 10;
        int generationLimit = 20;
        double mortality = 0.8;
        System.out.println("\n testGo() \n");
        
        ArrayList<GoEntity> population = new ArrayList<GoEntity>();
        while (population.size() < populationSize)
        {
            GoEntity newEntity = randomMember(population);
            if (null != newEntity) population.add(newEntity);
        }
        
        int generation = 0;
        while (generationLimit > generation++)
        {
            int rank = 0;
            LOG.warning(String.format("=== Generaton #%d ===", generation));
            Collections.sort(population);
            ArrayList<GoEntity> dead = new ArrayList<GoEntity>();
            ArrayList<GoEntity> newBirths = new ArrayList<GoEntity>();
            for (GoEntity entity : population)
            {
                LOG.warning(String.format("#%d: %s", ++rank, entity.getId()));
                if (rank > (((double)population.size()) * mortality))
                {
                    LOG.warning(String.format("DEATH: %s", entity.getId()));
                    dead.add(entity);
                }
                else
                {
                    LOG.warning(String.format("%s Details: %s", entity.getId(), entity.toString()));
                }
            }
            population.removeAll(dead);
            int numberOfChildren = populationSize - population.size();
            while ((newBirths.size()) < numberOfChildren)
            {
                GoEntity entity = null;
                if (0 == population.size())
                {
                    LOG.warning(String.format("No more entities!?"));
                    entity = randomMember(population);
                }
                else
                {
                    entity = newOffspring(population);
                }
                if(null != newBirths)
                {
                    newBirths.add(entity);
                }
            }
            population.addAll(newBirths);
        }
        
    }

    private GoEntity newOffspring(ArrayList<GoEntity> population)
    {
        GoEntity mom = Util.randomMember(population);
        GoEntity dad = Util.randomMember(population);
        if(null == dad) return null;
        if(null == mom) return null;
        if(mom.equals(dad)) return null;
        GoEntity entity = new GoEntity(mom, dad);
        LOG.warning(String.format("Replacing %s with the child of %s and %s", entity.getId(), mom.getId(), dad.getId()));
        LOG.warning(String.format("BIRTH: %s", entity.getId()));
        return entity;
    }

    private GoEntity randomMember(ArrayList<GoEntity> population)
    {
        int expansion = (int) (Math.random() * 25);
        int depth = (int) (Math.random() * 5);
        GoEntity newEntity = null;
        if (Math.pow(expansion, depth) < 1000)
        {
            newEntity = new GoEntity(Integer.toString(population.size()), depth, expansion);
        }
        return newEntity;
    }
}
