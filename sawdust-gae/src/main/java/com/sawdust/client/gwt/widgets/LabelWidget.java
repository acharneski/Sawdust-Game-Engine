package com.sawdust.client.gwt.widgets;

import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.sawdust.client.gwt.util.EventListener;
import com.sawdust.engine.common.game.GameLabel;
import com.sawdust.engine.common.geometry.Position;
import com.sawdust.engine.common.geometry.Vector;

/**
 * @author Administrator
 */
public class LabelWidget extends SimplePanel implements MouseUpHandler

{
    private String _command = null;
    private Widget _innerWidget;
    private String _labelId = null;
    private final GameWidget gameWidget;
    
    /**
	 * 
	 */
    public LabelWidget(final GameWidget parentP)
    {
        super();
        gameWidget = parentP;
        this.addHandler(this, MouseUpEvent.getType());
        DOM.setStyleAttribute(getElement(), "position", "absolute");
        DOM.sinkEvents(getElement(), DOM.getEventsSunk(getElement()) | Event.MOUSEEVENTS);
    }
    
    public String getLabelId()
    {
        return _labelId;
    }
    
    protected Position getPosition()
    {
        return new Position(gameWidget.getWidgetLeft(this), gameWidget.getWidgetTop(this));
    }
    
    public void onMouseUp(final MouseUpEvent event)
    {
        if (null != _command)
        {
            gameWidget.getService().doCommand(_command, new EventListener()
            {
                public void onEvent(final Object... params)
                {
                    if (_innerWidget instanceof Button)
                    {
                        ((Button) _innerWidget).setEnabled(true);
                    }
                }
            });
            if (_innerWidget instanceof Button)
            {
                ((Button) _innerWidget).setEnabled(false);
            }
        }
    }
    
    protected void setPosition(final Position position)
    {
        gameWidget.setWidgetPosition(this, position.getX(), position.getY());
    }
    
    public void setState(final GameLabel state)
    {
        if (null == state) return;
        // this.setX(v.position.getX() - (v.width / 2));
        // Element element = getElement();
        final Position position = state.position;
        _command = state.command;
        _labelId = state.key;
        clear();
        if (null == state.command)
        {
            _innerWidget = new HTML(state.text);
            setWidget(_innerWidget);
            _innerWidget.setStylePrimaryName("sdge-play-gamelabel");
            if (state.width > 0)
            {
                DOM.setStyleAttribute(_innerWidget.getElement(), "width", Integer.toString(state.width) + "px");
            }
            this.ensureDebugId("label_" + state.key);
        }
        else
        {
            _innerWidget = new Button(state.text);
            setWidget(_innerWidget);
            _innerWidget.setStylePrimaryName("sdge-play-gamebutton");
            this.ensureDebugId("button_" + state.key);
        }
        
        final int clientWidth = _innerWidget.getElement().getClientWidth();
        final Position newPosition = position.add(new Vector(-clientWidth / 2, 0));
        if (!newPosition.equals(LabelWidget.this.getPosition()))
        {
            LabelWidget.this.setPosition(newPosition);
        }
    }
    
}
