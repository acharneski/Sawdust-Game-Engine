package com.sawdust.gwt.art;
import com.google.gwt.user.client.ui.ImageBundle;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.core.client.GWT;

public class singleton 
{
     private static boolean _init = false;
     private static xface _instance = null;
     static xface instance() {
         if(!_init)
         {
             _instance = GWT.create(xface.class);
             _init = true;         }
         return _instance;
              }
}
