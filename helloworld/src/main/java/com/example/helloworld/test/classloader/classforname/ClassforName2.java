package com.example.helloworld.test.classloader.classforname;

public class ClassforName2 {
    static {
        System.out.println("执行静态代码块");
    }

    private static String staticFiled = staticMethod();

    public static String staticMethod() {
        System.out.println("执行的静态方法");
        return "给静态字段赋值";
    }
}
