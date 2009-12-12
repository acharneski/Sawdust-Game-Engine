package com.sawdust.engine.game.wordHunt;

public interface LanguageProvider
{

    String getUrl(String urlString);

    boolean verifyWord(String word);
    
}
