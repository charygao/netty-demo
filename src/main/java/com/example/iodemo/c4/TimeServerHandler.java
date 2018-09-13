package com.example.iodemo.c4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;


/**
 * @Author zhouguanya
 * @Date 2018/9/7
 * @Description 对网络时间进行读写
 */
public class TimeServerHandler extends ChannelHandlerAdapter {
    /**
     * 计数器
     */
    private int counter;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        //类似NIO中的ByteBuffer
        ByteBuf buf = (ByteBuf) msg;
        //获取缓冲区可读字节数
        byte[] req = new byte[buf.readableBytes()];
        //缓冲区中的字节复制到字节数组
        buf.readBytes(req);
        String body = new String(req).substring(0, req.length - System.getProperty("line.separator").length());
        if ("hello world".equalsIgnoreCase(body)) {
            System.out.println("收到输入：" + body);
        } else {
            System.out.println("异常输入：" + body);
        }
        ++counter;
        ByteBuf response = Unpooled.copiedBuffer(("当前时间：" + new Date()).getBytes());
        //并不是直接把消息发送到SocketChannel中，只是把消息发送到缓冲数组，通过flush方法将消息发到SocketChannel
        ctx.write(response);
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
