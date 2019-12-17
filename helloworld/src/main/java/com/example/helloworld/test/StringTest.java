package com.example.helloworld.test;

public class StringTest {
    public static void main(String[] args) {
        String str1 = "abc";
        String str2 = "abc";
        // 大家都是常量池的，所以相等
        System.out.println(str1 == str2);
        String str3 = new String("abc");
        // 对象不会等于常量池中的变量
        System.out.println(str1 == str3);
        String str4 = new String("abc");
        // 对象不会等于其他对象
        System.out.println(str3 == str4);
    }
}
