package com.example.demo.rpc.util;

import com.alibaba.fastjson.JSON;
import com.example.demo.rpc.factory.StartFactory;
import com.example.demo.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * @author pikaqiu
 */
public class FrameWork {

    static Logger log = LoggerFactory.getLogger(FrameWork.class.getName());

    /**
     *  通过反射调用目标方法
     * @param classPathStr
     * @param methodStr
     * @param param
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object methodInvoke(String classPathStr,String methodStr,Object ... param) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        //获取类实例
        Class clazz = loader.loadClass(classPathStr);
        Object contextBean = StartFactory.getRpcContext().getBeanFactory().getBean(clazz);
        //获取参数类型
        Class[] classType = ClassUtils.getClassType(param);
        Method method = clazz.getMethod(methodStr, classType);
        //反射执行方法
        log.debug("执行 {}.{}方法 参数是 {}",classPathStr,methodStr, JSON.toJSONString(param));
        Object result = method.invoke(contextBean, param);
        return result;
    }

}
