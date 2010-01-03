package com.sawdust.engine.service.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.game.Bank;
import com.sawdust.engine.game.Game;
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

    void addPlayer(Participant p) throws GameException;

    void anteUp() throws GameException;

    int getAnte();

    String getId();

    Game getLatestState();

    int getLatestVersionNumber();

    Collection<Player> getMembers();

    SessionMember getOwner();

    SessionStatus getSessionStatus();

    List<Game> getStatesSince(int versionNumber);

    void payOut(Collection<Player> collection) throws GameException;

    boolean setSessionStatus(SessionStatus playing, Game game) throws GameException;

    void setState(Game baseGame) throws GameException;

	void updateStatus() throws GameException;

    void setRequiredPlayers(int nPlayers);

    void addAi(String name);

    void start(Collection<Participant> players) throws GameException;

    public void setPlayerTimeout(final int pplayerTimeout);

    MoneyAccount getAccount();
    
    String getName();

    void modifyPayout(double factor, String msg) throws GameException;

    <T extends Serializable> void setResource(Class<T> c, T markovChain);

    <T extends Serializable> T getResource(Class<T> c);

    String getUrl();

    void updateConfig(GameConfig game) throws GameException;

    void setValue(int anteInteger) throws GameException;

    int getActiveMembers() throws GameException;

}
