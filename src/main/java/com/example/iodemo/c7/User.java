package com.example.iodemo.c7;

import org.msgpack.annotation.Message;

/**
 * @Author zhouguanya
 * @Date 2018/9/13
 * @Description
 */
@Message
public class User {
    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User [name=" + name + ", age=" + age + "]";
    }
}