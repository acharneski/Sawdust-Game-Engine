package com.sawdust.engine.common.game;

import java.io.Serializable;
import java.util.HashMap;

import com.sawdust.engine.common.geometry.Position;

public class Token implements Serializable
{
    private Position _position;
    private String text = "";
    private HashMap<String, String> contextCommands = new HashMap<String, String>();
    private int id = 0;
    private boolean movable = false;
    private HashMap<Position, String> moveCommands = new HashMap<Position, String>();
    private String baseImageId = "";
    private String toggleImageId = null;
    private String toggleCommand = null;
    private String imageLibraryId = null;

    public Token()
    {
    }

    public Token(final int id2, final String imageLibrary, final String art2)
    {
        id = id2;
        imageLibraryId = imageLibrary;
        baseImageId = art2;
    }

    /**
     * @return the _art
     */
    public String getBaseImageId()
    {
        return baseImageId;
    }

    /**
     * @return the contextCommands
     */
    public HashMap<String, String> getContextCommands()
    {
        return contextCommands;
    }

    /**
     * @return the distance to the point
     */
    public double getDistance(final double x2, final double y2)
    {
        final double yd = y2 - _position.getY();
        final double xd = x2 - _position.getX();
        return Math.sqrt(yd * yd + xd * xd);
    }

    /**
     * @return the _id
     */
    public int getId()
    {
        return id;
    }

    /**
     * @return the moveCommands
     */
    public HashMap<Position, String> getMoveCommands()
    {
        return moveCommands;
    }

    public Position getPosition()
    {
        return _position;
    }

    /**
     * @return the x
     */
    public int getX()
    {
        return _position.getX();
    }

    /**
     * @return the y
     */
    public int getY()
    {
        return _position.getY();
    }

    /**
     * @return the _movable
     */
    public boolean isMovable()
    {
        return movable;
    }

    /**
     * @param _art
     *            the _art to set
     */
    public void setBaseImageId(final String part)
    {
        baseImageId = part;
    }

    /**
     * @param _movable
     *            the _movable to set
     */
    public void setMovable(final boolean pmovable)
    {
        movable = pmovable;
    }

    public void setPosition(final Position toGwt)
    {
        _position = toGwt;
    }

    private void setContextCommands(HashMap<String, String> pcontextCommands)
    {
        this.contextCommands = pcontextCommands;
    }

    private void setMoveCommands(HashMap<Position, String> pmoveCommands)
    {
        this.moveCommands = pmoveCommands;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getText()
    {
        return text;
    }

   public void setToggleImageId(String toggleImageId)
   {
      this.toggleImageId = toggleImageId;
   }

   public String getToggleImageId()
   {
      return toggleImageId;
   }

   public void setToggleCommand(String toggleCommand)
   {
      this.toggleCommand = toggleCommand;
   }

   public String getToggleCommand()
   {
      return toggleCommand;
   }

   public void setImageLibraryId(String imageLibraryId)
   {
      this.imageLibraryId = imageLibraryId;
   }

   public String getImageLibraryId()
   {
      return imageLibraryId;
   }
}
