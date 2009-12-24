package com.sawdust.engine.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

//import org.apache.geronimo.mail.util.Base64;
import org.apache.commons.codec.binary.Base64;
//import java.util.prefs.Base64;


import com.sawdust.engine.service.debug.SawdustSystemError;

public class Util
{
   private static final Logger LOG = Logger.getLogger(Util.class.getName());

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T Copy(final T obj)
    {
        T copiedObj = null;
        try
        {
            final ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
            final ObjectOutputStream out = new ObjectOutputStream(outBuffer);
            out.writeObject(obj);

            final ByteArrayInputStream inBuffer = new ByteArrayInputStream(outBuffer.toByteArray());
            final ObjectInputStream in = new ObjectInputStream(inBuffer);
            copiedObj = (T) in.readObject();
        }
        catch (final IOException e)
        {
            e.printStackTrace();
            throw new SawdustSystemError(e);
        }
        catch (final ClassNotFoundException e)
        {
            e.printStackTrace();
            throw new SawdustSystemError(e);
        }
        return copiedObj;
    }

    public static String hashStrings(final String... params)
    {
        final StringBuilder sb = new StringBuilder();
        for (final String s : params)
        {
            sb.append(s);
        }
        int h = sb.toString().hashCode();
        h = h % 0xFFFFFFF;
        if (h < 0)
        {
            h = -h;
        }
        return Integer.toHexString(h);
    }

    public static String hexString(final byte[] bytes)
    {
        final StringBuilder sb = new StringBuilder();
        for (final byte b : bytes)
        {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static String md5(final String id)
    {
        if (null == id) return null;
        if (id.isEmpty()) return null;
        String expectedSignature = null;
        try
        {
            final byte[] bytes = id.getBytes("UTF-8");
            final MessageDigest md = MessageDigest.getInstance("MD5");
            final byte[] digest = md.digest(bytes);
            expectedSignature = hexString(digest);
        }
        catch (final NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (final UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return expectedSignature;
    }

    public static <T> T randomMember(final T[] values)
    {
        if (0 == values.length) return null;
        final int i = (int) Math.floor(Math.random() * values.length);
        return values[i];
    }

    public static String string(final Serializable obj)
    {
        try
        {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            OutputStream out = byteArrayOutputStream;

            out = new DeflaterOutputStream(out, new Deflater(1, true), 1);

            final ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(obj);
            objOut.close();
            final byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeBase64(byteArray).toString();
        }
        catch (final IOException e)
        {
            e.printStackTrace();
            // throw new RuntimeException(e);
            return null;
        }
    }

    public static <T extends Serializable> T unstring(final String s, final Class<T> c)
    {
        try
        {
            byte[] decode;
             decode = Base64.decodeBase64(s.getBytes());
            InputStream in = new ByteArrayInputStream(decode);

            in = new InflaterInputStream(in, new Inflater(true));

            final ObjectInputStream inObj = new ObjectInputStream(in);
            final T copiedObj = (T) inObj.readObject();
            return copiedObj;
        }
        catch (final IOException e)
        {
            return null;
        }
        catch (final ClassNotFoundException e)
        {
            return null;
        }
    }

    public static <T extends Serializable> byte[] toBytes(final T obj)
    {
        byte[] data;
        try
        {
            final ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
            ZipOutputStream z = new ZipOutputStream(outBuffer);
            z.putNextEntry(new ZipEntry(""));
            final ObjectOutputStream out = new ObjectOutputStream(z);
            try
            {
                out.writeObject(obj);
                out.flush();
                z.closeEntry();
                z.flush();
                LOG.fine(String.format("Serialize object: %s; %d bytes",obj.getClass().getName(), outBuffer.size()));
            }
            catch (Exception e)
            {
                LOG.warning(e.toString());
            }
            data = outBuffer.toByteArray();
        }
        catch (final IOException e)
        {
            e.printStackTrace();
            throw new SawdustSystemError(e);
        }
        return data;
    }

    public static Object fromBytes(final byte[] data)
    {
        Object copiedObj = null;
        try
        {
            final ByteArrayInputStream inBuffer = new ByteArrayInputStream(data);
            ZipInputStream z = new ZipInputStream(inBuffer);
            z.getNextEntry();
            final ObjectInputStream in = new ObjectInputStream(z);
            copiedObj = (Object) in.readObject();
            LOG.fine(String.format("Deserialize object: %s; %d bytes",copiedObj.getClass().getName(), data.length));
        }
        catch (final IOException e)
        {
            throw new SawdustSystemError(e);
        }
        catch (final ClassNotFoundException e)
        {
            throw new SawdustSystemError(e);
        }
        return copiedObj;
    }


    public static void clearCookie(final HttpServletResponse response, final String cookieName)
    {
        final Cookie loginCookie = new Cookie(cookieName, null);
        loginCookie.setMaxAge(0);
        response.addCookie(loginCookie);
    }

    public static String md5base64(final String id)
    {
        if (null == id) return null;
        if (id.isEmpty()) return null;
        String expectedSignature = null;
        try
        {
            final byte[] bytes = id.getBytes("UTF-8");
            final MessageDigest md = MessageDigest.getInstance("MD5");
            final byte[] digest = md.digest(bytes);
            expectedSignature = Base64.encodeBase64(digest).toString();
        }
        catch (final NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (final UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return expectedSignature;
    }

    public static String getFullString(Throwable e)
    {
        if(null == e) return "NULL";
        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out, true));
        return out.getBuffer().toString();
    }

    public static <T> T randomMember(ArrayList<T> population)
    {
        return (T) randomMember(population.toArray());
    }
}
