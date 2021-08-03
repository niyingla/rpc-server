package com.example.demo.service;

import com.example.demo.annotation.RpcServerCase;
import com.example.demo.dto.CompareDto;

/**
 * @author pikaqiu
 */

@RpcServerCase(serverName = "user")
public interface UserInfoService {

     CompareDto getCompareDto(String type);

     CompareDto getCompareTest(String type);
}
