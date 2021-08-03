package com.example.demo.util;

import lombok.extern.slf4j.Slf4j;

/**
 * @author pikaqiu
 */
@Slf4j
public class ClassUtils {
    /**
     * 获取参数类型
     * @param param
     * @return
     */
    public static Class[] getClassType(Object[] param) {
        Class[] classType = null;
        if (param != null && param.length > 0) {
            classType = new Class[param.length];
            for (int i = 0; i < param.length; i++) {
                Object o = param[i];
                classType[i] = o.getClass();
            }
        }
        return classType;
    }

}
