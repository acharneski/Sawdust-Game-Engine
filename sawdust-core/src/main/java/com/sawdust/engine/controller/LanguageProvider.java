package com.sawdust.engine.controller;

import java.io.Serializable;
import java.util.ArrayList;



public interface LanguageProvider extends Serializable
{

    String getUrl(String urlString, HttpInterface theInternet);

    boolean verifyWord(String word, HttpInterface theInternet);

    ArrayList<String> getWordCharacterSet();

    ArrayList<String> getDelimiterCharacterSet();

    String normalizeString(String b);

    ArrayList<String> tokens(String commandText);
    
}
