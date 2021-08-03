package com.example.demo.dto;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * @program: demo
 * @description:
 * @author: xiaoye
 * @create: 2019-08-12 16:29
 **/
@Slf4j
public class SyncResult {
    volatile boolean isRead = false;
    Object Data = null;
    Object lock = new Object();

    /**
     * 获取数据没有数据时线程等待
     * @return
     */
    public Object getData() {
        if (!isRead) {
            try {
                synchronized (lock) {
                    //释放锁
                    lock.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return Data;
    }

    /**
     * 设置数据
     * @param data
     */
    public void setData(Object data) {
        Data = data;
        this.isRead = true;
        synchronized (lock) {
            lock.notifyAll();
        }
    }
}