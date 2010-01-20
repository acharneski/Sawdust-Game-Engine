package com.sawdust.gwt.art;

import java.util.HashMap;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ImageBundle;

public class ArtManager
{
    public static final ArtManager Instance = new ArtManager();

    private ArtManager()
    {
        addArtLibrary("CARD1", com.sawdust.gwt.art.generated.cards1.enumArt.values());
        addArtLibrary("WORD1", com.sawdust.gwt.art.generated.letters1.enumArt.values());
        addArtLibrary("GO1", com.sawdust.gwt.art.go1.enumArt.values());
    }
    HashMap<String,HashMap<String,GwtSawdustArt>> art = new HashMap<String, HashMap<String,GwtSawdustArt>>();

    private <T extends GwtSawdustArt> void addArtLibrary(String libraryId, T[] values)
    {
        HashMap<String, GwtSawdustArt> library = getLibrary(libraryId);
        for(T v : values)
        {
            library.put(v.getId(), v);
        }
    }

    private HashMap<String, GwtSawdustArt> getLibrary(String library)
    {
        if(art.containsKey(library))
        {
            return art.get(library);
        }
        HashMap<String, GwtSawdustArt> returnValue = new HashMap<String, GwtSawdustArt>();
        art.put(library, returnValue);
        return returnValue;
    }

    public AbstractImagePrototype getImage(final String lib, final String id)
    {
        HashMap<String, GwtSawdustArt> library = getLibrary(lib);
        if(!library.containsKey(id)) return null;
        return library.get(id).getImage();
    }
}
