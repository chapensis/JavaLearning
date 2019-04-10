package com.itstyle.seckill.queue.jvm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.itstyle.seckill.common.entity.SuccessKilled;
import com.itstyle.seckill.service.ISeckillService;

/**
 * 消费秒杀队列
 * ApplicationRunner和CommandLineRunner类似
 * 他们的执行时机为容器启动完成的时候
 * 创建者 科帮网
 * 创建时间	2018年4月3日
 */
@Component
public class TaskRunner implements ApplicationRunner {

    @Autowired
    private ISeckillService seckillService;

    @Override
    public void run(ApplicationArguments var) throws Exception {
        while (true) {
            // 进程内队列，因为每次只消费一个对象，所以不会有竞争
            System.out.println("进程内队列正在执行......");
            SuccessKilled kill = SeckillQueue.getMailQueue().consume();
            if (kill != null) {
                seckillService.startSeckil(kill.getSeckillId(), kill.getUserId());
            }
        }
    }
}