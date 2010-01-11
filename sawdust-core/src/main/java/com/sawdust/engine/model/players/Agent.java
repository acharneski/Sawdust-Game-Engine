package com.sawdust.engine.model.players;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.basetypes.GameState;
import com.sawdust.engine.model.state.GameCommand;

public class Agent<G extends GameState> extends Participant
{

    protected static class SerialForm<G extends GameState> extends Participant.SerialForm implements Serializable
    {
        MoveFactory moveFactory;

        protected SerialForm(){}
        protected SerialForm(Agent<G> obj)
        {
            super(obj);
            moveFactory = obj.moveFactory;
        }

        private Object readResolve()
        {
            return new Agent<G>(this);
        }
    }

    private void readObject(ObjectInputStream s) throws  IOException, ClassNotFoundException
    {
        throw new NotSerializableException();
    }

    private Object writeReplace()
    {
        return new SerialForm<G>(this);
    }
    
    public Agent(SerialForm<G> serialForm)
    {
        super(serialForm);
        moveFactory = serialForm.moveFactory;
    }

    public Agent(final String s, MoveFactory moves)
    {
        super(s);
        moveFactory = moves;
    }

    final MoveFactory moveFactory;
    
    public GameCommand<G> getMove(G game, Participant participant) throws GameException
    {
        return moveFactory.getMove(game,participant);
    }
}
