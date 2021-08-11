package com.example.demo.rpc.context;

import com.example.demo.netty.config.RpcSource;

public class RpcContext {

    private RpcServerPool rpcServerPool;

    private RpcSource rpcSource;

    public RpcServerPool getRpcServerPool() {
        return rpcServerPool;
    }

    public void setRpcServerPool(RpcServerPool rpcServerPool) {
        this.rpcServerPool = rpcServerPool;
    }

    public RpcSource getRpcSource() {
        return rpcSource;
    }

    public void setRpcSource(RpcSource rpcSource) {
        this.rpcSource = rpcSource;
    }
}
