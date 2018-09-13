package com.example.iodemo.c1.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Author zhouguanya
 * @Date 2018/9/3
 * @Description
 */
public class BioServer {

    public static void main(String[] args) {
        int port = 8888;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("服务器端口8888启动");
            Socket socket = null;
            while (true) {
                socket = serverSocket.accept();
                new Thread(
                        new ServerHandler(socket)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                System.out.println("关闭服务端");
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
