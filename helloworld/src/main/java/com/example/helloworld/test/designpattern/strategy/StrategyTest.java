package com.example.helloworld.test.designpattern.strategy;

public class StrategyTest {
    public static void main(String[] args) {
        getResult(100,1);
    }
    private static double getResult(long money, int type) {

        if (money < 1000) {
            return money;
        }

        Strategy strategy = StrategyFactory.getInstance().get(type);

        if (strategy == null) {
            throw new IllegalArgumentException("please input right type");
        }

        return strategy.compute(money);
    }
}
