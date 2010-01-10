/**
 * 
 */
package com.sawdust.client.gwt;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.sawdust.engine.view.config.PropertyConfig;

class BooleanValueMapper implements ValueChangeHandler<Boolean>
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