package com.example.demo.rpc.util;

import com.example.demo.util.ClassUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * @author pikaqiu
 */
@Component
public class FrameWork implements ApplicationContextAware {

    private static ApplicationContext applicationContext;


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
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        //获取类实例
        Class clazz = loader.loadClass(classPathStr);
        Object contextBean = applicationContext.getBean(clazz);
        //获取参数类型
        Class[] classType = ClassUtils.getClassType(param);
        Method method = clazz.getMethod(methodStr, classType);
        //反射执行方法
        Object invokeResult = method.invoke(contextBean, param);
        return invokeResult;

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        FrameWork.applicationContext = applicationContext;
    }
}
