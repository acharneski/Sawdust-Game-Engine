package com.sawdust.engine.controller;

public class HttpResponse
{

    private String _content;
    private int _statusCode;

    public HttpResponse(String content, int statusCode)
    {
        super();
        _content = content;
        this.setStatusCode(statusCode);
    }

    public String getContent()
    {
        return _content;
    }

    private void setStatusCode(int statusCode)
    {
        this._statusCode = statusCode;
    }

    public int getStatusCode()
    {
        return _statusCode;
    }

}
