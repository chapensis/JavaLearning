package com.example.helloworld.test.classloader.classforname;

/**
 *  Class.forName 加载类是将类进了初始化，
 *  而 ClassLoader 的 loadClass 并没有对类进行初始化，只是把类加载到了虚拟机中
 *  在我们熟悉的 Spring 框架中的 IOC 的实现就是使用的 ClassLoader。
 *  而在我们使用 JDBC 时通常是使用 Class.forName() 方法来加载数据库连接驱动。
 *  这是因为在 JDBC 规范中明确要求 Driver(数据库驱动)类必须向 DriverManager 注册自己
 */
public class ClassforNameTest {

    public static void main(String[] args) throws Exception {
        Class.forName("com.example.helloworld.test.classloader.classforname.ClassforName");
        System.out.println("###分隔符###");
        ClassLoader.getSystemClassLoader().loadClass("com.example.helloworld.test.classloader.classforname.ClassforName2");
    }
}
