package com.sawdust.client.gwt.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sawdust.client.gwt.util.EventListener;

public class SawdustDialogBox extends DialogBox
{
    public final Button buttonOk = new Button("OK"); 
    public final EventListener _onClose;

    public SawdustDialogBox(Widget innerWidget, EventListener onClose)
    {
        super();
        this._onClose = onClose;
        final VerticalPanel dialogVPanel = new VerticalPanel();
        dialogVPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        dialogVPanel.add(innerWidget);
        dialogVPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        buttonOk.getElement().setId("configAcceptButton");
        buttonOk.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                SawdustDialogBox.this.hide();
                if (null != _onClose)
                {
                    _onClose.onEvent(event);
                }
            }
        });
        dialogVPanel.add(buttonOk);
        this.setWidget(dialogVPanel);
    }

}
