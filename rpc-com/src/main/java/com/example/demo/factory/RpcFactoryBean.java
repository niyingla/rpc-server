package com.example.demo.factory;

import org.springframework.beans.factory.FactoryBean;

/**
 * <p> RpcFactoryBean </p>
 *
 * @author xiaoye
 * @version 1.0
 * @date 2022/11/3 19:36
 */

public class RpcFactoryBean implements FactoryBean<Object> {

  private Class<?> type;

  @Override
  public Object getObject() throws Exception {
    return ProxyFactory.getInterfaceProxy(type);
  }

  @Override
  public Class<?> getObjectType() {
    return type;
  }

  public Class<?> getType() {
    return type;
  }

  public void setType(Class<?> type) {
    this.type = type;
  }
}
