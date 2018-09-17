package com.example.iodemo.c8;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;


/**
 * @Author zhouguanya
 * @Date 2018/9/11
 * @Description
 */
public class EchoServerHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println(
                "Server receive the msgpack message : "
                        + msg);
        ctx.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx
            , Throwable cause) {
        // 发生异常，关闭链路
        ctx.close();
    }
}
