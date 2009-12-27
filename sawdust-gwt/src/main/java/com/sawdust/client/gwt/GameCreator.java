package com.sawdust.client.gwt;

import java.util.Collection;
import java.util.HashMap;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sawdust.client.gwt.util.Constants;
import com.sawdust.client.gwt.util.GoogleAccess;
import com.sawdust.common.gwt.SawdustGameService;
import com.sawdust.common.gwt.SawdustGameServiceAsync;
import com.sawdust.engine.common.AccessToken;
import com.sawdust.engine.common.GameLocation;
import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.common.config.GameModConfig;
import com.sawdust.engine.common.config.PropertyConfig;
import com.sawdust.engine.common.config.PropertyConfig.PropertyType;

public class GameCreator implements EntryPoint
{
    private class BooleanValueMapper implements ValueChangeHandler<Boolean>
    {
        PropertyConfig _property = null;
        
        public BooleanValueMapper(final PropertyConfig property)
        {
            super();
            _property = property;
            property.value = property.defaultValue;
        }
        
        public void onValueChange(final ValueChangeEvent<Boolean> event)
        {
            _property.value = event.getValue() ? PropertyConfig.TRUE : PropertyConfig.FALSE;
        }
    }
    
    private class StringValueMapper implements ValueChangeHandler<String>
    {
        PropertyConfig _property = null;
        
        public StringValueMapper(final PropertyConfig property)
        {
            super();
            _property = property;
            property.value = property.defaultValue;
        }
        
        public void onValueChange(final ValueChangeEvent<String> event)
        {
            _property.value = event.getValue();
        }
    }
    
    private AccessToken _accessKey;
    final Button _close = new Button("Close");;
    private GameConfig _config = null;
    private final Button _createGame = new Button("Create Game");
    final DialogBox _dialogBox = new DialogBox();
    private String _gameName = "";
    private VerticalPanel _panel = new VerticalPanel();
    final HTML _serverResponse = new HTML();
    private final SawdustGameServiceAsync _service = GWT.create(SawdustGameService.class);
    
    private void createGame()
    {
        _service.createGame(_accessKey, _config, new AsyncCallback<GameLocation>()
        {
            public void onFailure(final Throwable caught)
            {
                showError(caught);
            }
            
            public void onSuccess(final GameLocation game)
            {
                if (null != game)
                {
                    String redirectUrl = game.getRedirectUrl();
                    if (null != redirectUrl) Window.open(redirectUrl, "_top", null);
                }
            }
        });
    }
    
    private DisclosurePanel createModuleBox(final GameModConfig module, final Widget x)
    {
        final String name = module.getName();
        final CheckBox header = new CheckBox(name);
        final DisclosurePanel d = new DisclosurePanel(header, module.isEnabled());
        d.addCloseHandler(new CloseHandler<DisclosurePanel>()
        {
            public void onClose(final CloseEvent<DisclosurePanel> event)
            {
                module.setEnabled(false);
                new Timer()
                {
                    @Override
                    public void run()
                    {
                        header.setValue(false);
                    }
                }.schedule(Constants.JS_FOLLOW_UP);
            }
        });
        d.addOpenHandler(new OpenHandler<DisclosurePanel>()
        {
            public void onOpen(final OpenEvent<DisclosurePanel> event)
            {
                module.setEnabled(true);
                new Timer()
                {
                    @Override
                    public void run()
                    {
                        header.setValue(true);
                    }
                }.schedule(Constants.JS_FOLLOW_UP);
            }
        });
        header.setValue(module.isEnabled());
        d.add(x);
        return d;
    }
    
    private void initErrorHandler()
    {
        _close.getElement().setId("sdge-play-errorDialog-closeButton");
        final VerticalPanel dialogVPanel = new VerticalPanel();
        dialogVPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        dialogVPanel.add(_serverResponse);
        dialogVPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        dialogVPanel.add(_close);
        _dialogBox.setWidget(dialogVPanel);
        _dialogBox.setAnimationEnabled(true);
        _dialogBox.setText("Error");
        _dialogBox.setStylePrimaryName("sdge-play-errorDialog");
        // Add a handler to close the DialogBox
        _close.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                _dialogBox.hide();
            }
        });
    }
    
    private void initMainPanel(final RootPanel mainPanel)
    {
        _createGame.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                createGame();
            }
        });
        
        _service.getGameTemplate(_accessKey, _gameName, new AsyncCallback<GameConfig>()
        {
            public void onFailure(final Throwable caught)
            {
                showError(caught);
            }
            
            public void onSuccess(final GameConfig result)
            {
                _config = result;
                _panel = GameCreator.this.renderGameTemplate(result);
                mainPanel.getElement().setInnerHTML("");
                mainPanel.add(_panel);
            }
        });
    }
    
    private void instrumentRow(final Grid propertyTable, final PropertyConfig property, final int row)
    {
        final Widget w = propertyTable.getWidget(row, 1);
        final HorizontalPanel h = new HorizontalPanel();
        if (null != property.suffix)
        {
            h.add(w);
            h.add(new HTML("&nbsp " + property.suffix));
        }
        if (null != property.description)
        {
            final HTML header = new HTML("(explain)");
            header.setStylePrimaryName("sdge-help-subtitle");
            final DisclosurePanel disclosure = new DisclosurePanel(header, false);
            disclosure.setAnimationEnabled(true);
            disclosure.setContent(new HTML(property.description));
            disclosure.setStylePrimaryName("sdge-help-float");
            h.add(disclosure);
        }
        if (h.getWidgetCount() > 1)
        {
            propertyTable.setWidget(row, 1, h);
        }
    }
    
    public void onModuleLoad()
    {
        _gameName = Window.Location.getParameter("game");
        
        final RootPanel mainPanel = RootPanel.get("gameCreator-main");
        if (null != mainPanel)
        {
            _accessKey = GoogleAccess.getAccessToken(mainPanel);
            initMainPanel(mainPanel);
            initErrorHandler();
        }
        
        new Timer(){
            @Override
            public void run()
            {
                final RootPanel quickLaunchPanel = RootPanel.get("gameCreator-quickLaunch");
                final Button playNowButton = new Button("Play Now!");
                playNowButton.addClickHandler(new ClickHandler()
                {
                    public void onClick(final ClickEvent event)
                    {
                        playNowButton.setEnabled(false);
                        playNowButton.setText("Entering Game...");
                        createGame();
                    }
                });
                quickLaunchPanel.add(playNowButton);
            }}.schedule(500);
    }
    
    private VerticalPanel renderGameTemplate(final GameConfig result)
    {
        final VerticalPanel panel = new VerticalPanel();
        final HashMap<String, PropertyConfig> properties = result.getProperties();
        final Grid renderProperties = renderProperties(properties.values());
        panel.add(renderProperties);
        
        for (final GameModConfig module : result.getModules())
        {
            final VerticalPanel x = new VerticalPanel();
            x.add(new HTML(module.getDescription()));
            x.add(renderProperties(module.getProperties().values()));
            panel.add(createModuleBox(module, x));
        }
        
        panel.add(_createGame);
        panel.setStylePrimaryName("sdge-game-listing");
        return panel;
    }
    
    private Grid renderProperties(final Collection<PropertyConfig> properties)
    {
        final Grid propertyTable = new Grid(0, 2);
        for (final PropertyConfig property : properties)
        {
            final int row = propertyTable.insertRow(propertyTable.getRowCount());
            final HTML label = new HTML(property.key + ":&nbsp ");
            label.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
            label.setWidth("");
            propertyTable.setWidget(row, 0, label);
            propertyTable.setWidget(row, 1, renderPropertyValue(property));
            instrumentRow(propertyTable, property, row);
        }
        return propertyTable;
    }
    
    private Widget renderPropertyValue(final PropertyConfig property)
    {
        if (PropertyType.Text == property.type)
        {
            final TextBox newTextBox = new TextBox();
            newTextBox.setTextAlignment(TextBoxBase.ALIGN_LEFT);
            newTextBox.setWidth("20em");
            newTextBox.setValue(property.defaultValue);
            newTextBox.addValueChangeHandler(new StringValueMapper(property));
            return newTextBox;
        }
        else if (PropertyType.Number == property.type)
        {
            final TextBox newTextBox = new TextBox();
            newTextBox.setTextAlignment(TextBoxBase.ALIGN_LEFT);
            newTextBox.setWidth("5em");
            newTextBox.setValue(property.defaultValue);
            newTextBox.addValueChangeHandler(new StringValueMapper(property));
            return newTextBox;
        }
        else if (PropertyType.Boolean == property.type)
        {
            final CheckBox cb = new CheckBox();
            cb.addValueChangeHandler(new BooleanValueMapper(property));
            cb.setValue(0 == property.defaultValue.compareToIgnoreCase("true"));
            return cb;
        }
        else throw new RuntimeException("Upsupported Type: " + property.type);
    }
    
    void showError(final Throwable caught)
    {
        _serverResponse.setHTML("<b>" + caught.getMessage() + "</b>");
        _dialogBox.center();
        _close.setFocus(true);
    }
}
