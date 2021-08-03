package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pikaqiu
 */
@NoArgsConstructor
@AllArgsConstructor
public class RpcServerDto {

    private String name;
    private List<Example> examples = new ArrayList<>();

    @Data
    public class Example{
        public Example(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        public Example() {
        }
        private String ip;
        private int port;


        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }

    public RpcServerDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Example> getExamples() {
        return examples;
    }

    public void setExamples(List<Example> examples) {
        this.examples = examples;
    }

    public void addExample(String ip, int port) {
        Example example = new Example(ip, port);
        examples.add(example);
    }
}
