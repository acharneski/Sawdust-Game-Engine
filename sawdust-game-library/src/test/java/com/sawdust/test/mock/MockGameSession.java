package com.sawdust.test.mock;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.game.Bank;
import com.sawdust.engine.game.basetypes.BaseGame;
import com.sawdust.engine.game.basetypes.GameState;
import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.game.players.Player;
import com.sawdust.engine.service.MarkovPredictor;
import com.sawdust.engine.service.data.Account;
import com.sawdust.engine.service.data.GameSession;
import com.sawdust.engine.service.data.BankAccount;
import com.sawdust.engine.service.data.SessionMember;
import com.sawdust.engine.service.debug.GameException;
import com.sawdust.engine.service.debug.GameLogicException;

public class MockGameSession implements GameSession, Serializable
{
    private HashSet<Player> _members = new HashSet<Player>();
    private SessionStatus _status = SessionStatus.Initializing;
    public int ante = 1;
    public int bank = 0;
    private GameState _currentState = null;
    private int playerTimeout;

    private MockGameSession()
    {
    }
    
    /**
     * @param currentState
     */
    public MockGameSession(BaseGame currentState)
    {
        super();
        this._currentState = currentState;
    }

    public void addPlayer(Participant account) throws GameException
    {
        if(account instanceof Player)
        {
            _members.add((Player) account);
        }
    }

    public void doUnitWager() throws GameException
    {
        for (Player s : _members)
        {
            Account a = s.loadAccount();
            a.withdraw(ante, this, "Ante Up");
        }
    }

    public int getUnitWager()
    {
        return ante;
    }

    public int getBalance()
    {
        return bank;
    }

    public Collection<Player> getPlayers()
    {
        return new HashSet<Player>(_members);
    }

    public SessionStatus getStatus()
    {
        return _status;
    }

    public GameState getState()
    {
        return _currentState;
    }

    public void doSplitWagerPool(Collection<Player> winners) throws GameException
    {
        if (null != winners && 0 < winners.size())
        {
            int amt = bank / winners.size();
            for (Player player : winners)
            {
                this.withdraw(amt, player.loadAccount(), "Pay Out");
                bank -= amt;
            }
        }
        bank = 0;
    }

    public boolean setStatus(SessionStatus playing, GameState game) throws GameException
    {
        _status = playing;
        return false;
    }

    public void setState(GameState baseGame) throws GameException
    {
        _currentState = baseGame;
    }

    public void withdraw(int amount, Bank from, String description) throws GameException
    {
        bank -= amount;
        if(null != from) from.withdraw(-amount, null, description);
    }

    public SessionMember getOwner()
    {
        for(Player p : _members)
        {
            return new MockSessionMember(p);
        }
        return null;
    }

    public int getLatestVersionNumber()
    {
        return 0;
    }

    public List<GameState> doGetStatesSince(int versionNumber)
    {
        return new ArrayList<GameState>();
    }

    public String getStringId()
    {
        return "TestGameSession";
    }

    public void doUpdateStatus() throws GameException
    {
        // TODO Auto-generated method stub
        
    }

    public void setMinimumPlayers(int nPlayers)
    {
        // TODO Auto-generated method stub
        
    }

    public void addAgent(String name)
    {
        // TODO Auto-generated method stub
        
    }

    public void doStart() throws GameException
    {
        _currentState.start();
    }

    public void setPlayerTimeout(final int pplayerTimeout)
    {
        playerTimeout = pplayerTimeout;
    }

    @Override
    public BankAccount getBankAccount()
    {
        return new MockMoneyAccount();
    }

    @Override
    public String getName()
    {
        return "Test Game Session";
    }

    @Override
    public void doModifyWagerPool(double factor, String msg)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public <T extends Serializable> T getResource(Class<T> class1)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends Serializable> void setResource(Class<T> c, T markovChain)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getUrl()
    {
        return "http://com.sawdust.test.mock/MockGameSession";
    }

    @Override
    public void doUpdateConfig(GameConfig game)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setUnitWager(int anteInteger)
    {
        ante = anteInteger;
    }

    @Override
    public int getActivePlayers() throws GameException
    {
        assert(false);
        return 1;
    }
}
