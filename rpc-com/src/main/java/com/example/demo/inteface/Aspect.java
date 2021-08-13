package com.example.demo.inteface;

import com.example.demo.dto.RpcRequestDto;
import org.springframework.core.annotation.Order;

public interface Aspect {
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
