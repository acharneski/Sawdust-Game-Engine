package com.sawdust.engine.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

import com.sawdust.engine.controller.exceptions.SawdustSystemError;

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

    public static byte[] unhexString(final String stringData)
    {
        assert(0 == (stringData.length() % 2));
        byte[] buffer = new byte[(int)(stringData.length()/2)];
        char[] charArray = stringData.toCharArray();
        for(int i=0;i<buffer.length;i++)
        {
            char byteOne = charArray[i*2];
            char byteTwo = charArray[i*2+1];
            buffer[i] = (byte) (hexCharToInt(byteOne)*16+hexCharToInt(byteTwo));
        }
        return buffer;
    }

    private static byte hexCharToInt(char c)
    {
        if(c == '0')
        {
            return 0;
        }
        else if(c == '1')
        {
            return 1;
        }
        else if(c == '2')
        {
            return 2;
        }
        else if(c == '3')
        {
            return 3;
        }
        else if(c == '4')
        {
            return 4;
        }
        else if(c == '5')
        {
            return 5;
        }
        else if(c == '6')
        {
            return 6;
        }
        else if(c == '7')
        {
            return 7;
        }
        else if(c == '8')
        {
            return 8;
        }
        else if(c == '9')
        {
            return 9;
        }
        else if(c == 'A' || c == 'a')
        {
            return 10;
        }
        else if(c == 'B' || c == 'b')
        {
            return 11;
        }
        else if(c == 'C' || c == 'c')
        {
            return 12;
        }
        else if(c == 'D' || c == 'd')
        {
            return 13;
        }
        else if(c == 'E' || c == 'e')
        {
            return 14;
        }
        else if(c == 'F' || c == 'f')
        {
            return 15;
        }
        throw new NumberFormatException("Unrecognized Symbol: " + c);
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

    public static <T> T randomMember(final T[] values)
    {
        if (null == values) return null;
        if (0 == values.length) return null;
        final int i = (int) Math.floor(Math.random() * values.length);
        return values[i];
    }

    public static String string(final Serializable obj)
    {
        final byte[] byteArray = Util.toBytes(obj);
        return hexString(byteArray);
    }

    public static <T extends Serializable> T unstring(final String s)
    {
        final T copiedObj = (T) Util.fromBytes(unhexString(s));
        return copiedObj;
    }

    public static <T extends Serializable> byte[] toBytes(final T obj)
    {
        byte[] data = new byte[0];
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
                data = outBuffer.toByteArray();
            }
            catch (Throwable e)
            {
                LOG.warning(e.toString());
            }
        }
        catch (final IOException e)
        {
            e.printStackTrace();
            throw new SawdustSystemError(e);
        }
        return data;
    }

    public static Serializable fromBytes(final byte[] data)
    {
        Serializable copiedObj = null;
        try
        {
            final ByteArrayInputStream inBuffer = new ByteArrayInputStream(data);
            ZipInputStream z = new ZipInputStream(inBuffer);
            z.getNextEntry();
            final ObjectInputStream in = new ObjectInputStream(z);
            copiedObj = (Serializable) in.readObject();
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
        byte[] digest;
        try
        {
            digest = md5(id.getBytes("UTF-8"));
        }
        catch (final UnsupportedEncodingException e)
        {
            LOG.severe(Util.getFullString(e));
            throw new RuntimeException(e);
        }
        expectedSignature = Base64.encodeBase64(digest).toString();
        return expectedSignature;
    }

    public static String md5(final String id)
    {
        if (null == id) return null;
        if (id.isEmpty()) return null;
        String expectedSignature = null;
        try
        {
            final byte[] digest = md5(id.getBytes("UTF-8"));
            expectedSignature = hexString(digest);
        }
        catch (final UnsupportedEncodingException e)
        {
            LOG.severe(Util.getFullString(e));
            throw new RuntimeException(e);
        }
        return expectedSignature;
    }

    public static byte[] md5(final byte bytes[])
    {
        MessageDigest md;
        try
        {
            md = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e)
        {
            LOG.severe(Util.getFullString(e));
            throw new RuntimeException(e);
        }
        final byte[] digest = md.digest(bytes);
        return digest;
    }

    public static String md5hex(String sessionId)
    {
        byte[] bytes;
        try
        {
            bytes = sessionId.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            LOG.severe(Util.getFullString(e));
            throw new RuntimeException(e);
        }
        return hexString(md5(bytes));
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
