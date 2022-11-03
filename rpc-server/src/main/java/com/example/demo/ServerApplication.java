package com.example.demo;

import com.example.demo.annotation.EnableRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRpc(classPath = "com.example.demo")
@SpringBootApplication
public class ServerApplication {

    public static void main(String[] args)throws Exception {
        SpringApplication.run(ServerApplication.class, args);
    }



}
