/**
 * 
 */
package com.sawdust.games.euchre;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.players.Player;
import com.sawdust.engine.model.state.GameCommand;
import com.sawdust.engine.model.state.GameLabel;

public abstract class GamePhase implements Serializable
{
    public abstract void doCommand(EuchreGame game, EuchreCommand cmd, Object... params) throws GameException;

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final GamePhase other = (GamePhase) obj;
        if (getId() == null)
        {
            if (other.getId() != null) return false;
        }
        else if (!getId().equals(other.getId())) return false;
        return true;
    }

    public abstract String getId();

    public abstract ArrayList<GameCommand> getMoves(Participant access, EuchreGame game) throws GameException;

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        return result;
    }

    public abstract Collection<GameLabel> setupLabels(EuchreGame game, Player access);

}
