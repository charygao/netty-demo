package com.example.iodemo.c8;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

public class EchoServer {
    public void bind(int port) throws Exception {
        // 配置服务端的NIO线程组
        try (EventLoopGroup bossGroup = new NioEventLoopGroup();
             EventLoopGroup workerGroup = new NioEventLoopGroup()) {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //用于decode前解决半包和粘包问题
                            // （利用包头中的包含数组长度来识别半包粘包）
                            ch.pipeline().addLast(
                                    new ProtobufVarint32FrameDecoder());
                            //反序列化指定的Probuf字节数组为protobuf类型
                            ch.pipeline().addLast(
                                    new ProtobufDecoder(
                                            Message.Person.getDefaultInstance()));
                            //用于在序列化的字节数组前加上一个简单的包头
                            // 只包含序列化的字节长度
                            ch.pipeline().addLast(
                                    new ProtobufVarint32LengthFieldPrepender());
                            //用于对Probuf类型序列化
                            ch.pipeline().addLast(new ProtobufEncoder());
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
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(port);
            } catch (NumberFormatException e) {
                // TODO: handle exception
            }
        }
        new EchoServer().bind(port);
    }
}
