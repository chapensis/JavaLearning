package com.example.helloworld.test.lru;

import java.util.HashMap;
import java.util.LinkedList;

public class MyLruCache {
    private HashMap<Integer, Integer> map;
    private LinkedList<Integer> list;
    private int capacity;

    public static void main(String[] args) {
        MyLruCache myLruCache = new MyLruCache(3);
        myLruCache.put(1, 1);
        myLruCache.put(2, 2);
        System.out.println(myLruCache.toString());
        myLruCache.put(4, 4);
        myLruCache.put(3, 3);
        System.out.println(myLruCache.get(2));
        System.out.println(myLruCache.toString());
    }

    public MyLruCache(int capacity) {
        this.capacity = capacity;
        map = new HashMap<>(capacity);
        list = new LinkedList<>();
    }

    public void put(int key, int value) {
        if (map.containsKey(key)) {
            list.remove(key);
            list.add(key);
            map.put(key, value);
        } else {
            list.add(key);
            map.put(key, value);
            if (list.size() > capacity) {
                int delKey = list.remove();
                map.remove(delKey);
            }
        }
    }

    public int get(Integer key) {
        if (map.containsKey(key)) {
            list.remove(key);
            list.add(key);
            return map.get(key);
        } else {
            return -1;
        }
    }

    public String toString() {
        return list.toString();
    }
}
