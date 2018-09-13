package com.example.iodemo.c5;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @Author zhouguanya
 * @Date 2018/9/7
 * @Description
 */
public class FixedLengthFrameDecoderEchoClient {

    public void connect(int port, String host) {
        try (EventLoopGroup eventLoopGroup = new NioEventLoopGroup()){
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new StringDecoder());
                            socketChannel.pipeline().addLast(new FixedLengthFrameDecoderEchoClientHandler());
                        }
                    });
            //异步链接操作
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            //等待客户端
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port = 8888;
        new FixedLengthFrameDecoderEchoClient().connect(port, "127.0.0.1");
    }
}
