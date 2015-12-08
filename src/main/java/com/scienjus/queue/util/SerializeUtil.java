package com.scienjus.queue.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author ScienJus
 * @date 2014/7/24.
 */
public class SerializeUtil {

    /**
     * 序列化
     *
     * @param object
     */
    public static byte[] serialize(Object object) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos);) {
            oos.writeObject(object);
            return baos.toByteArray();
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 反序列化
     *
     * @param bytes
     */
    public static Object unserialize(byte[] bytes) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bais);) {
            return ois.readObject();
        } catch (Exception e) {
        }
        return null;
    }
}
