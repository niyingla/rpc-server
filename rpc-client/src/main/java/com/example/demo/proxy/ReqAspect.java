package com.example.demo.proxy;

import com.example.demo.dto.RpcRequestDto;
import com.example.demo.inteface.ClientAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@Slf4j
@Component
public class ReqAspect implements ClientAspect {
    private ThreadLocal<Long> threadLocal = new ThreadLocal();

    @Override
    public void before(RpcRequestDto requestDto) {
        threadLocal.set(System.currentTimeMillis());
    }

    @Override
    public void after(RpcRequestDto requestDto, Object result) {
        Long time = threadLocal.get();
        log.debug("请求id：{}, 本次请求花费：{}ms", requestDto.getRequestId(), System.currentTimeMillis() - time);
    }
}
