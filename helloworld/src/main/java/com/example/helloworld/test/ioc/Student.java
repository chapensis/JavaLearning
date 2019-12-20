package com.example.helloworld.test.ioc;

@MyIoc
public class Student {
    public String play(){
        return "student"+ this.toString();
    }
}
