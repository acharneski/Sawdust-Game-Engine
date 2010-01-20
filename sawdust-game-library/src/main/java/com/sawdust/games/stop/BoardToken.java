package com.sawdust.games.stop;

import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.state.IndexPosition;
import com.sawdust.engine.model.state.Token;

public class BoardToken extends Token
{
    protected final class SerialForm extends Token.SerialForm 
    {}
    
    
    public BoardToken()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    public BoardToken(int id, String libararyId, String art, Participant player, String publicArt, boolean movable, IndexPosition position)
    {
        super(id, libararyId, art, player, publicArt, movable, position);
        // TODO Auto-generated constructor stub
    }

    public BoardToken(SerialForm obj)
    {
        super(obj);
    }
    

}
