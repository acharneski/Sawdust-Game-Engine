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
import com.sawdust.engine.view.AccessToken;
import com.sawdust.engine.view.GameLocation;
import com.sawdust.engine.view.config.GameConfig;
import com.sawdust.engine.view.config.GameModConfig;
import com.sawdust.engine.view.config.PropertyConfig.DetailLevel;
import com.sawdust.engine.view.config.PropertyConfig.PropertyType;

public class GameCreator implements EntryPoint
{
    private AccessToken _accessKey;
    final Button _close = new Button("Close");;
    final DialogBox _dialogBox = new DialogBox();
    private String _gameName = "";
    private GameConfigWidget _panel = null;
    final HTML _serverResponse = new HTML();
    private final SawdustGameServiceAsync _service = GWT.create(SawdustGameService.class);
    private final Button _createGame = new Button("Create Game");
    
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
        
        _service.getGameTemplate(_accessKey, _gameName, new AsyncCallback<GameConfig>()
        {
            public void onFailure(final Throwable caught)
            {
                showError(caught);
            }
            
            public void onSuccess(final GameConfig config)
            {
                _panel = new GameConfigWidget(config, DetailLevel.Startup);
                _panel.add(_createGame);
                _createGame.addClickHandler(new ClickHandler()
                {
                    public void onClick(final ClickEvent event)
                    {
                        createGame();
                    }
                });
                
                mainPanel.getElement().setInnerHTML("");
                mainPanel.add(_panel);
            }
        });
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
        
    void showError(final Throwable caught)
    {
        _serverResponse.setHTML("<b>" + caught.getMessage() + "</b>");
        _dialogBox.center();
        _close.setFocus(true);
    }
    
    public void createGame()
    {
        _service.createGame(_accessKey, _panel.getConfig(), new AsyncCallback<GameLocation>()
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
}
