package com.example.demo;

import com.example.demo.rpc.util.FrameWork;
import com.example.demo.service.UserInfoService;
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
    public void cxt() {
        UserInfoService bean = applicationContext.getBean(UserInfoService.class);
        log.debug(bean.getCompareDto("2333").toString());
        log.debug(bean.toString());
    }
}
