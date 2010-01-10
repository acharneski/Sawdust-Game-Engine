package com.sawdust.test;

import java.io.Serializable;

import junit.framework.TestCase;

import org.junit.Test;

import com.sawdust.engine.controller.Util;



public class UtilTests extends TestCase
{

    @Test(timeout = 10000)
    public void testStringObj() throws Exception
    {
        String obj = "I am an opaqe object";
        String string = Util.string(obj);
        System.out.println("string= " + string);
        Serializable unstring = Util.unstring(string);
        System.out.println("unstring= " + unstring);
        assert(obj.equals(unstring));
    }

    @Test(timeout = 10000)
    public void testSerializer() throws Exception
    {
        String obj = "I am an opaqe object";
        byte[] string = Util.toBytes(obj);
        Serializable unstring = Util.fromBytes(string);
        System.out.println("unstring= "+unstring);
        assert(obj.equals(unstring));
    }
}
