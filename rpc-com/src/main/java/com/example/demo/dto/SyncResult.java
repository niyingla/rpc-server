package com.example.demo.dto;


/**
 * @program: demo
 * @description:
 * @author: xiaoye
 * @create: 2019-08-12 16:29
 **/
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
                    lock.wait(3000);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException("报错了");
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
