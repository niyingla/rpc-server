package com.example.demo.controller;

import com.example.demo.dto.CompareDto;
import com.example.demo.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class TestController {
    @Lazy
    @Autowired
    private UserInfoService userInfoService;

    @GetMapping("getCompareDto")
    public CompareDto getCompareDto(@RequestParam String type){
        return userInfoService.getCompareDto(type);
    }
}
