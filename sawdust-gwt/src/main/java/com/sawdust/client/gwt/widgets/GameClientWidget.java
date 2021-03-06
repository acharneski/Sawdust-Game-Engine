package com.sawdust.client.gwt.widgets;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.sawdust.client.gwt.util.Command;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sawdust.client.gwt.GameConfigWidget;
import com.sawdust.client.gwt.util.CommandExecutor;
import com.sawdust.client.gwt.util.Constants;
import com.sawdust.client.gwt.util.EventListener;
import com.sawdust.client.gwt.util.FacebookLogic;
import com.sawdust.client.gwt.util.GoogleAccess;
import com.sawdust.engine.view.CommandResult;
import com.sawdust.engine.view.config.GameConfig;
import com.sawdust.engine.view.config.PropertyConfig.DetailLevel;
import com.sawdust.engine.view.game.ClientCommand;
import com.sawdust.engine.view.game.GameFrame;
import com.sawdust.engine.view.game.Message;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GameClientWidget
{
    public static final String CMD_UPDATE = "Update";
    static final int jsWorkaroundDelay = 50;
    private static final int START_TIME = 50;
    public static boolean DEBUG = false;

    
    private final Button _closeButton = new Button("Close");
    private final ConsoleWidget _consoleWidget = new ConsoleWidget();
    private final DialogBox _dialogBox = new DialogBox();
    private final PlayAreaWidget _gameWidget;
    private final MultiWordSuggestOracle _oracle = new MultiWordSuggestOracle();
    private final HTML _serverResponseLabel = new HTML();
    private final SuggestBox _suggestBox = new SuggestBox(_oracle);
    private final Label _updateStatusLabel = new Label();

    private int _commandCursorLength;
    private int _commandCursorPos;
    private boolean _isFocused = false;
    private boolean _wasFocused;
    private GameFrame _currentState = null;
    private String _previousText;
    
    private final mylog LOG = new mylog(){

        @Override
        public void debug(String string)
        {
            if(DEBUG)
            {
                _consoleWidget.addMessage(new Message(string));
            }
        }};

    final CommandExecutor cmdService = new CommandExecutor(LOG,
    /* Success Hook */
    new EventListener()
    {
        public void onEvent(final Object... params)
        {
            final CommandResult result = (CommandResult) params[0];
            defaultOnSuccess(result);
        }
    },
    /* Error Hook */
    new EventListener()
    {
        public void onEvent(final Object... params)
        {
            if(DEBUG)
            {
                String timeString = new Date().toLocaleString();
                _consoleWidget.addMessage(new Message("Error at " + timeString + ": " + params[0].toString()));
            }
            if (params[0] instanceof Throwable)
            {
                GameClientWidget.this.showServiceError((Throwable) params[0], (EventListener) params[1]);
            }
            else
            {
                GameClientWidget.this.showApplicationError((String) params[0], (EventListener) params[1]);
            }
        }
    },
    /* Pre-send Hook */
    new EventListener()
    {
        public void onEvent(final Object... params)
        {
            LOG.debug("Server method called at " + new Date().toLocaleString());
            refocusTimer.schedule(Constants.JS_FOLLOW_UP);
        }
    },
    /* Post-send Hook */
    new EventListener()
    {
        public void onEvent(final Object... params)
        {
            GameClientWidget.this.backupCommandStatus();
        }
    });

    EventListener onErrorClose = null;

    final Timer refocusTimer = new Timer()
    {
        @Override
        public void run()
        {
            // suggestBox.getTextBox().setReadOnly(false);
            if (_wasFocused)
            {
                if (!_isFocused)
                {
                    _suggestBox.getTextBox().setFocus(true);
                }
                if (_previousText.equals(_suggestBox.getTextBox().getText()))
                {
                    final int txtLength = _suggestBox.getTextBox().getText().length();
                    if ((_commandCursorPos >= 0) && (_commandCursorPos < txtLength))
                    {
                        if ((_commandCursorLength >= 0) && ((_commandCursorPos + _commandCursorLength) <= txtLength))
                        {
                            _suggestBox.getTextBox().setSelectionRange(_commandCursorPos, _commandCursorLength);
                        }
                        else
                        {
                            _suggestBox.getTextBox().setCursorPos(_commandCursorPos);
                        }
                    }
                }
            }
        }
    };

    int refreshDelayMillis = 5000;
    final Timer refreshTimer = new Timer()
    {
        @Override
        public void run()
        {
            if(DEBUG)
            {
                String timeString = new Date().toLocaleString();
                _consoleWidget.addMessage(new Message("Refresh poll at " + timeString));
            }
            final GameFrame state = _currentState;
            final int versionNumber = (null == state) ? 0 : state.versionNumber;
            cmdService.doUpdate(versionNumber, null);
        }
    };
    
    
    static final String FILTER_KEY = "TurnConfig";

    public GameClientWidget(final RootPanel rootPanel)
    {
        cmdService.setAccessKey(GoogleAccess.getAccessToken(rootPanel));
        _gameWidget = new PlayAreaWidget(cmdService);
        buildDialog(_dialogBox, _closeButton, _serverResponseLabel);
        final VerticalPanel vPanel = buildMainPanel();
        rootPanel.add(vPanel);
        _oracle.clear();
        setupAjaxLoader();
        cmdService.getPreCommand().add(new EventListener()
        {
            
            @Override
            public void onEvent(Object... params)
            {
                final Command command = (Command) params[0];
                if(command.supress) return;
                if(command.filterStatus.containsKey(FILTER_KEY)) return;
                command.filterStatus.put(FILTER_KEY,false);
                if(command.command.equals("Deal"))
                {
                    final GameConfigWidget gameConfigWidget = new GameConfigWidget(_currentState.getConfig(), DetailLevel.Spam);
                    final SawdustDialogBox configDialog = new SawdustDialogBox(gameConfigWidget, new EventListener()
                    {
                        @Override
                        public void onEvent(Object... params)
                        {
                            GameConfig config = gameConfigWidget.getConfig();
                            cmdService.setGameConfig(config, new EventListener()
                            {
                                @Override
                                public void onEvent(Object... params)
                                {
                                    command.filterStatus.put(FILTER_KEY,true);
                                    command.supress = false;
                                    cmdService.doQueue();
                                }
                            });
                        }
                    });
                    configDialog.center();
                    configDialog.setTitle("Confirm Game Configuration");
                    configDialog.setText("Please confirm game settings:");
                    configDialog.show();
                    configDialog.buttonOk.setFocus(true);
                    command.supress = true;
                }
            }
        });
        cmdService.getGame(new EventListener()
        {
            public void onEvent(Object... params)
            {
                refreshTimer.scheduleRepeating(refreshDelayMillis);
            }
        });
    }

    final Image image = new Image("/media/ajax-loader.gif");
    private void setupAjaxLoader()
    {
        _gameWidget.add(image, 0, 0);
        DOM.setStyleAttribute(image.getElement(), "zIndex", Integer.toString(100));
        image.setVisible(false);
        cmdService.getPreCommand().add(new EventListener(){
            @Override
            public void onEvent(Object... params)
            {
                image.setVisible(true);
            }});
        cmdService._onComplete.add(new EventListener(){

            @Override
            public void onEvent(Object... params)
            {
                image.setVisible(false);
            }});
    }

    private VerticalPanel buildMainPanel()
    {
        final VerticalPanel vPanel = new VerticalPanel();
        final DockPanel commandPanel = buildCommandPanel();
        vPanel.add(_gameWidget.getTabPanel());
        vPanel.add(commandPanel);
        vPanel.add(_consoleWidget);
        vPanel.add(_updateStatusLabel);
        return vPanel;
    }

    void backupCommandStatus()
    {
        _commandCursorLength = _suggestBox.getTextBox().getSelectionLength();
        _commandCursorPos = _suggestBox.getTextBox().getCursorPos();
        _previousText = _suggestBox.getTextBox().getText();
        _wasFocused = _isFocused;
    }

    private void defaultOnSuccess(final CommandResult result)
    {
        int frameCount = 0;
        final List<GameFrame> stateFrames = result.getStateFrames();
        Collections.sort(stateFrames, new Comparator<GameFrame>()
        {
            public int compare(final GameFrame o1, final GameFrame o2)
            {
                return ((Integer) o1.versionNumber).compareTo(o2.versionNumber);
            }
        });
        int framesQueued = 0;
        int currentTime = Integer.MAX_VALUE;
        for (final GameFrame state : stateFrames)
        {
            if (state != null && state.timeOffset < currentTime)
            {
                currentTime = state.timeOffset;
            }
        }
        currentTime -= START_TIME;
        final HashMap<Integer, Timer> frameQueue = new HashMap<Integer, Timer>();
        for (final GameFrame state : stateFrames)
        {
            if (null == state)
            {
                break;
            }
            final int thisIdx = frameCount++;
            final Timer timer = new Timer()
            {
                @Override
                public void run()
                {
                    LOG.debug("Display frame " + state.versionNumber);
                    showFrame(state);
                }
            };
            final int timeSpan = state.timeOffset - currentTime;
            frameQueue.put(timeSpan, timer);
            LOG.debug("Queing frame " + state.versionNumber + " in " + timeSpan + "ms");
        }
        if (1 == frameQueue.size())
        {
            final Timer finalState = frameQueue.get(START_TIME);
            if (null != finalState)
            {
                LOG.debug("Display the returned state.");
                finalState.run();
            }
            else
            {
                LOG.debug("No new game frame availible to display");
                String text = "";
                String timeString = new Date().toLocaleString();
                text = "Updated at " + timeString;
                text += "<br/>Current Version: " + _currentState.versionNumber;
                if(DEBUG)
                {
                    _consoleWidget.addMessage(new Message("Game state reconfirmed at " + timeString + " with version " + _currentState.versionNumber));
                }
                _updateStatusLabel.getElement().setInnerHTML(text);
            }
        }
        else if (0 < frameQueue.size())
        {
            for (final Entry<Integer, Timer> entry : frameQueue.entrySet())
            {
                final int key = entry.getKey();
                final Timer timer = entry.getValue();
                LOG.debug("Schedule Frame in " + key + "ms");
                try
                {
                    timer.schedule(key);
                }
                catch (final Throwable e)
                {
                    e.printStackTrace(System.err);
                }
            }
        }

        final int updateTime = ((null == _currentState) ? 5 : _currentState.updateTime) * 1000;
        if (refreshDelayMillis != updateTime)
        {
            refreshDelayMillis = updateTime;
            refreshTimer.cancel();
            if (updateTime > 0) refreshTimer.scheduleRepeating(refreshDelayMillis);
        }
    }

    public GameFrame getGame()
    {
        _currentState = _gameWidget.getState();
        return _currentState;
    }

    private DockPanel buildCommandPanel()
    {
        final DockPanel commandPanel = new DockPanel();
        final Button runButton = new Button("Run");
        final Label label = new Label("Command: ");

        buildSuggestBox(runButton, _suggestBox);

        commandPanel.add(label, DockPanel.WEST);
        commandPanel.add(_suggestBox, DockPanel.CENTER);
        commandPanel.add(runButton, DockPanel.EAST);

        commandPanel.setWidth("100%");
        commandPanel.setCellHorizontalAlignment(label, HasHorizontalAlignment.ALIGN_LEFT);
        commandPanel.setCellWidth(_suggestBox, "100%");
        commandPanel.setCellHorizontalAlignment(runButton, HasHorizontalAlignment.ALIGN_RIGHT);
        
        return commandPanel;
    }

    private void buildDialog(final DialogBox dialogBox, final Button button, final HTML dialogMessage)
    {
        // Setup error dialog
        final VerticalPanel dialogVPanel = new VerticalPanel();
        dialogVPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        dialogVPanel.add(dialogMessage);
        dialogVPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        dialogVPanel.add(button);
        dialogBox.setWidget(dialogVPanel);
        dialogBox.setAnimationEnabled(true);
        dialogBox.setText("Error");
        // Add a handler to close the DialogBox
        button.getElement().setId("closeButton");
        button.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                dialogBox.hide();
                if (null != onErrorClose)
                {
                    onErrorClose.onEvent(event);
                }
            }
        });
    }

    private void buildSuggestBox(final Button runButton, final SuggestBox suggestBox)
    {
        suggestBox.setWidth("100%");

        runButton.addClickHandler(new ClickHandler()
        {

            public void onClick(final ClickEvent event)
            {
                runButton.setEnabled(false);
                cmdService.doCommand(suggestBox.getText(), new EventListener()
                {
                    public void onEvent(final Object... params)
                    {
                        runButton.setEnabled(true);
                    }
                });
            }
        });
        suggestBox.addKeyUpHandler(new KeyUpHandler()
        {
            public void onKeyUp(final KeyUpEvent event)
            {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
                {
                    if (suggestBox.getTextBox().isEnabled())
                    {
                        suggestBox.getTextBox().setEnabled(false);
                        cmdService.doCommand(suggestBox.getText(), new EventListener()
                        {
                            public void onEvent(final Object... params)
                            {
                                suggestBox.getTextBox().setEnabled(true);
                                suggestBox.setText("");
                                suggestBox.getTextBox().setFocus(true);
                            }
                        });
                    }
                }
            }
        });
        suggestBox.getTextBox().setFocus(true);

        suggestBox.getTextBox().addFocusHandler(new FocusHandler()
        {
            public void onFocus(final FocusEvent event)
            {
                _isFocused = true;
            }
        });
        suggestBox.getTextBox().addBlurHandler(new BlurHandler()
        {
            public void onBlur(final BlurEvent event)
            {
                _isFocused = false;
            }
        });
        
    }

    public void showApplicationError(final String caught, final EventListener post)
    {
        DOM.setStyleAttribute(_dialogBox.getElement(), "zIndex", Integer.toString(100));
        _dialogBox.setPopupPosition(350, 300);
        _dialogBox.setText("Application Error");
        _serverResponseLabel.setHTML("<b>" + caught + "</b>");
        _dialogBox.show();
        // _dialogBox.center();
        _closeButton.setFocus(true);
        onErrorClose = post;
    }

    public void showServiceError(final Throwable caught, final EventListener post)
    {
        _dialogBox.setPopupPosition(100, 100);
        _dialogBox.setText("Service Error");
        _serverResponseLabel.setHTML("<b>" + caught.getMessage() + "</b>");
        // _dialogBox.center();
        _closeButton.setFocus(true);
        onErrorClose = post;
    }

    public void setGame(final GameFrame game)
    {
        if (null == game) return;
        int messagesSince = -1;
        if (null != _currentState)
        {
            messagesSince = _currentState.getLastMessageID();
        }
        _currentState = game;
        _gameWidget.setState(game);
        _oracle.clear();
        if (null != game.getCommands())
        {
            for (final ClientCommand cmd : game.getCommands())
            {
                if (null == cmd)
                {
                    continue;
                }
                final String command = cmd.getCommand();
                if (null == command)
                {
                    continue;
                }
                _oracle.add(command);
            }
        }
        final Iterable<Message> newMessages = game.getMessagesSince(messagesSince);
        if (null != newMessages)
        {
            for (final Message m : newMessages)
            {
                if(m.isSocialActivity)
                {
                    LOG.debug("Posting social activity: " + m.getText());
                    FacebookLogic.postActivity(m);
                }
                else
                {
                    _consoleWidget.addMessage(m);
                }
            }
        }
    }

    private void showFrame(final GameFrame state)
    {
        if (null == state)
        {
            LOG.debug("Null frame!");
            return;
        }
        if (null != _currentState && state.versionNumber < _currentState.versionNumber)
        {
            LOG.debug("Non-increasing version number in the frames!");
            return;
        }
        if ((null != cmdService) && (cmdService.isLocked()))
        {
            LOG.debug("Updates are currently locked down");
            return;
        }
        LOG.debug("Output Frame " + state.versionNumber);
        GameClientWidget.this.setGame(state);
        String text = "";
        String timeString = new Date().toLocaleString();
        text = "Updated at " + timeString;
        text += "<br/>Current Version: " + state.versionNumber;
        _updateStatusLabel.getElement().setInnerHTML(text);
    }
}
