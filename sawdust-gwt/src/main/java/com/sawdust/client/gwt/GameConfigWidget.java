package com.sawdust.client.gwt;

import java.util.Collection;
import java.util.HashMap;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sawdust.client.gwt.util.Constants;
import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.common.config.GameModConfig;
import com.sawdust.engine.common.config.PropertyConfig;
import com.sawdust.engine.common.config.PropertyConfig.DetailLevel;
import com.sawdust.engine.common.config.PropertyConfig.PropertyType;

public class GameConfigWidget extends VerticalPanel
{
    private final GameConfig _config;

    public GameConfigWidget(GameConfig config, DetailLevel lod)
    {
        _config = config;
        this.setStylePrimaryName("sdge-game-listing");
        final HashMap<String, PropertyConfig> properties = _config.getProperties();
        final Grid renderProperties = renderProperties(properties.values(), lod);
        this.add(renderProperties);
        
        for (final GameModConfig module : _config.getModules())
        {
            final VerticalPanel x = new VerticalPanel();
            x.add(new HTML(module.getDescription()));
            x.add(renderProperties(module.getProperties().values(), lod));
            this.add(createModuleBox(module, x));
        }
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

    
    private Grid renderProperties(final Collection<PropertyConfig> properties, DetailLevel lod)
    {
        final Grid propertyTable = new Grid(0, 2);
        for (final PropertyConfig property : properties)
        {
            if(property.levelOfDetail.value < lod.value) continue;
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

    public GameConfig getConfig()
    {
        return _config;
    }
}
