package com.sawdust.engine.view.game;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable
{
    public enum MessageType
    {
        Admin, Compact, Error, Normal, Verbose, Warning
    }

    public static final String ADMIN = "ADMIN";
    public static final String ALL = "*";
    public static final String SYSTEM = "[System]";

    private String _from = SYSTEM;
    private int _id;
    protected String _text;
    protected Date _time;
    private String _to = ALL;
    private MessageType _type = MessageType.Normal;
    public boolean isSocialActivity = false;
    public String fbAttachment = "{}";

    @Deprecated
    public Message()
    {
    }

    /**
     * @param text
     */
    public Message(final String text)
    {
        super();
        _text = text;
        _time = new Date();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final Message other = (Message) obj;
        if (_time == null)
        {
            if (other._time != null) return false;
        }
        else if (!_time.equals(other._time)) return false;
        if (_text == null)
        {
            if (other._text != null) return false;
        }
        else if (!_text.equals(other._text)) return false;
        return true;
    }

    public Date getDateTime()
    {
        // TODO Auto-generated method stub
        return _time;
    }

    public String getFrom()
    {
        return _from;
    }

    public int getId()
    {
        return _id;
    }

    /**
     * @return the text
     */
    public String getText()
    {
        return _text;
    }

    public String getTo()
    {
        return _to;
    }

    public MessageType getType()
    {
        return _type;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_time == null) ? 0 : _time.hashCode());
        result = prime * result + ((_text == null) ? 0 : _text.hashCode());
        return result;
    }

    public boolean isEmpty()
    {
        return (null == _text) || (_text.isEmpty());
    }

    public Message setFrom(final String from)
    {
        _from = from;
        return this;
    }

    public void setId(final int id)
    {
        _id = id;
    }

    public Message setTo(final String to)
    {
        _to = to;
        return this;
    }

    public Message setType(final MessageType type)
    {
        _type = type;
        return this;
    }

    public Message setSocialActivity(boolean b)
    {
        isSocialActivity = b;
        return this;
    }
}
