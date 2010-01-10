/**
 * 
 */
package com.sawdust.client.gwt.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sawdust.client.gwt.util.CommandExecutor;
import com.sawdust.client.gwt.util.Constants;
import com.sawdust.client.gwt.util.EventListener;
import com.sawdust.engine.view.config.GameConfig;
import com.sawdust.engine.view.game.ClientCommand;
import com.sawdust.engine.view.game.GameFrame;
import com.sawdust.engine.view.game.GameLabel;
import com.sawdust.engine.view.game.Notification;
import com.sawdust.engine.view.game.SolidColorGameCanvas;
import com.sawdust.engine.view.game.Token;

/**
 * @author Administrator
 */
public class PlayAreaWidget extends AbsolutePanel
{
    private final VerticalPanel _descriptionWidget = new VerticalPanel();
    private final VerticalPanel _helpWidget = new VerticalPanel();
    private final Timer _redrawTimer = new Timer()
    {
        @Override
        public void run()
        {
            PlayAreaWidget.this.setState(_state);
        }
    };
    private final VerticalPanel _rulesWidget = new VerticalPanel();
    private final CommandExecutor _service;

    private GameFrame _state = null;
    private final DecoratedTabPanel _tabPanel = new DecoratedTabPanel();
    private final HorizontalPanel notifyPanel = new HorizontalPanel();

    public PlayAreaWidget(final CommandExecutor cmdService)
    {
        super();
        _service = cmdService;
        DOM.sinkEvents(getElement(), DOM.getEventsSunk(getElement()) | Event.MOUSEEVENTS);
        this.setStylePrimaryName("sdge-play-gamespace");

        VerticalPanel v1 = new VerticalPanel();
        v1.add(notifyPanel);
        v1.add(this);
        _tabPanel.add(v1, "Game");
        _tabPanel.add(_descriptionWidget, "About");
        _tabPanel.add(_rulesWidget, "Rules");
        _tabPanel.add(_helpWidget, "Help");
        _tabPanel.selectTab(0);
        _tabPanel.setAnimationEnabled(true);

        _tabPanel.addSelectionHandler(new SelectionHandler<Integer>()
        {
            public void onSelection(final SelectionEvent<Integer> event)
            {
                _redrawTimer.schedule(Constants.JS_FOLLOW_UP);
            }
        });
    }

    @Override
    public void add(final Widget child)
    {
        if (null != child.getParent())
        {
            child.removeFromParent();
        }
        super.add(child);
        // if(child instanceof MouseUpHandler)
        // this.addHandler((MouseUpHandler)child, MouseUpEvent.getType());
        if (child instanceof MouseMoveHandler)
        {
            this.addHandler((MouseMoveHandler) child, MouseMoveEvent.getType());
        }
    }

    public HashMap<String, LabelWidget> getLabelIndexById()
    {
        final HashMap<String, LabelWidget> cardIndex = new HashMap<String, LabelWidget>();
        for (final Widget card : this)
        {
            if (card instanceof LabelWidget)
            {
                cardIndex.put(((LabelWidget) card).getLabelId(), (LabelWidget) card);
            }
        }
        return cardIndex;
    }

    public CommandExecutor getService()
    {
        return _service;
    }

    public GameFrame getState()
    {
        if (null == _state)
        {
            _state = new GameFrame();
        }
        _state.clearTokens();
        for (final Widget cardW : this)
        {
            _state.add(((TokenWidget) cardW).getState());
        }
        return _state;
    }

    public DecoratedTabPanel getTabPanel()
    {
        return _tabPanel;
    }

    public HashMap<Integer, TokenWidget> getTokenIndexById()
    {
        final HashMap<Integer, TokenWidget> cardIndex = new HashMap<Integer, TokenWidget>();
        for (final Widget card : this)
        {
            if (card instanceof TokenWidget)
            {
                cardIndex.put(((TokenWidget) card).getTokenId(), (TokenWidget) card);
            }
        }
        return cardIndex;
    }

    @Override
    public boolean remove(final Widget widget)
    {
        widget.setVisible(false);
        return super.remove(widget);
    }

    private void setLabels(final GameFrame v)
    {
        // Add/Overwrite Labels
        final HashMap<String, LabelWidget> lookup = getLabelIndexById();
        final HashSet<String> keysToRemove = new HashSet<String>(lookup.keySet());
        final List<GameLabel> existingItems = new ArrayList<GameLabel>(v.getLabels());
        for (final GameLabel label : existingItems)
        {
            if (lookup.containsKey(label.key))
            {
                final LabelWidget thisLabel = lookup.get(label.key);
                thisLabel.setState(label);
                keysToRemove.remove(label.key);
            }
            else
            {
                final LabelWidget newLabel = new LabelWidget(this);
                PlayAreaWidget.this.add(newLabel);
                newLabel.setState(label);
                lookup.put(label.key, newLabel);
            }
        }
        for (final String key : keysToRemove)
        {
            this.remove(lookup.get(key));
        }
    }

    public void setState(final GameFrame state)
    {
        if ((null == state) || (null == state.getTokens())) return;
        _state = state;

        _descriptionWidget.clear();
        GameConfig config = state.getConfig();
        _descriptionWidget.add(new HTML(config.getGameDescription()));

        _rulesWidget.clear();
        _rulesWidget.add(new HTML(config.getRules()));

        _helpWidget.clear();
        
        Notification notification = _state.getNotification();
        notifyPanel.clear();
        notifyPanel.setStylePrimaryName("sdge-play-notification");
        if(null != notification)
        {
            notifyPanel.clear();
            notifyPanel.add(new HTML(notification.notifyText));
            for(final Entry<String, String> cmd : notification.commands.entrySet())
            {
                Button button = new Button(cmd.getValue());
                notifyPanel.add(button);
                notifyPanel.setWidth("100%");
                notifyPanel.setCellHorizontalAlignment(button, notifyPanel.ALIGN_RIGHT);
                button.addClickHandler(new ClickHandler()
                {
                    public void onClick(ClickEvent event)
                    {
                        _service.doCommand(cmd.getKey(), null);
                        notifyPanel.clear();
                    }
                });
            }
        }

        for (final ClientCommand cmd : state.getCommands())
        {
            _helpWidget.add(new HTML(cmd.getHelp()));
        }

        final String width = state.getWidth() + "px";
        final String height = state.getHeight() + "px";
        _tabPanel.setSize(width, height);
        if(null != state.canvas)
        {
            if(state.canvas instanceof SolidColorGameCanvas)
            {
                this.getElement().getStyle().setBackgroundColor(((SolidColorGameCanvas)state.canvas).color);
                this.getElement().getStyle().setColor(((SolidColorGameCanvas)state.canvas).textColor);
            }
            else
            {
                // Unknown canvas type!
            }
        }
        
        PlayAreaWidget.this.setSize(width, height);

        setLabels(state);
        setTokens(state);
    }

    private void setTokens(final GameFrame state)
    {
        final HashMap<Integer, TokenWidget> lookup = getTokenIndexById();
        final HashSet<Integer> keysToRemove = new HashSet<Integer>(lookup.keySet());
        final List<Token> stateTokens = new ArrayList<Token>(state.getTokens());
        for (final Token token : stateTokens)
        {
            final int cardId = token.getId();
            final TokenWidget newToken;
            if (lookup.containsKey(cardId))
            {
                newToken = lookup.get(cardId);
                newToken.setState(token);
                keysToRemove.remove(cardId);
            }
            else
            {
                newToken = new TokenWidget(this, token.getId());
                this.add(newToken);
                newToken.setState(token);
                lookup.put(cardId, newToken);
            }
        }
        for (final Integer card : keysToRemove)
        {
            this.remove(lookup.get(card));
        }
    }

}
