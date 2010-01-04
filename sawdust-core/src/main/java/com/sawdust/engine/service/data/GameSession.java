package com.sawdust.engine.service.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.game.Bank;
import com.sawdust.engine.game.basetypes.GameState;
import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.game.players.Player;
import com.sawdust.engine.service.debug.GameException;

public interface GameSession extends Bank
{

    public enum SessionStatus
    {
        /**
         * Session has been closed
         */
        Closed,
        /**
         * Session contains a completed game
         */
        Finished,
        /**
         * Session holds a game which is timed out
         */
        Idle,
        /**
         * Session state is not yet complete
         */
        Initializing,
        /**
         * Session is ready for new members to join
         */
        Inviting,
        /**
         * Session holds an active game
         */
        Playing
    }

    void addAgent(String name);

    void addPlayer(Participant p) throws GameException;

    List<GameState> doGetStatesSince(int versionNumber);

    void doModifyWagerPool(double factor, String msg) throws GameException;

    void doSplitWagerPool(Collection<Player> collection) throws GameException;

    void doStart(Collection<Participant> players) throws GameException;

    void doUnitWager() throws GameException;

    void doUpdateConfig(GameConfig game) throws GameException;

    void doUpdateStatus() throws GameException;

    int getActivePlayers() throws GameException;

    BankAccount getBankAccount();

    int getLatestVersionNumber();

    String getName();

    SessionMember getOwner();

    Collection<Player> getPlayers();

    <T extends Serializable> T getResource(Class<T> c);

    GameState getState();

    SessionStatus getStatus();

    String getStringId();

    int getUnitWager();

    String getUrl();

    void setMinimumPlayers(int nPlayers);

    public void setPlayerTimeout(final int pplayerTimeout);

    <T extends Serializable> void setResource(Class<T> c, T markovChain);

    void setState(GameState baseGame) throws GameException;

    boolean setStatus(SessionStatus playing, GameState game) throws GameException;

    void setUnitWager(int anteInteger) throws GameException;

}
