package com.sawdust.client.gwt.widgets;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sawdust.engine.common.game.Message;
import com.sawdust.engine.common.game.Message.MessageType;

public class ConsoleWidget extends Composite
{
    private static final int TOP_MESSAGE_EMPHASIS = 2;
    private final VerticalPanel commands = new VerticalPanel();
    private HTML prevCompactMessage = null;
    private final ScrollPanel scrollFrame = new ScrollPanel();

    public ConsoleWidget()
    {
        super();
        scrollFrame.add(commands);
        scrollFrame.setSize("100%", "200px");
        scrollFrame.setAlwaysShowScrollBars(true);
        scrollFrame.setStylePrimaryName("sdge-play-console");
        initWidget(scrollFrame);
    }

    public void addMessage(final Message message)
    {
        if (message.getType() == MessageType.Compact)
        {
            if (null == prevCompactMessage)
            {
                prevCompactMessage = new HTML();
                commands.insert(prevCompactMessage, 0);
            }
            prevCompactMessage.setHTML(prevCompactMessage.getHTML() + "<span>" + message.getText() + "</span>");
        }
        else
        {
            prevCompactMessage = null;
            if (!message.isEmpty())
            {
                commands.insert(getMessageWidget(message), 0);
            }
        }
        commands.getWidget(0).setStylePrimaryName("sdge-play-top-message");
        if (commands.getWidgetCount() > TOP_MESSAGE_EMPHASIS)
        {
            commands.getWidget(TOP_MESSAGE_EMPHASIS).setStylePrimaryName("sdge-play-message");
        }

        scrollFrame.scrollToTop();

    }

    public void clearMessages()
    {
        commands.clear();
    }

    private HTML getMessageWidget(final Message m)
    {
        final HTML widget = new HTML(m.getText());
        return widget;
    }
}
