package com.sawdust.engine.game;

import java.io.Serializable;


public interface LanguageProvider extends Serializable
{

    String getUrl(String urlString, HttpInterface theInternet);

    boolean verifyWord(String word, HttpInterface theInternet);
    
}
