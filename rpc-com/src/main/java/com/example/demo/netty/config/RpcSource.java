package com.example.demo.netty.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rpc")
public class RpcSource {

    /**
     * 默认扫描路径
     */
    private String classPtah = "com.example.demo";

    /**
     * 默认连接数
     */
    private Integer connectCount = 3;

    /**
     * 默认端口
     */
    private int port = 33333;

    private String serverName;

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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}
