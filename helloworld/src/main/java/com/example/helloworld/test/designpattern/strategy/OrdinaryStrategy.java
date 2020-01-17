package com.example.helloworld.test.designpattern.strategy;

public class OrdinaryStrategy implements Strategy{
    @Override
    public double compute(long money) {
        System.out.println("普通会员 不打折");
        return money;
    }

    // 添加 type 返回
    @Override
    public int getType() {
        return UserType.SILVER_VIP.getCode();
    }
}
