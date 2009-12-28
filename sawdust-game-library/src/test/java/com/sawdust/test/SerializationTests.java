package com.sawdust.test;

import java.io.Serializable;

import junit.framework.TestCase;

import org.junit.Test;

import com.sawdust.engine.common.cards.CardDeck;
import com.sawdust.engine.game.wordHunt.BoardToken;
import com.sawdust.engine.service.Util;



public class SerializationTests extends TestCase
{

    @Test(timeout = 10000)
    public void testBoardToken() throws Exception
    {
        serializationTests(new BoardToken(1,"fakelib", "noart", null, null, false, null));
    }

    private <T extends Serializable> void serializationTests(T obj)
    {
        byte data[] = Util.toBytes(obj);
        System.out.println("Serialized: " + obj.toString());
        T unserialized = (T) Util.fromBytes(data);
        if(obj.equals(unserialized))
        {
            System.out.println("Verified: " + obj.toString());
        }
        else
        {
            throw new RuntimeException("Not Serializable: " + obj.toString());
        }
    }
}
