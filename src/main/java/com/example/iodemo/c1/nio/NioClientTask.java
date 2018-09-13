package com.example.iodemo.c1.nio;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author zhouguanya
 * @Date 2018/9/5
 * @Description
 */
public class NioClientTask implements Runnable {
    private String host;
    private int port;
    private Selector selector;
    private SocketChannel socketChannel;
    private volatile boolean stop;

    public NioClientTask(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            selector = Selector.open();
            //打开SocketChannel
            socketChannel = SocketChannel.open();
            //设置SocketChannel为非阻塞
            socketChannel.configureBlocking(false);
        } catch (Exception e) {

        }
    }

    @Override
    public void run() {
        try {
            connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (!stop) {
            try {
                selector.select(1000);
                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeySet.iterator();
                SelectionKey selectionKey = null;
                while (iterator.hasNext()) {
                    selectionKey = iterator.next();
                    iterator.remove();
                    try {
                        handle(selectionKey);
                    } catch (Exception e) {
                        if (selectionKey != null) {
                            selectionKey.cancel();
                            if (selectionKey.channel() != null) {
                                selectionKey.channel().close();
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

    private void handle(SelectionKey selectionKey) throws IOException {
        if (selectionKey.isValid()) {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            //是否连接成功
            if (selectionKey.isConnectable()) {
                if (socketChannel.finishConnect()) {
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    doWrite(socketChannel);
                } else {
                    System.exit(1);
                }
            }
        }

        if (selectionKey.isReadable()) {
            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            int readBytes = socketChannel.read(readBuffer);
            if (readBytes > 0) {
                readBuffer.flip();
                byte[] bytes = new byte[readBuffer.remaining()];
                readBuffer.get(bytes);
                String body = new String(bytes);
                System.out.println(body);
            } else if (readBytes < 0) {
                selectionKey.channel();
                socketChannel.close();
            } else {
                //忽略
            }
        }
    }

    /**
     * 连接服务端
     * @throws IOException
     */
    private void connect() throws IOException {
        //如果连接成功，注册到多路复用器上，发送请求
        if (socketChannel.connect(new InetSocketAddress(host, port))) {
            socketChannel.register(selector, SelectionKey.OP_READ);
            doWrite(socketChannel);
        } else {
            //不代表连接失败，等待服务端返回syn-ack
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
        }
    }

    /**
     * 写入消息
     * @param socketChannel
     * @throws IOException
     */
    private void doWrite(SocketChannel socketChannel) throws IOException {
        byte[] req = "hello world".getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
        writeBuffer.put(req);
        writeBuffer.flip();
        socketChannel.write(writeBuffer);
    }
}
