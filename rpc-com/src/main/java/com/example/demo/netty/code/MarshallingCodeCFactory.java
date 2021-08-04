package com.example.demo.netty.code;

import io.netty.handler.codec.marshalling.*;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

/**
 * Marshalling工厂
 *
 * @author（alienware）
 * @since 2014-12-16
 */
public final class MarshallingCodeCFactory {


  /**
   * 解码
   * @return
   */
  public static MarshallingDecoder buildMarshallingDecoder() {
    //1、首先通过编组工具类的精通方法获取编组实例对象参数序列标识创建的是java序列化工厂对象。
    final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
    //2、创建了MarshallingConfiguration对象，配置了版本号为5
    final MarshallingConfiguration configuration = new MarshallingConfiguration();
    configuration.setVersion(5);
    //3、根据marshallerFactory和配置创建提供商
    UnmarshallerProvider provider = new DefaultUnmarshallerProvider(marshallerFactory, configuration);
    //4、构建Netty的MarshallingDecoder对象，两个参数分别为提供商和单个消息序列化后的最大长度
    MarshallingDecoder decoder = new MarshallingDecoder(provider, 1024 * 1024 * 1);
    return decoder;
  }

  /**
   * 编码
   * @return
   */
  public static MarshallingEncoder buildMarshallingEncoder() {
    final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
    final MarshallingConfiguration configuration = new MarshallingConfiguration();
    configuration.setVersion(5);
    MarshallerProvider provider = new DefaultMarshallerProvider(marshallerFactory, configuration);
    //5、构建Netty的MarshallingEncoder对象，MarshallingEncoder用于实现序列化接口的POJO对象序列化为二进制数组
    MarshallingEncoder encoder = new MarshallingEncoder(provider);
    return encoder;
  }
}
