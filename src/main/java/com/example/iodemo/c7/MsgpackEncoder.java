package com.example.iodemo.c7;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

/**
 * @Author zhouguanya
 * @Date 2018/9/13
 * @Description
 */
public class MsgpackEncoder extends MessageToByteEncoder<Object> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        // 创建MessagePack对象
        MessagePack msgpack = new MessagePack();
        // 将对象编码为MessagePack格式的字节数组
        byte[] raw = msgpack.write(msg);
        // 将字节数组写入到ByteBuf中
        out.writeBytes(raw);
    }

}
