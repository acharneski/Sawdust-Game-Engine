package com.sawdust.util.art;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public abstract class ArtLibraryGenerator<T extends ArtToken>
{
    protected final String _inputPath;
    protected final String _basePath;
    protected final String _nameSpace;

    public ArtLibraryGenerator(String inputPath, String resourceOutputPath)
    {
        this(inputPath, resourceOutputPath, "com.sawdust.gwt.art");
    }
    
    public ArtLibraryGenerator(String inputPath, String outputPath, String nameSpace)
    {
        super();
        this._nameSpace = nameSpace;
        this._inputPath = inputPath;
        this._basePath = outputPath;
    }

    abstract T[] getTokenSet();

    abstract String getKey();

    public void createLibrary()
    {
        HashMap<String, String> files = new HashMap<String, String>();
        String libraryKey = getKey();
        String libraryNameSpace = _nameSpace + "." + libraryKey;
        String libraryPath = _basePath + "/" + libraryNameSpace.replaceAll("\\.", "/") + "/";
        File interfaceFile = new File(libraryPath + "xface.java");
        File enumFile = new File(libraryPath + "enumArt.java");
        File singletonFile = new File(libraryPath + "singleton.java");
        interfaceFile.getParentFile().mkdirs();
        FileWriter interfaceWriter;
        FileWriter enumWriter;
        FileWriter singletonWriter;
        try
        {
            interfaceWriter = new FileWriter(interfaceFile);
            interfaceWriter.write(String.format("package %s;\n", libraryNameSpace));
            interfaceWriter.write(String.format("import com.google.gwt.user.client.ui.AbstractImagePrototype;\n"));
            interfaceWriter.write(String.format("import com.google.gwt.user.client.ui.ImageBundle;\n"));
            interfaceWriter.write(String.format("public interface xface extends ImageBundle \n{\n"));

            enumWriter = new FileWriter(enumFile);
            enumWriter.write(String.format("package %s;\n", libraryNameSpace));
            enumWriter.write(String.format("import com.google.gwt.user.client.ui.ImageBundle;\n"));
            enumWriter.write(String.format("import com.google.gwt.user.client.ui.AbstractImagePrototype;\n"));
            enumWriter.write(String.format("public enum enumArt implements GwtSawdustArt \n{\n", libraryKey));

            singletonWriter = new FileWriter(singletonFile);
            singletonWriter.write(String.format("package %s;\n", libraryNameSpace));
            singletonWriter.write(String.format("import com.google.gwt.user.client.ui.ImageBundle;\n"));
            singletonWriter.write(String.format("import com.google.gwt.user.client.ui.AbstractImagePrototype;\n"));
            singletonWriter.write(String.format("import com.google.gwt.core.client.GWT;\n"));
            singletonWriter.write(String.format("public class singleton\n" +
            		"{\n" +
                    "     private static boolean _init = false;\n" +
                    "     private static xface _instance = null;\n" +
            		"     static xface instance() {\n" +
            		"         if(!_init)\n" +
            		"         {\n" +
            		"             _instance = GWT.create(xface.class);\n" +
            		"             _init = true;" +
                    "         }\n" +
            		"         return _instance;\n" +
            		"         " +
            		"     }\n" +
            		"}\n"));
            singletonWriter.close();
            
            
            for (T token : getTokenSet())
            {
                String imagePath = createImage(token);
                String tokenKey = token.getKey();
                files.put(tokenKey, imagePath);

                interfaceWriter.write(String.format("@Resource(\"%s\")\n", imagePath));
                interfaceWriter.write(String.format("AbstractImagePrototype get%s();\n", tokenKey));

                enumWriter.write(String.format("%s%s\n", libraryKey, tokenKey));
                enumWriter.write(String.format("{\n"));
                enumWriter.write(String.format("AbstractImagePrototype getImage() {\n return singleton.instance().get%s(); \n}\n", tokenKey));
                enumWriter.write(String.format("String getId() {\n return \"%s\"; \n}\n", tokenKey));
                enumWriter.write(String.format("\n},\n", tokenKey));
            }
            
            interfaceWriter.write(String.format("}"));
            interfaceWriter.close();
            
            enumWriter.write(String.format("}"));
            enumWriter.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    protected String createImage(T token)
    {
        String relativePath = getKey() + "/" + token.getKey() + ".png";
        String absolutePath = _basePath + "/" + relativePath;
        try
        {
            File file = new File(absolutePath);
            file.getParentFile().mkdirs();
            token.writeImage(file);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return relativePath;
    }
}
