package com.example.iodemo.c5;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;


/**
 * @Author zhouguanya
 * @Date 2018/9/11
 * @Description
 */
public class FixedLengthFrameDecoderEchoServerHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String body = (String) msg;
        System.out.println("服务器端收到消息：" + body);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        //将消息发送队列中的消息写入SocketChannel中，发送到对方
        //防止频繁的唤醒Selector进行消息发送
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        //发生异常关闭ChannelHandlerContext等资源
        ctx.close();
    }
}
