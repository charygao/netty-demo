package com.example.iodemo.c8;

import com.google.protobuf.InvalidProtocolBufferException;

import java.util.Arrays;
import java.util.List;

/**
 * @Author zhouguanya
 * @Date 2018/9/14
 * @Description
 */
public class Main {
    public static void main(String[] args) {
        Message.Person.Builder personBuilder = Message.Person.newBuilder();
        personBuilder.setId(12345678);
        personBuilder.setName("Admin");
        personBuilder.addPhone(Message.Person.Phone.newBuilder().setNumber("10010").setType(Message.Person.PhoneType.MOBILE));
        personBuilder.addPhone(Message.Person.Phone.newBuilder().setNumber("10086").setType(Message.Person.PhoneType.HOME));
        personBuilder.addPhone(Message.Person.Phone.newBuilder().setNumber("10000").setType(Message.Person.PhoneType.WORK));

        Message.Person person = personBuilder.build();
        byte[] buff = person.toByteArray();

        try {
            Message.Person personOut = Message.Person.parseFrom(buff);
            System.out.printf("Id:%d, Name:%s\n", personOut.getId(), personOut.getName());

            List<Message.Person.Phone> phoneList = personOut.getPhoneList();

            for (Message.Person.Phone phone : phoneList) {
                System.out.printf("PhoneNumber:%s (%s)\n", phone.getNumber(), phone.getType());
            }

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        //Person类的字节码
        System.out.println(Arrays.toString(buff));

    }
}
