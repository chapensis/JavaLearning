package com.example.helloworld.test;

import java.util.ArrayList;
import java.util.List;

public class Ttest<T> {
    static class Fruit {
    }

    static class Apple extends Fruit {
    }

    static class BigApple extends Apple {
    }

    /**
     * 主函数
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        // 上去（取）下沉（存），这里是上界通配符，所以不能存
        List<? extends Apple> list = new ArrayList<>();
//        list.add(new BigApple());
//        list.add(new Apple());

        // 同上
        List<? extends Fruit> list2 = new ArrayList<>();
//        list2.add(new Apple());

        // 苹果是水果子类，所以可以存
        List<Fruit> fruitList = new ArrayList<>();
        List<Apple> appleList = new ArrayList<>();
        fruitList.addAll(appleList);
    }

    /**
     * 因为 T 通配符只有在运行期才知道 T 的具体类型，所以不能是 static 修饰的
     *
     * @param t
     */
    public void show(T t) {
        System.out.println(t.toString());
    }
}
