package com.sawdust.engine.model.players;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.logging.Logger;

import com.sawdust.engine.controller.entities.Account;
import com.sawdust.engine.view.game.ActivityEvent;

public class Player extends Participant implements Serializable
{
    private static final Logger LOG = Logger.getLogger(Player.class.getName());

    protected static class SerialForm extends Participant.SerialForm implements Serializable
    {
        boolean _isAdmin;
        AccountFactory _account;
        
        protected SerialForm(){}
        protected SerialForm(Player obj)
        {
            super(obj);
            _isAdmin = obj._isAdmin;
            _account = obj._account;
        }

        private Object readResolve()
        {
            return new Player(this);
        }
    }

    private void readObject(ObjectInputStream s) throws  IOException, ClassNotFoundException
    {
        throw new NotSerializableException();
    }

    private Object writeReplace()
    {
        return new SerialForm(this);
    }

    protected boolean _isAdmin;
    protected AccountFactory _account;

    protected Player(SerialForm obj)
    {
        super((Participant.SerialForm)obj);
        _isAdmin = obj._isAdmin;
        _account = obj._account;
    }

    protected Player(final String userId, final boolean isAdmin, AccountFactory accountFactory)
    {
        super(userId);
        _isAdmin = isAdmin;
        _account = accountFactory;
    }

    public String getUserId()
    {
        return getId();
    }

    public boolean isAdmin()
    {
        return _isAdmin;
    }

    public void doLogActivity(ActivityEvent event)
    {
        LOG.info(String.format("Event: %s: %s",getUserId(),event.toString()));
        getAccount().doLogActivity(event);
    }

    public Account getAccount()
    {
        return _account.getAccount();
    }

}
