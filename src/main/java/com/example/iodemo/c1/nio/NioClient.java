package com.example.iodemo.c1.nio;

/**
 * @Author zhouguanya
 * @Date 2018/9/5
 * @Description
 */
public class NioClient {
    public static void main(String[] args) {
        NioClientTask nioClientTask = new NioClientTask("127.0.0.1", 8888);
        new Thread(nioClientTask).start();
    }
}
