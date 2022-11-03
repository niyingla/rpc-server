package com.example.demo.service.impl;

import com.example.demo.service.TestService;
import org.springframework.stereotype.Service;

/**
 * <p> TestServiceImpl </p>
 *
 * @author xiaoye
 * @version 1.0
 * @date 2022/11/3 19:41
 */
@Service
public class TestServiceImpl implements TestService {

  @Override
  public Integer test() {
    return 9999;
  }
}
