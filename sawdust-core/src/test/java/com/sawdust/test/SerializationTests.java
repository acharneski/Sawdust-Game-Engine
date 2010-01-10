package com.sawdust.test;

import java.io.Serializable;

import junit.framework.TestCase;

import org.junit.Test;

import com.sawdust.engine.controller.Util;
import com.sawdust.engine.model.state.IndexCard;
import com.sawdust.engine.view.cards.Card;
import com.sawdust.engine.view.cards.CardDeck;
import com.sawdust.engine.view.cards.SpecialCard;



public class SerializationTests extends TestCase
{

    @Test(timeout = 10000)
    public void testCardDeck() throws Exception
    {
        CardDeck obj = new CardDeck();
        serializationTests(obj);
    }

    @Test(timeout = 10000)
    public void testToken() throws Exception
    {
        serializationTests(new com.sawdust.engine.model.state.Token());
    }

    @Test(timeout = 10000)
    public void testIndexToken() throws Exception
    {
        serializationTests(new IndexCard(1,null,null,false,null,new Card(SpecialCard.FaceDown)));
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
