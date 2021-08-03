package com.example.demo;

import com.example.demo.netty.connect.NettyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServerApplication {

    public static void main(String[] args)throws Exception {
        SpringApplication.run(ServerApplication.class, args);
    }



}
