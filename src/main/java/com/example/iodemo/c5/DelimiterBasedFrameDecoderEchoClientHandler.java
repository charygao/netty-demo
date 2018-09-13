package com.example.iodemo.c5;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author zhouguanya
 * @Date 2018/9/7
 * @Description
 */
public class DelimiterBasedFrameDecoderEchoClientHandler extends ChannelHandlerAdapter {
    private AtomicInteger count = new AtomicInteger(0);
    private byte[] req;

    public DelimiterBasedFrameDecoderEchoClientHandler() {
        req = ("hello world" + "$_").getBytes();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf message = null;
        //循环发送100条消息，每发送一条就刷新一次，理论上服务器端会收到100条hello world
        for (int i = 0; i < 100; i++) {
            message = Unpooled.buffer(req.length);
            message.writeBytes(req);
            ctx.writeAndFlush(message);
        }

    }

    /**
     * 读取并打印消息
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String body = (String) msg;
        System.out.println("客户端第" + count.incrementAndGet() + "次收到消息：" + body);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
