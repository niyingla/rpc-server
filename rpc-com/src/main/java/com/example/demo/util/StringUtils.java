package com.example.demo.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @program: demo
 * @description:
 * @author: xiaoye
 * @create: 2019-08-05 17:11
 **/
public class StringUtils {

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    public static String lowerFirst(String name) {
        char[] chars = name.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    public static String dateToString(Date date) {
        return sdf.format(date);
    }
}
