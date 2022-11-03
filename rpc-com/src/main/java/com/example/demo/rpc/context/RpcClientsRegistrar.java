package com.example.demo.rpc.context;

import com.example.demo.annotation.EnableRpc;
import com.example.demo.rpc.factory.StartFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * <p> RpcClientsRegistrar </p>
 *
 * @author xiaoye
 * @version 1.0
 * @date 2022/11/3 14:12
 */
public class RpcClientsRegistrar implements ImportBeanDefinitionRegistrar{

  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
    StartFactory startFactory = new StartFactory();
    //1 设置上下文
    startFactory.setContextBean((ConfigurableListableBeanFactory) registry);
    //2 启动服务端
    startFactory.registerRpcServer(registry, (String) importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName(), true).get("classPath"));
  }

}
