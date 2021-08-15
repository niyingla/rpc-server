package com.example.demo.util;

import java.io.*;

public class RedisUtil {
    /**
     * 序列化
     * @param obj
     * @return
     */
    public static byte[] serialize(Object obj) {
        ObjectOutputStream obi = null;
        ByteArrayOutputStream bai = null;
        try {
            bai = new ByteArrayOutputStream();
            obi = new ObjectOutputStream(bai);
            obi.writeObject(obj);
            byte[] byt = bai.toByteArray();
            return byt;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 反序列化
     *
     * @param byt
     * @return
     */
    public static <T> T unserizlize(byte[] byt) {
        ObjectInputStream oii = null;
        ByteArrayInputStream bis = null;
        bis = new ByteArrayInputStream(byt);
        try {
            oii = new ObjectInputStream(bis);
            Object obj = oii.readObject();
            if (obj == null) {
                return null;
            }
            return (T) obj;
        } catch (Exception e) {

            e.printStackTrace();
        }
        return null;
    }
}