package com.example.helloworld.test.designpattern.strategy;

public class SilverStrategy implements Strategy {
    @Override
    public double compute(long money) {

        System.out.println("白银会员 优惠50元");
        return money - 50;
    }

    // type 返回
    @Override
    public int getType() {
        return UserType.SILVER_VIP.getCode();
    }
}
