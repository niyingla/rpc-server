package com.example.demo.dto;

import java.io.Serializable;

public class ServerDto implements Serializable {
    private Integer port;

    private String ip;

    private String serverName;

    public ServerDto(Integer port, String ip, String serverName) {
        this.port = port;
        this.ip = ip;
        this.serverName = serverName;
    }

    public ServerDto() {
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}
