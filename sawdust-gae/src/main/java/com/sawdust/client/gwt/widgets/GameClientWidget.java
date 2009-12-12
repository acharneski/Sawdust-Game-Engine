package com.sawdust.client.gwt.widgets;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sawdust.client.gwt.util.CommandExecutor;
import com.sawdust.client.gwt.util.Constants;
import com.sawdust.client.gwt.util.EventListener;
import com.sawdust.client.gwt.util.GoogleAccess;
import com.sawdust.engine.common.CommandResult;
import com.sawdust.engine.common.game.ClientCommand;
import com.sawdust.engine.common.game.GameState;
import com.sawdust.engine.common.game.Message;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GameClientWidget
{
    public static final String CMD_UPDATE = "Update";
    static final int jsWorkaroundDelay = 50;
    private static final int START_TIME = 50;

    private final Button _closeButton = new Button("Close");
    private int _commandCursorLength;
    private int _commandCursorPos;
    private final ConsoleWidget _consoleWidget = new ConsoleWidget();
    private GameState _currentState = null;
    private final DialogBox _dialogBox = new DialogBox();
    private final GameWidget _gameWidget;

    private boolean _isFocused = false;
    private final MultiWordSuggestOracle _oracle = new MultiWordSuggestOracle();
    private String _previousText;
    private final HTML _serverResponseLabel = new HTML();
    private final SuggestBox _suggestBox = new SuggestBox(_oracle);
    private final Label _updateStatusLabel = new Label();
    private boolean _wasFocused;

    final CommandExecutor cmdService = new CommandExecutor(
    /* Success Hook */
    new EventListener()
    {
        public void onEvent(final Object... params)
        {
            final CommandResult result = (CommandResult) params[0];
            int frameCount = 0;
            final List<GameState> stateFrames = result.getStateFrames();
            Collections.sort(stateFrames, new Comparator<GameState>()
            {
                public int compare(final GameState o1, final GameState o2)
                {
                    return ((Integer) o1.versionNumber).compareTo(o2.versionNumber);
                }
            });
            int framesQueued = 0;
            int currentTime = Integer.MAX_VALUE;
            for (final GameState state : stateFrames)
            {
                if (state != null && state.timeOffset < currentTime)
                {
                    currentTime = state.timeOffset;
                }
            }
            currentTime -= START_TIME;
            final HashMap<Integer, Timer> _states = new HashMap<Integer, Timer>();
            if(0 < cmdService.getCommandQueue().size())
            {
                System.err.println("Non-increasing version number in the frames!");
            }
            else
            {
                for (final GameState state : stateFrames)
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
                            if ((null != _currentState) && (state.versionNumber <= _currentState.versionNumber))
                            {
                                System.err.println("Non-increasing version number in the frames!");
                                return;
                            }
                            if ((null != _currentState) && (state.versionNumber <= _currentState.versionNumber))
                            {
                                System.err.println("Non-increasing version number in the frames!");
                                return;
                            }
                            if ((null != cmdService) && (cmdService.isLocked()))
                            {
                                System.err.println("Updates are currently locked down");
                                return;
                            }
                            System.out.println("Output Frame " + thisIdx);
                            GameClientWidget.this.setGame(state);
                            _updateStatusLabel.setText("Updated at " + new Date().toLocaleString());
                            
                            String text = "";
                            text = "Updated at " + new Date().toLocaleString();
                            text += "<br/>Current Version: " + state.versionNumber;
                            _updateStatusLabel.getElement().setInnerHTML(text);
                        }
                    };
                    final int timeSpan = state.timeOffset - currentTime;
                    _states.put(timeSpan, timer);
                    System.out.println("Queing frame " + ++framesQueued + " in " + timeSpan + "ms");
                }
                if (1 == _states.size())
                {
                    final Timer finalState = _states.get(START_TIME);
                    if (null != finalState)
                    {
                        finalState.run();
                    }
                    else
                    {
                        String text = "";
                        text = "Updated at " + new Date().toLocaleString();
                        text += "<br/>Current Version: " + _currentState.versionNumber;
                        _updateStatusLabel.getElement().setInnerHTML(text);
                    }
                }
                else if (0 < _states.size())
                {
                    for (final Entry<Integer, Timer> entry : _states.entrySet())
                    {
                        final int key = entry.getKey();
                        final Timer timer = entry.getValue();
                        System.out.println("Schedule Frame in " + key + "ms");
                        try
                        {
                            timer.schedule(key);
                        }
                        catch (final Exception e)
                        {
                            e.printStackTrace(System.err);
                        }
                    }
                }
            }

            final int updateTime = ((null == _currentState)?5:_currentState.updateTime) * 1000;
            if (refreshDelayMillis != updateTime)
            {
                refreshDelayMillis = updateTime;
                refreshTimer.cancel();
                if(updateTime > 0) refreshTimer.scheduleRepeating(refreshDelayMillis);
            }
        }
    },
    /* Error Hook */
    new EventListener()
    {
        public void onEvent(final Object... params)
        {
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
            final GameState state = _currentState;
            final int versionNumber = (null == state) ? 0 : state.versionNumber;
            cmdService.doUpdate(versionNumber, null);
        }
    };

    public GameClientWidget(final RootPanel rootPanel)
    {
        cmdService.setAccessKey(GoogleAccess.getAccessToken(rootPanel));
        _gameWidget = new GameWidget(cmdService);

        final VerticalPanel vPanel = new VerticalPanel();
        vPanel.add(_gameWidget.getTabPanel());
        rootPanel.add(vPanel);
        init(vPanel);
    }

    void backupCommandStatus()
    {
        // suggestBox.getTextBox().setReadOnly(true);
        _commandCursorLength = _suggestBox.getTextBox().getSelectionLength();
        _commandCursorPos = _suggestBox.getTextBox().getCursorPos();
        _previousText = _suggestBox.getTextBox().getText();
        _wasFocused = _isFocused;
    }

    public GameState getGame()
    {
        _currentState = _gameWidget.getState();
        return _currentState;
    }

    private void init(final VerticalPanel vPanel)
    {
        final Button runButton = new Button("Run");
        final DockPanel commandPanel = new DockPanel();
        final Label label = new Label("Command: ");

        commandPanel.setWidth("100%");
        _suggestBox.setWidth("100%");
        commandPanel.add(label, DockPanel.WEST);
        commandPanel.add(_suggestBox, DockPanel.CENTER);
        commandPanel.add(runButton, DockPanel.EAST);
        commandPanel.setCellHorizontalAlignment(label, HasHorizontalAlignment.ALIGN_LEFT);
        commandPanel.setCellWidth(_suggestBox, "100%");
        commandPanel.setCellHorizontalAlignment(runButton, HasHorizontalAlignment.ALIGN_RIGHT);

        vPanel.add(commandPanel);
        vPanel.add(_consoleWidget);
        vPanel.add(_updateStatusLabel);
        _oracle.clear();

        // Setup error dialog
        _closeButton.getElement().setId("closeButton");
        final VerticalPanel dialogVPanel = new VerticalPanel();
        dialogVPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        dialogVPanel.add(_serverResponseLabel);
        dialogVPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        dialogVPanel.add(_closeButton);
        _dialogBox.setWidget(dialogVPanel);
        _dialogBox.setAnimationEnabled(true);
        _dialogBox.setText("Error");
        // Add a handler to close the DialogBox
        _closeButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                _dialogBox.hide();
                if (null != onErrorClose)
                {
                    onErrorClose.onEvent(event);
                }
            }
        });

        runButton.addClickHandler(new ClickHandler()
        {

            public void onClick(final ClickEvent event)
            {
                runButton.setEnabled(false);
                cmdService.doCommand(_suggestBox.getText(), new EventListener()
                {
                    public void onEvent(final Object... params)
                    {
                        runButton.setEnabled(true);
                    }
                });
            }
        });
        _suggestBox.addKeyUpHandler(new KeyUpHandler()
        {
            public void onKeyUp(final KeyUpEvent event)
            {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
                {
                    if (_suggestBox.getTextBox().isEnabled())
                    {
                        _suggestBox.getTextBox().setEnabled(false);
                        cmdService.doCommand(_suggestBox.getText(), new EventListener()
                        {
                            public void onEvent(final Object... params)
                            {
                                _suggestBox.getTextBox().setEnabled(true);
                                _suggestBox.setText("");
                                _suggestBox.getTextBox().setFocus(true);
                            }
                        });
                    }
                }
            }
        });
        _suggestBox.getTextBox().setFocus(true);

        _suggestBox.getTextBox().addFocusHandler(new FocusHandler()
        {
            public void onFocus(final FocusEvent event)
            {
                _isFocused = true;
            }
        });
        _suggestBox.getTextBox().addBlurHandler(new BlurHandler()
        {
            public void onBlur(final BlurEvent event)
            {
                _isFocused = false;
            }
        });

        loadGame();
    }

    private void loadGame()
    {
        cmdService.getGame(new EventListener()
        {
            
            public void onEvent(Object... params)
            {
                refreshTimer.scheduleRepeating(refreshDelayMillis);
            }
        });
    }

    public void setGame(final GameState game)
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
                // GWT.log(command, null);
                _oracle.add(command);
            }
        }
        // shellDialog.ClearMessages();
        final Iterable<Message> newMessages = game.getMessagesSince(messagesSince);
        if (null != newMessages)
        {
            for (final Message m : newMessages)
            {
                _consoleWidget.addMessage(m);
            }
        }
        // if (txt.length() > 1) shellDialog.setCursorPos(txt.length() - 1);
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
}
