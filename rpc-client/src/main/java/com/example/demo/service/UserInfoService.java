package com.example.demo.service;

import com.example.demo.annotation.RpcServerCase;
import com.example.demo.dto.CompareDto;
import com.example.demo.inteface.Aspect;
import com.example.demo.proxy.ReqAspect;

/**
 * @author pikaqiu
 */

@RpcServerCase(serverName = "user", proxyClass = {ReqAspect.class})
public interface UserInfoService {

     CompareDto getCompareDto(String type);

     CompareDto getCompareTest(String type);
}
