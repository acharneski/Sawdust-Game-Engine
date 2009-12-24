package com.sawdust.engine.game;

import java.io.Serializable;

public interface HttpInterface extends Serializable
{

    HttpResponse getURL(String urlString);

}
