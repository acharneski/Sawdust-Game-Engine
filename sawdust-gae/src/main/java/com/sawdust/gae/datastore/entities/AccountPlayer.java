/**
 * 
 */
package com.sawdust.gae.datastore.entities;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.logging.Logger;

import com.sawdust.engine.model.players.AccountFactory;
import com.sawdust.engine.model.players.Player;

public final class AccountPlayer extends Player
{
    private static final Logger LOG = Logger.getLogger(Object.class.getName());

    protected static class SerialForm extends Player.SerialForm implements Serializable
    {
        protected SerialForm(){}
        protected SerialForm(Player obj)
        {
            super(obj);
        }

        private Object readResolve()
        {
            return new AccountPlayer(this);
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
    
    public AccountPlayer(SerialForm serialForm)
    {
        super(serialForm);
    }

    public AccountPlayer(final Account account2)
    {
        super(account2.getUserId(), account2.isAdmin(), new AccountFactory()
        {
            @Override
            public Account getAccount()
            {
                return Account.Load(account2.getUserId());
            }
        });
    }
}
