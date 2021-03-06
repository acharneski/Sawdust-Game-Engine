package com.sawdust.games.wordHunt;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.basetypes.TokenGame;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.players.Player;
import com.sawdust.engine.model.state.IndexPosition;
import com.sawdust.engine.model.state.Token;

public class BoardToken extends Token
{
    public String letter;
    public HashSet<String> selectedFor = new HashSet<String>();

    protected static class SerialForm extends Token.SerialForm
    {
        String letter;
        HashSet<String> selectedFor = new HashSet<String>();
        
        protected SerialForm(){}
        protected SerialForm(BoardToken obj)
        {
            super(obj);
            letter = obj.letter;
            selectedFor = obj.selectedFor;
        }
        private Object readResolve()
        {
            return new BoardToken(this);
        }
    }
    
    private Object writeReplace()
    {
        return new SerialForm(this);
    }
    
    private void readObject(ObjectInputStream s) throws  IOException, ClassNotFoundException
    {
        throw new NotSerializableException();
    }

    public BoardToken(SerialForm serialForm)
    {
        super(serialForm);
        letter = serialForm.letter;
        selectedFor = serialForm.selectedFor;
    }

    /**
     * @param id
     * @param art
     * @param player
     * @param isPublic
     * @param movable
     * @param position
     */
    public BoardToken(final int id, String libraryId, final String art, final Participant player, final String isPublic, final boolean movable, final IndexPosition position)
    {
        super(id, libraryId, art, player, isPublic, movable, position);
        letter = art;
    }


    @Override
    public com.sawdust.engine.view.game.Token toGwt(final Player access, final TokenGame parametricTokenGame) throws GameException
    {
        final com.sawdust.engine.view.game.Token gwt = super.toGwt(access, parametricTokenGame);
        if (null == gwt) return null;
        if (selectedFor.contains(access.getUserId()))
        {
            gwt.setBaseImageId(gwt.getToggleImageId());
            gwt.setToggleImageId("");
        }
        return gwt;
    }

}
