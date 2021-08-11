package com.example.demo.netty.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rpc")
public class RpcSource {

    private String classPtah = "com.example.demo";

    private Integer connectCount = 3;

    public String getClassPtah() {
        return classPtah;
    }

    public void setClassPtah(String classPtah) {
        this.classPtah = classPtah;
    }

    public Integer getConnectCount() {
        return connectCount;
    }

    public void setConnectCount(Integer connectCount) {
        this.connectCount = connectCount;
    }
}
