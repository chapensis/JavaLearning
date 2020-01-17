package com.example.helloworld.test.designpattern.strategy;

public interface Strategy {
    double compute(long money);

    // 返回 type
    int getType();
}
