package com.example.helloworld.test;

import java.util.ArrayList;

public class ListTest {
    public static void main(String[] args) {
        ArrayList<Integer> list = new ArrayList<>();
        list.sort(Integer::compareTo);
    }
}
