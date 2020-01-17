package com.example.helloworld.test.designpattern.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StrategyFactory {
    private Map<Integer, Strategy> map;

    public StrategyFactory() {

        List<Strategy> strategies = new ArrayList<>();

        strategies.add(new OrdinaryStrategy());
        strategies.add(new SilverStrategy());

        // 看这里 看这里 看这里！
        map = strategies.stream().collect(Collectors.toMap(Strategy::getType, strategy -> strategy));

        /* 等同上面
        map = new HashMap<>();
        for (Strategy strategy : strategies) {
            map.put(strategy.getType(), strategy);
        }*/
    }

    public static class Holder {
        public static StrategyFactory instance = new StrategyFactory();
    }

    public static StrategyFactory getInstance() {
        return Holder.instance;
    }

    public Strategy get(Integer type) {
        return map.get(type);
    }
}
