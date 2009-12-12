package com.sawdust.test.mock;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.sawdust.engine.common.Bank;
import com.sawdust.engine.game.BaseGame;
import com.sawdust.engine.game.Game;
import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.game.players.Player;
import com.sawdust.engine.service.data.Account;
import com.sawdust.engine.service.data.GameSession;
import com.sawdust.engine.service.data.MoneyAccount;
import com.sawdust.engine.service.data.SessionMember;
import com.sawdust.engine.service.debug.GameException;
import com.sawdust.engine.service.debug.GameLogicException;

public class MockGameSession implements GameSession, Serializable
{
    private HashSet<Player> _members = new HashSet<Player>();
    private SessionStatus _status = SessionStatus.Initializing;
    public int ante = 1;
    public int bank = 0;
    private Game _currentState = null;
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

    public void anteUp() throws com.sawdust.engine.common.GameException
    {
        for (Player s : _members)
        {
            Account a = s.loadAccount();
            a.withdraw(ante, this, "Ante Up");
        }
    }

    public int getAnte()
    {
        return ante;
    }

    public int getBalance()
    {
        return bank;
    }

    public Collection<Player> getMembers()
    {
        return new HashSet<Player>(_members);
    }

    public SessionStatus getSessionStatus()
    {
        return _status;
    }

    public Game getLatestState()
    {
        return _currentState;
    }

    public void payOut(Collection<Player> winners) throws com.sawdust.engine.common.GameException
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

    public boolean setSessionStatus(SessionStatus playing, Game game) throws GameException
    {
        _status = playing;
        return false;
    }

    public void setState(Game baseGame) throws GameException
    {
        _currentState = baseGame;
    }

    public void withdraw(int amount, Bank from, String description) throws com.sawdust.engine.common.GameException
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

    public List<Game> getStatesSince(int versionNumber)
    {
        return new ArrayList<Game>();
    }

    public String getId()
    {
        return "TestGameSession";
    }

    public void updateStatus() throws GameException
    {
        // TODO Auto-generated method stub
        
    }

    public void setRequiredPlayers(int nPlayers)
    {
        // TODO Auto-generated method stub
        
    }

    public void addAi(String name)
    {
        // TODO Auto-generated method stub
        
    }

    public void start(Collection<Participant> collection) throws com.sawdust.engine.common.GameException
    {
        _currentState.start();
    }

    public void setPlayerTimeout(final int pplayerTimeout)
    {
        playerTimeout = pplayerTimeout;
    }

    @Override
    public MoneyAccount getAccount()
    {
        return new MockMoneyAccount();
    }

    @Override
    public String getName()
    {
        return "Test Game Session";
    }

    @Override
    public void modifyPayout(double factor, String msg)
    {
        // TODO Auto-generated method stub
    }
}
