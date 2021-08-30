package com.example.demo.inteface;

import com.example.demo.dto.RpcRequestDto;

public interface ServerAspect {
    /**
     * 前置处理
     * @param requestDto
     */
    void before(RpcRequestDto requestDto);

    /**
     * 后置处理
     * @param requestDto
     */
    void after(RpcRequestDto requestDto);
}
