package com.example.demo.controller;

import com.example.demo.dto.CompareDto;
import com.example.demo.service.TestService;
import com.example.demo.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("test")
public class TestController {
    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private TestService testService;

    @GetMapping("getCompareDto")
    public CompareDto getCompareDto(@RequestParam String type) {
        System.out.println(testService.test());
        return userInfoService.getCompareDto(type);
    }

    @GetMapping("testQrCode")
    public void testQrCode(HttpServletRequest request, HttpServletResponse response)throws Exception{
        System.out.println("进来了。。。。。");
        String path = "https://work.weixin.qq.com/ct/wcdec65609a06992de8b61daebe071262d8e";
        response.sendRedirect(path);
    }
}
