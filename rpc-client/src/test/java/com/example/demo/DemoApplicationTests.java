package com.example.demo;

import com.example.demo.dto.CompareDto;
import com.example.demo.rpc.util.FrameWork;
import com.example.demo.util.IoUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class DemoApplicationTests {
    @Autowired
    private FrameWork frameWork;

    @Autowired
    private ApplicationContext applicationContext;


    @Test
    public void contextLoads()throws Exception{
        CompareDto compareDto = new CompareDto();
        compareDto.setType("2222");
        Object invoke = frameWork.methodInvoke("com.example.demo.rpc.FreamWork", "testRpc", compareDto);
        byte[] objectByte = IoUtils.getObjectByte(invoke);
        Object objectByByte = IoUtils.getObjectByByte(objectByte);
        System.out.println(objectByByte);
    }

}
