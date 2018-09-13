package com.example.iodemo.c7;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.MessagePack;

import java.util.List;

/**
 * @Author zhouguanya
 * @Date 2018/9/13
 * @Description
 */
public class MsgpackDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        // 从数据报msg中（这里的数据类型为ByteBuf，因为Netty的通信基于ByteBuf对象）
        final byte[] array;
        final int length = msg.readableBytes();
        array = new byte[length];
        /**
         * 这里使用的是ByteBuf的getBytes方法来将ByteBuf对象转换为字节数组，前面是使用readBytes，直接传入一个接收的字节数组参数即可
         * 这里的参数比较多，第一个参数是index，关于readerIndex，说明如下：
         * ByteBuf是通过readerIndex跟writerIndex两个位置指针来协助缓冲区的读写操作的，具体原理等到Netty源码分析时再详细学习一下
         * 第二个参数是接收的字节数组
         * 第三个参数是dstIndex the first index of the destination
         * 第四个参数是length   the number of bytes to transfer
         */
        msg.getBytes(msg.readerIndex(), array, 0, length);
        // 创建一个MessagePack对象
        MessagePack msgpack = new MessagePack();
        // 解码并添加到解码列表out中
        out.add(msgpack.read(array));
    }
}
