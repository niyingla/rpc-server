package com.example.demo.service.impl;

import com.example.demo.dto.CompareDto;
import com.example.demo.service.UserInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author pikaqiu
 */

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Override
    public CompareDto getCompareDto(String type) {
        CompareDto compareDto = new CompareDto();
        compareDto.setType(type);
        compareDto.setWay("ye");
        return compareDto;
    }

    @Override
    public CompareDto getCompareTest(String type) {
        return null;
    }
}
