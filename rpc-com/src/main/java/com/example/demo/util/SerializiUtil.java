package com.example.demo.util;

import org.apache.commons.lang3.CharSet;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SerializiUtil {
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

    public static String toBase64String(Object obj) {
        ObjectOutputStream obi = null;
        ByteArrayOutputStream bai = null;
        try {
            bai = new ByteArrayOutputStream();
            obi = new ObjectOutputStream(bai);
            obi.writeObject(obj);
            return Base64.getEncoder().encodeToString(bai.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T Base64ToObj(String msg) {
        ObjectInputStream oii = null;
        ByteArrayInputStream bis = null;
        try {
            byte[] decode = Base64.getDecoder().decode(msg);
            bis = new ByteArrayInputStream(decode);
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

    /**
     * 序列化
     * @param obj
     * @return
     */
    public static byte[] serializeToString(Object obj) {
        ObjectOutputStream obi = null;
        ByteArrayOutputStream bai = null;
        try {
            bai = new ByteArrayOutputStream();
            obi = new ObjectOutputStream(bai);
            obi.writeObject(obj);
            return bai.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 反序列化
     *
     * @param msg
     * @return
     */
    public static <T> T unSerizlizeFromString(String msg) {
        ObjectInputStream oii = null;
        ByteArrayInputStream bis = null;
        bis = new ByteArrayInputStream(msg.getBytes());
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
