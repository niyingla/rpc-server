package com.example.demo.util;

import java.io.*;

/**
 * @author pikaqiu
 */
public class IoUtils {

    /**
     * 将对象转成字节
     * @param object
     * @return
     * @throws IOException
     */
    public static byte[] getObjectByte(Object object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(byteArrayOutputStream);
        os.writeObject(object);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        os.flush();
        os.close();
        byteArrayOutputStream.close();
        return bytes;
    }


    /**
     * 将字节转成对象
     * @param bytes
     * @return
     * @throws IOException
     */
    public static Object getObjectByByte(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream os = new ObjectInputStream(byteInputStream);
        Object readObject = os.readObject();
        os.close();
        byteInputStream.close();
        return readObject;
    }

}
