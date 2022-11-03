package com.example.demo.annotation;

import com.example.demo.rpc.context.RpcClientsRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * <p> EnableRpc </p>
 *
 * @author xiaoye
 * @version 1.0
 * @date 2022/11/3 14:10
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(RpcClientsRegistrar.class)
public @interface EnableRpc {
  String classPath();
}
