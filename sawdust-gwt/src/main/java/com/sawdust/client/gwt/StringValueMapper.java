/**
 * 
 */
package com.sawdust.client.gwt;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.sawdust.engine.common.config.PropertyConfig;

class StringValueMapper implements ValueChangeHandler<String>
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