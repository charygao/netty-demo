package com.example.iodemo.c8;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

public class EchoClient {
    public void connect(String host, int port, int sendNumber) throws Exception {
        // 配置客户端NIO线程组
        try (EventLoopGroup group = new NioEventLoopGroup()) {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    // 设置TCP连接超时时间
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //用于decode前解决半包和粘包问题
                            // （利用包头中的包含数组长度来识别半包粘包）
                            ch.pipeline()
                                    .addLast(new ProtobufVarint32FrameDecoder());
                            //反序列化指定的Probuf字节数组为protobuf类型
                            ch.pipeline().addLast(new ProtobufDecoder(
                                    Message.Person.getDefaultInstance()));
                            //用于在序列化的字节数组前加上一个简单的包头
                            // 只包含序列化的字节长度
                            ch.pipeline()
                                    .addLast(new ProtobufVarint32LengthFieldPrepender());
                            //用于对Probuf类型序列化
                            ch.pipeline().addLast(new ProtobufEncoder());
                            // 添加业务处理handler
                            ch.pipeline().addLast(new EchoClientHandler(sendNumber));
                        }
                    });
            // 发起异步连接操作
            ChannelFuture f = b.connect(host, port).sync();
            // 等待客户端链路关闭
            f.channel().closeFuture().sync();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8888;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(port);
            } catch (NumberFormatException e) {
                // 采用默认值
            }
        }
        int sendNumber = 100;
        new EchoClient()
                .connect("localhost", port, sendNumber);
    }
}
