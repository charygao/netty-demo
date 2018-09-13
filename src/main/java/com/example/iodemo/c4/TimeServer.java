package com.example.iodemo.c4;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @Author zhouguanya
 * @Date 2018/9/7
 * @Description netty时间服务器服务器端
 */
public class TimeServer {

    public void bind(int port) {
        //配置服务器端NIO线程组
        //NioEventLoopGroup是个线程组，包含了一组NIO线程，处理网络事件，实际上就是Reactor线程组
        try (EventLoopGroup bossLoopGroup = new NioEventLoopGroup();EventLoopGroup workerGroup = new NioEventLoopGroup()){
            //netty用于启动NIO服务端的启动类，目的是降低NIO开发的复杂度
            ServerBootstrap bootstrap = new ServerBootstrap();
            //功能类似于NIO中的ServerSocketChannel
            bootstrap.group(bossLoopGroup, workerGroup).channel(NioServerSocketChannel.class)
                    //配置NioServerSocketChannel的参数
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    //绑定事件的处理类ChildChannelHandler
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new TimeServerHandler());
                        }
                    });
            //绑定端口，同步等待绑定操作完成
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            //等待服务器监听端口关闭
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port = 8888;
        new TimeServer().bind(port);
    }
}
