package com.sawdust.engine.controller;

import java.io.Serializable;

public interface HttpInterface extends Serializable
{

    HttpResponse getURL(String urlString);

}
