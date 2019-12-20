package com.example.helloworld.test.ioc;

public interface MyBeanFactory {
    Object getBeanByName(String name) throws Exception;
}