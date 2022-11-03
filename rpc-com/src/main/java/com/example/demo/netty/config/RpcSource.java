package com.example.demo.netty.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rpc")
public class RpcSource {

    /**
     * 默认连接数
     */
    private Integer connectCount = 3;

    /**
     * 默认端口
     */
    private int port = 33333;

    private String nameSpace = "nameSpace1";

    private String serverName;

    private String[] commonReqAspect = new String[]{};;

    private String[] commonRecAspect = new String[]{};

    public String[] getCommonReqAspect() {
        return commonReqAspect;
    }

    public void setCommonReqAspect(String[] commonReqAspect) {
        this.commonReqAspect = commonReqAspect;
    }

    public String[] getCommonRecAspect() {
        return commonRecAspect;
    }

    public void setCommonRecAspect(String[] commonRecAspect) {
        this.commonRecAspect = commonRecAspect;
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

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }
}
