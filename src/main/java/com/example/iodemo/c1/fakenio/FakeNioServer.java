package com.example.iodemo.c1.fakenio;

import com.example.iodemo.c1.bio.ServerHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author zhouguanya
 * @Date 2018/9/3
 * @Description
 */
public class FakeNioServer {

    public static void main(String[] args) {
        int port = 8888;
        ServerSocket serverSocket = null;
        ExecutorService executorService = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("服务器端口8888启动");
            Socket socket = null;
            executorService = Executors.newFixedThreadPool(10);
            while (true) {
                socket = serverSocket.accept();
                executorService.submit(new ServerHandler(socket));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                System.out.println("关闭服务端");
                try {
                    serverSocket.close();
                    executorService.shutdown();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
