package com.example.helloworld.test.hashmap;

/**
 * 测试我的hashmap
 *
 * @author yangchang
 */
public class MyHashMapTest {
    public static void main(String[] args) {
        MyMap<String, String> myMap = new MyHashMap<>(16, 0.75f);
        for (int i = 0; i < 500; i++) {
            myMap.put("key:" + i, "value:" + i);
        }

        for (int i = 0; i < 500; i++) {
            System.out.println(myMap.get("key:" + i));
        }
    }
}
