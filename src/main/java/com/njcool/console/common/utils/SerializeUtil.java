package com.njcool.console.common.utils;

import com.alibaba.druid.support.json.JSONUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.log4j.Logger;

import java.io.*;

/**
 * @author xfe
 * @Date 2018/9/12
 * @Desc
 */
public class SerializeUtil {
    private static final Logger LOG = Logger.getLogger(SerializeUtil.class.getName());

    public static byte[] serialize(Object value) throws JsonProcessingException {
        if (value == null) {
            throw new NullPointerException("Can't serialize null");
        }
        byte[] rv = null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream os = null;
        try {
            bos = new ByteArrayOutputStream();
            os = new ObjectOutputStream(bos);
            os.writeObject(value);
            os.close();
            bos.close();
            rv = bos.toByteArray();
        } catch (Exception e) {
            LOG.error(String.format("serialize error %s", JSONUtils.toJSONString(value)), e);
            //LoggerUtils.fmtError(CLAZZ,e, "serialize error %s", JsonUtil.objectToString(value));
        } finally {
            close(os);
            close(bos);
        }
        return rv;
    }


    public static Object deserialize(byte[] in)
    {
        return deserialize(in, Object.class);
    }

    public static <T> T deserialize(byte[] in, Class<T>...requiredType) {
        Object rv = null;
        ByteArrayInputStream bis = null;
        ObjectInputStream is = null;
        try {
            if (in != null) {
                bis = new ByteArrayInputStream(in);
                is = new ObjectInputStream(bis);
                rv = is.readObject();
            }
        } catch (Exception e) {
            LOG.error(String.format("serialize error %s", in), e);
            //LoggerUtils.fmtError(CLAZZ,e, "serialize error %s", in);
        } finally {
            close(is);
            close(bis);
        }
        return (T) rv;
    }

    private static void close(Closeable closeable) {
        if (closeable != null)
            try {
                closeable.close();
            } catch (IOException e) {
                LOG.error("close stream error");
                //LoggerUtils.fmtError(CLAZZ, "close stream error");
            }
    }
}
