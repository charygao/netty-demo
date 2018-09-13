package com.example.iodemo.c1.nio;

/**
 * @Author zhouguanya
 * @Date 2018/9/5
 * @Description
 */
public class NioServer {
    public static void main(String[] args) {
        //多路复用类
        NioServerTask nioServerTask = new NioServerTask(8888);
        //单独的线程维护多路复用器
        new Thread(nioServerTask).start();
    }
}
