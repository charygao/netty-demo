package com.example.iodemo.c8;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author zhouguanya
 * @Date 2018/9/7
 * @Description
 */
public class EchoClientHandler extends ChannelHandlerAdapter {
    // sendNumber为写入发送缓冲区的对象数量
    private int sendNumber;

    public EchoClientHandler(int sendNumber) {
        this.sendNumber = sendNumber;
    }

    /**
     * 构建长度为userNum的User对象数组
     *
     * @param userNum
     * @return
     */
    private List<Message.Person> getPersonList(int userNum) {
        List<Message.Person> personList = new ArrayList<>();
        for (int i = 0; i < userNum; i++) {
            Message.Person.Builder personBuilder = Message.Person.newBuilder();
            personBuilder.setId(i);
            personBuilder.setName("Admin" + i);
            personBuilder.addPhone(
                    Message.Person.Phone.newBuilder()
                            .setNumber("10010")
                            .setType(Message.Person.PhoneType.MOBILE));
            personBuilder.addPhone(
                    Message.Person.Phone.newBuilder()
                            .setNumber("10086")
                            .setType(Message.Person.PhoneType.HOME));
            personBuilder.addPhone(Message.Person.Phone.newBuilder()
                    .setNumber("10000")
                    .setType(Message.Person.PhoneType.WORK));
            Message.Person person = personBuilder.build();
            personList.add(person);
        }
        return personList;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        List<Message.Person> personList = getPersonList(sendNumber);
        for (Message.Person person : personList) {
            ctx.writeAndFlush(person);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("Client receive the msgpack message : " + msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }

}
