package com.example.demo.proxy;

import com.example.demo.dto.RpcRequestDto;
import com.example.demo.inteface.Aspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@Slf4j
@Component
public class ReqAspect implements Aspect {

    @Override
    public void before(RpcRequestDto requestDto) {
        requestDto.setOther(System.currentTimeMillis());
    }

    @Override
    public void after(RpcRequestDto requestDto) {
        long time = (long) requestDto.getOther();
        log.debug("请求id：{}, 本次请求花费：{}ms", requestDto.getRequestId(), System.currentTimeMillis() - time);
    }
}
