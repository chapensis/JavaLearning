package com.example.helloworld.test.ioc;

/**
 * 测试IOC容器
 *
 * @author yangchang
 */
public class testMain {
    public static MyBeanFactory beanFactory = new MyBeanFactoryImpl();

    /**
     * 主函数
     *
     * @param args 参数
     * @throws Exception 异常
     */
    public static void main(String[] args) throws Exception {
        IoCInitConifg ioCInitConifg = new IoCInitConifg();
        ioCInitConifg.run();

        User user1 = (User) beanFactory.getBeanByName("com.example.ioc.domain.User");
        User user2 = (User) beanFactory.getBeanByName("com.example.ioc.domain.User");
        Student student1 = user1.getStudent();
        Student student2 = user1.getStudent();
        Student student3 = (Student) beanFactory.getBeanByName("com.example.ioc.domain.Student");
        System.out.println(user1);
        System.out.println(user2);
        System.out.println(student1);
        System.out.println(student2);
        System.out.println(student3);
    }
}
