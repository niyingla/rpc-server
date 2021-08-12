package com.example.demo.dto;

import java.io.Serializable;

/**
 * @program: demo
 * @description:
 * @author: xiaoye
 * @create: 2019-08-12 11:55
 **/
public class RpcRequestDto implements Serializable {
    public RpcRequestDto(String requestId, String classPath, String methodName, Object[] args) {
        this.requestId = requestId;
        this.classPath = classPath;
        this.methodName = methodName;
        this.args = args;
    }

    private String requestId;

    private String classPath;

    private String methodName;

    private Object[] args;

    private Object result;

    private Object other;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Object getOther() {
        return other;
    }

    public void setOther(Object other) {
        this.other = other;
    }
}
