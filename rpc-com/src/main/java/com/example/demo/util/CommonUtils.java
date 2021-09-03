package com.example.demo.util;

import java.net.Inet4Address;
import java.net.UnknownHostException;

public class CommonUtils {

    public static String getIp() {
        try {
            String ip = Inet4Address.getLocalHost().getHostAddress();
            return ip;
        } catch (UnknownHostException e) {
            throw new RuntimeException("获取本机ip失败");
        }

    }
}
