package com.sawdust.engine.service;

import java.io.Serializable;

public interface HttpInterface extends Serializable
{

    HttpResponse getURL(String urlString);

}
