/**
 * 
 */
package com.sawdust.client.gwt.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.sawdust.client.gwt.GameClient;
import com.sawdust.client.gwt.art.ArtManager;
import com.sawdust.client.gwt.util.CommandExecutor;
import com.sawdust.client.gwt.util.EventListener;
import com.sawdust.engine.common.game.Token;
import com.sawdust.engine.common.geometry.Position;
import com.sawdust.engine.common.geometry.Vector;

/**
 * @author Administrator
 */
public class TokenWidget extends Image implements MouseMoveHandler, MouseDownHandler, MouseUpHandler

{
    private static final int SNAP_TOLERANCE = 50;
    private boolean _dragging = false;
    private int _dragStartX = 0;
    private int _dragStartY = 0;
    private final int _id;
    private final GameWidget _parent;
    private int _startX;
    private int _startY;
    private Token _state;
    private int _startZ;
    
    /**
	 * 
	 */
    public TokenWidget(final GameWidget parent, final int id)
    {
        super();
        _id = id;
        _parent = parent;
        this.addHandler(this, MouseDownEvent.getType());
        this.addHandler(this, MouseMoveEvent.getType());
        this.addHandler(this, MouseUpEvent.getType());
        DOM.sinkEvents(getElement(), DOM.getEventsSunk(getElement()) | Event.MOUSEEVENTS);
        this.setVisible(true);
    }
    
    public String getArt()
    {
        return _state.getBaseImageId();
    }
    
    protected Position getPosition()
    {
        return new Position(getX(), getY());
    }
    
    public Token getState()
    {
        final Position p = _state.getPosition();
        p.setX(getX());
        p.setY(getY());
        return _state;
    }
    
    public int getTokenId()
    {
        return _id;
    }
    
    public int getX()
    {
        return _parent.getWidgetLeft(this);
    }
    
    public int getY()
    {
        return _parent.getWidgetTop(this);
    }
    
    private Position isNearMoveTarget(final int x, final int y)
    {
        double winningDistance = -1;
        Position winningPosition = null;
        for (final Position p : _state.getMoveCommands().keySet())
        {
            final Position centerPos = p.add(new Vector(-(getWidth() / 2), 0));
            final double d = centerPos.getDistance(x, y);
            if ((winningDistance < 0) || (winningDistance > d))
            {
                winningDistance = d;
                winningPosition = p;
            }
        }
        return ((winningDistance > 0) && (winningDistance < SNAP_TOLERANCE)) ? winningPosition : null;
    }
    
    public boolean onEventPreview(final Event event)
    {
        final boolean isMyEvent = DOM.isOrHasChild(getElement(), DOM.eventGetTarget(event));
        final boolean isMouseEvent = (DOM.eventGetType(event) == Event.ONMOUSEDOWN);
        if (isMouseEvent && isMyEvent)
        {
            DOM.eventPreventDefault(event);
        }
        return true;
    }
    
    public void onMouseDown(final MouseDownEvent event)
    {
        final CommandExecutor service = _parent.getService();
        if (_dragging)
        {
            final Position p = isNearMoveTarget(event.getClientX() + _dragStartX, event.getClientY() + _dragStartY);
            if (null != p)
            {
                final Position centerPos = p.add(new Vector(-(getWidth() / 2), 0));
                _state.setPosition(centerPos);
                setPosition(centerPos);
                _dragging = false;
                final String command = _state.getMoveCommands().get(p);
                setZPosition(_startZ);
                service.doCommand(command, null);
            }
            else
            {
                setPosition(new Position(_startX, _startY));
                _dragging = false;
            }
        }
        else
        {
            if (null != _state.getToggleCommand())
            {
                final AbstractImagePrototype activeImage = ArtManager.Instance.getImage(_state.getImageLibraryId(), _state.getToggleImageId());
                if (null != activeImage)
                {
                    activeImage.applyTo(this);
                }
            }
            else if (_state.isMovable())
            {
                _startX = getX();
                _startY = getY();
                _startZ = getZPosition();
                _dragStartX = getX() - event.getClientX();
                _dragStartY = getY() - event.getClientY();
                _dragging = true;
                GWT.log("onMouseDown X=" + getX() + ";Y=" + getY() + ";", null);
            }
        }
    }
    
    public void onMouseMove(final MouseMoveEvent event)
    {
        if (_dragging)
        {
            final Position p = isNearMoveTarget(event.getClientX() + _dragStartX, event.getClientY() + _dragStartY);
            if (null != p)
            {
                final Position centerPos = p.add(new Vector(-(getWidth() / 2), 0));
                setPosition(centerPos);
                setZPosition(50);
            }
            else
            {
                setPosition(new Position(event.getClientX() + _dragStartX, event.getClientY() + _dragStartY));
                setZPosition(50);
            }
        }
        
    }
    
    public void onMouseUp(final MouseUpEvent event)
    {
        final CommandExecutor service = _parent.getService();
        if (null != _state.getToggleCommand())
        {
            final int lock = service.incrementLock();
            _parent.getService().queueCommand(_state.getToggleCommand(), new EventListener()
            {
                public void onEvent(final Object... params)
                {
                    service.decrementLock(lock);
                }
            });
        }
    }
    
    protected void setPosition(final Position position)
    {
        _parent.setWidgetPosition(this, position.getX(), position.getY());
        int z = position.getZ();
        setZPosition(z);
    }

    private void setZPosition(int z)
    {
        DOM.setStyleAttribute(getElement(), "zIndex", Integer.toString(1 + z));
    }
    
    protected int getZPosition()
    {
        String styleAttribute = DOM.getStyleAttribute(getElement(), "zIndex");
        int i = Integer.parseInt(styleAttribute)-1;
        return i;
    }
    
    public void setState(final Token state)
    {
        if (null == state) return;
        if (_id != state.getId())
        {
            System.err.println("Warning: Id mismatch");
            return;
        }
        try
        {
            if (null != state)
            {
                new Timer()
                { // TODO: Remove this hack; it is needed to make sure the id's
                  // are set on the first load
                    @Override
                    public void run()
                    {
                        ensureDebugId("token_" + state.getId());
                        setTitle(state.getText());
                    }
                }.schedule(50);
                final Position position = state.getPosition();
                if (null != position)
                {
                    final String art = state.getBaseImageId();
                    AbstractImagePrototype image = null;
                    if ((null != art) && !art.isEmpty())
                    {
                        image = ArtManager.Instance.getImage(state.getImageLibraryId(), art);
                        if (null == image)
                        {
                            System.err.println("Image Not Found!");
                        }
                    }
                    else
                    {
                        System.err.println("Null Image!");
                    }
                    if (null != image)
                    {
                        this.setVisible(true);
                        image.applyTo(this);
                        final int clientWidth = getWidth();
                        final Position newPosition = position.add(new Vector(-(clientWidth / 2), 0));
                        newPosition.setZ(position.getZ());
                        if (!newPosition.equals(TokenWidget.this.getPosition()))
                        {
                            setPosition(newPosition);
                        }
                    }
                    else
                    {
                        this.setVisible(false);
                    }
                }
                else
                {
                    this.setVisible(false);
                }
            }
        }
        finally
        {
            _state = state;
        }
    }
    
    public void setZ(final int z)
    {
        DOM.setStyleAttribute(getElement(), "zIndex", Integer.toString(1 + z));
    }
}
