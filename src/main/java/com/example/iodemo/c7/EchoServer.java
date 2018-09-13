package com.example.iodemo.c7;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class EchoServer {
    public void bind(int port) throws Exception {
        // 配置服务端的NIO线程组
        try(EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup()) {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 添加长度字段解码器
                            // 在MessagePack解码器之前增加LengthFieldBasedFrameDecoder，用于处理半包消息
                            // 它会解析消息头部的长度字段信息，这样后面的MsgpackDecoder接收到的永远是整包消息
                            ch.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
                            // 添加MesspagePack解码器
                            ch.pipeline().addLast("msgpack decoder", new MsgpackDecoder());
                            // 添加长度字段编码器
                            // 在MessagePack编码器之前增加LengthFieldPrepender，它将在ByteBuf之前增加2个字节的消息长度字段
                            ch.pipeline().addLast("frameEncoder", new LengthFieldPrepender(2));
                            // 添加MessagePack编码器
                            ch.pipeline().addLast("msgpack encoder", new MsgpackEncoder());
                            // 添加业务处理handler
                            // 添加业务处理handler
                            ch.pipeline().addLast(new EchoServerHandler());
                        }
                    });

            // 绑定端口，同步等待成功
            ChannelFuture f = b.bind(port).sync();
            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8888;
        if(args != null && args.length > 0) {
            try {
                port = Integer.valueOf(port);
            } catch (NumberFormatException e) {
                // TODO: handle exception
            }
        }
        new EchoServer().bind(port);
    }
}
