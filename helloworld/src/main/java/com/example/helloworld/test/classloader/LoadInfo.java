package com.example.helloworld.test.classloader;

import lombok.Data;

/**
 * 封装加载类的信息
 *
 * @Author niujinpeng
 * @Date 2019/10/24 23:32
 */
@Data
public class LoadInfo {

    /** 自定义的类加载器 */
    private MyClasslLoader myClasslLoader;

    /** 记录要加载的类的时间戳-->加载的时间 */
    private long loadTime;

    /** 需要被热加载的类 */
    private BaseManager manager;

    public LoadInfo(MyClasslLoader myClasslLoader, long loadTime) {
        this.myClasslLoader = myClasslLoader;
        this.loadTime = loadTime;
    }
}
