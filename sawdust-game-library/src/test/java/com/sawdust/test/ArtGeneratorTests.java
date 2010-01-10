package com.sawdust.test;

import junit.framework.TestCase;

import org.junit.Test;

import com.sawdust.util.art.CardGenerator1;
import com.sawdust.util.art.LetterGenerator1;

public class ArtGeneratorTests extends TestCase
{

    @Test(timeout = 10000)
    public void testCards() throws Exception
    {
        new CardGenerator1("src/main/art", "target/test/art").createLibrary();
    }

    @Test(timeout = 10000)
    public void testLetters() throws Exception
    {
        new LetterGenerator1("src/main/art", "target/test/art").createLibrary();
    }
}
