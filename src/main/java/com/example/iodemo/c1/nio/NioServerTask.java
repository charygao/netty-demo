package com.example.iodemo.c1.nio;

import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author zhouguanya
 * @Date 2018/9/5
 * @Description
 */
public class NioServerTask implements Runnable{
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private volatile boolean stop;

    /**
     * 初始化多路复用器，绑定端口
     * @param port
     */
    public NioServerTask(int port) {

        try{
            //多路复用器
            selector = Selector.open();
            //打开ServerSocketChannel， 监听客户端连接
            serverSocketChannel = ServerSocketChannel.open();
            //非阻塞模式
            serverSocketChannel.configureBlocking(false);
            //监听端口
            serverSocketChannel.socket().bind(new InetSocketAddress(port), 1024);
            // ServerSocketChannel注册到多路复用器Selector上，监听ACCEPT事件
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("服务器启动端口：" + port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop () {
        this.stop = true;
    }

    @Override
    public void run() {
        //轮询就绪的key
        while (!stop) {
            try {
               selector.select(1000);
               Set<SelectionKey> selectionKeys = selector.selectedKeys();
               Iterator<SelectionKey> iterator = selectionKeys.iterator();
               SelectionKey key = null;
               while (iterator.hasNext()) {
                   key = iterator.next();
                   iterator.remove();
                   try {
                       //处理key
                       handleKey(key);
                   } catch (Exception e) {
                       if (key != null) {
                           key.cancel();
                           if (key.channel() != null) {
                               key.channel().close();
                           }
                       }
                   }
               }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (selector != null) {
            try {
                selector.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 处理key
     * @param key
     * @throws IOException
     */
    private void handleKey(SelectionKey key) throws IOException {
        if (key.isValid()) {
            //监听到有新的客户端接入
            if (key.isAcceptable()) {
                ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                //完成TCP三次握手，TCP物理链接建立
                SocketChannel socketChannel = serverSocketChannel.accept();
                //客户端设置为非阻塞
                socketChannel.configureBlocking(false);
                //新的客户端注册到多路复用器Selector上，监听读操作，读取客户端发送的消息
                socketChannel.register(selector, SelectionKey.OP_READ);
            }

            //  读取数据
            if (key.isReadable()) {
                SocketChannel socketChannel = (SocketChannel) key.channel();
                //1M的缓冲区
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                //读取客户端请求到缓冲区
                int readBytes = socketChannel.read(readBuffer);
                if (readBytes > 0) {
                    //当前缓冲区limit设置为position，position设置为0，便于后续对缓冲区的读取
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes);
                    System.out.println("获取客户端输入:" + body);
                    doWrite(socketChannel, "当前时间是：" + new Date());
                } else if (readBytes < 0) {
                    //关闭
                    key.cancel();
                    socketChannel.close();
                }
            }
        }
    }

    /**
     * 发送响应消息
     * @param socketChannel
     * @param response
     * @throws IOException
     */
    private void doWrite(SocketChannel socketChannel, String response) throws IOException {
        if (!StringUtils.isEmpty(response)) {
            byte[] bytes = response.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();
            socketChannel.write(writeBuffer);
        }
    }
}
