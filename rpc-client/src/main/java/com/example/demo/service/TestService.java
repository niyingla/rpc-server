package com.example.demo.service;

import com.example.demo.annotation.RpcServerCase;

@RpcServerCase(serverName = "user")
public interface TestService {

  Integer test();
}
