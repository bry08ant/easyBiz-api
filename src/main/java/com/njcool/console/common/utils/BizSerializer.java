package com.njcool.console.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.*;

/**
 * Created by yuanfei on 2017/9/14.
 */
public class BizSerializer implements RedisSerializer
{
    static final Class<?> CLAZZ = BizSerializer.class;

    @Override
    public byte[] serialize(Object value) throws SerializationException
    {
        if (value == null)
        {
            throw new NullPointerException("Can't serialize null");
        }
        byte[] rv = null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream os = null;
        try
        {
            bos = new ByteArrayOutputStream();
            os = new ObjectOutputStream(bos);
            os.writeObject(value);
            os.close();
            bos.close();
            rv = bos.toByteArray();
        }
        catch (Exception e)
        {
           // LoggerUtils.fmtError(CLAZZ,e, "serialize error %s", JsonUtil.objectToString(value));
        }
        finally
        {
            close(os);
            close(bos);
        }
        return rv;
    }

    @Override
    public Object deserialize(byte[] in)
    {
        return deserialize(in, Object.class);
    }


    public <T> T deserialize(byte[] in, Class<T>...requiredType)
    {
        Object rv = null;
        ByteArrayInputStream bis = null;
        ObjectInputStream is = null;
        try {
            if (in != null) {
                bis = new ByteArrayInputStream(in);
                is = new ObjectInputStream(bis);
                rv = is.readObject();
            }
        }
        catch (Exception e)
        {
            //LoggerUtils.fmtError(CLAZZ,e, "serialize error %s", in);
        }
        finally
        {
            close(is);
            close(bis);
        }
        return (T) rv;
    }

    private static void close(Closeable closeable)
    {
        if (closeable != null)
            try
            {
                closeable.close();
            }
            catch (IOException e)
            {
                //LoggerUtils.fmtError(CLAZZ, "close stream error");
            }
    }
}
