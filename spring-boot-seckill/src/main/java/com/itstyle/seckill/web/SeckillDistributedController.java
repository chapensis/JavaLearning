package com.itstyle.seckill.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.jms.Destination;

import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itstyle.seckill.common.entity.Result;
import com.itstyle.seckill.common.redis.RedisUtil;
import com.itstyle.seckill.queue.activemq.ActiveMQSender;
import com.itstyle.seckill.queue.kafka.KafkaSender;
import com.itstyle.seckill.queue.redis.RedisSender;
import com.itstyle.seckill.service.ISeckillDistributedService;
import com.itstyle.seckill.service.ISeckillService;

/**
 * 分布式秒杀,重点学习对象
 */
@Api(tags = "分布式秒杀")
@RestController
@RequestMapping("/seckillDistributed")
public class SeckillDistributedController {
    private final static Logger LOGGER = LoggerFactory.getLogger(SeckillDistributedController.class);

    private static int corePoolSize = Runtime.getRuntime().availableProcessors();
    // 调整队列数 拒绝服务
    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, corePoolSize + 1, 10l, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(10000));

    @Autowired
    private ISeckillService seckillService;

    @Autowired
    private ISeckillDistributedService seckillDistributedService;

    @Autowired
    private RedisSender redisSender;

    @Autowired
    private KafkaSender kafkaSender;

    @Autowired
    private ActiveMQSender activeMQSender;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 利用redissonClient加锁和释放锁
     * 耗时：1384 ms
     * @param seckillId seckillId
     * @return
     */
    @ApiOperation(value = "秒杀一(Redis分布式锁)", nickname = "科帮网")
    @PostMapping("/startRedisLock")
    public Result startRedisLock(long seckillId) {
        seckillService.deleteSeckill(seckillId);
        final long killId = seckillId;
        int skillNum = 1000;
        final CountDownLatch latch = new CountDownLatch(skillNum);
        Long startTime = System.currentTimeMillis();
        LOGGER.info("开始Redis分布式秒杀一");
        for (int i = 0; i < skillNum; i++) {
            final long userId = i;
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    Result result = seckillDistributedService.startSeckilRedisLock(killId, userId);
                    latch.countDown();
                    LOGGER.info("用户:{}{}", userId, result.get("msg"));
                }
            };
            executor.execute(task);
        }
        try {
            // 等待所有人任务结束
            latch.await();
            Long seckillCount = seckillService.getSeckillCount(seckillId);
            Long endTime = System.currentTimeMillis();
            LOGGER.info("Redis分布式秒杀一结束:一共秒杀出{}件商品,{}", seckillCount, "耗时：" + (endTime - startTime) + " ms");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    /**
     * 利用zookeeper分布式锁
     * 耗时：57785 ms，因为尝试三秒
     * @param seckillId
     * @return
     */
    @ApiOperation(value = "秒杀二(zookeeper分布式锁)", nickname = "科帮网")
    @PostMapping("/startZkLock")
    public Result startZkLock(long seckillId) {
        seckillService.deleteSeckill(seckillId);
        final long killId = seckillId;
        int skillNum = 1000;
        final CountDownLatch latch = new CountDownLatch(skillNum);
        Long startTime = System.currentTimeMillis();
        LOGGER.info("开始Zookeeper分布式秒杀二");
        for (int i = 0; i < skillNum; i++) {
            final long userId = i;
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    Result result = seckillDistributedService.startSeckilZksLock(killId, userId);
                    latch.countDown();
                    LOGGER.info("用户:{}{}", userId, result.get("msg"));
                }
            };
            executor.execute(task);
        }
        try {
            latch.await();
            Long seckillCount = seckillService.getSeckillCount(seckillId);
            Long endTime = System.currentTimeMillis();
            LOGGER.info("Zookeeper分布式秒杀二结束:一共秒杀出{}件商品,{}", seckillCount, "耗时：" + (endTime - startTime) + " ms");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    /**
     * Redis分布式队列-订阅监听,这样就不用所有请求都去请求数据库了
     * 执行很快，利用websocket推送消息给前台
     * 耗时：509 ms
     * 1、大家都去发布要秒杀的消息
     * 2、然后一部分人会去执行数据库操作
     * 3、一旦有人数据库执行失败，就说明秒杀结束
     * 4、后续就算发布了消息的人，也不需要再去执行数据库
     * 5、甚至后面的人都不能发布消息
     * @param seckillId
     * @return
     */
    @ApiOperation(value = "秒杀三(Redis分布式队列-订阅监听)", nickname = "科帮网")
    @PostMapping("/startRedisQueue")
    public Result startRedisQueue(long seckillId) {
        // 秒杀开始
        redisUtil.cacheValue(seckillId + "", null);
        seckillService.deleteSeckill(seckillId);
        final long killId = seckillId;
        int skillNum = 1000;
        Long startTime = System.currentTimeMillis();
        LOGGER.info("开始Redis分布式队列-订阅监听秒杀三");
        for (int i = 0; i < 1000; i++) {
            final long userId = i;
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    if (redisUtil.getValue(killId + "") == null) {
                        // 发布完订阅消息就返回了，所以要思考如何返回给用户信息ws
                        redisSender.sendChannelMess("seckill", killId + ";" + userId);
                        LOGGER.info("用户:{}{}", userId, " Redis发布订阅消息成功");
                    } else {
                        // 秒杀结束
                        LOGGER.info("用户:{}{}", userId, " Redis发布订阅消息失败");
                    }
                }
            };
            executor.execute(task);
        }
        try {
            // 因为发布完消息立马返回了，这时候消息可能还没消费，更别提数据库操作了
            Thread.sleep(10000);
            Long seckillCount = seckillService.getSeckillCount(seckillId);
            Long endTime = System.currentTimeMillis();
            LOGGER.info("Redis分布式队列-订阅监听秒杀三结束:一共秒杀出{}件商品,{}", seckillCount, "耗时：" + (endTime - startTime) + " ms");
            redisUtil.cacheValue(killId + "", null);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    /**
     * 原理同上
     * @param seckillId
     * @return
     */
    @ApiOperation(value = "秒杀四(Kafka分布式队列)", nickname = "科帮网")
    @PostMapping("/startKafkaQueue")
    public Result startKafkaQueue(long seckillId) {
        // 秒杀开始
        redisUtil.cacheValue(seckillId + "", null);
        seckillService.deleteSeckill(seckillId);
        final long killId = seckillId;
        int skillNum = 1000;
        Long startTime = System.currentTimeMillis();
        LOGGER.info("开始Kafka分布式队列秒杀四");
        for (int i = 0; i < skillNum; i++) {
            final long userId = i;
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    if (redisUtil.getValue(killId + "") == null) {
                        // 发布完订阅消息就返回了，所以要思考如何返回给用户信息ws
                        kafkaSender.sendChannelMess("seckill", killId + ";" + userId);
                        LOGGER.info("用户:{}{}", userId, " Kafka分布式队列发布订阅消息成功");
                    } else {
                        // 秒杀结束
                        LOGGER.info("用户:{}{}", userId, " Kafka分布式队列发布订阅消息失败");
                    }
                }
            };
            executor.execute(task);
        }
        try {
            // 因为发布完消息立马返回了，这时候消息可能还没消费，更别提数据库操作了
            Thread.sleep(10000);
            Long seckillCount = seckillService.getSeckillCount(seckillId);
            Long endTime = System.currentTimeMillis();
            LOGGER.info("Kafka分布式队列秒杀四结束:一共秒杀出{}件商品,{}", seckillCount, "耗时：" + (endTime - startTime) + " ms");
            redisUtil.cacheValue(killId + "", null);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    /**
     * 原理同上
     * @param seckillId
     * @return
     */
    @ApiOperation(value = "秒杀五(ActiveMQ分布式队列)", nickname = "科帮网")
    @PostMapping("/startActiveMQQueue")
    public Result startActiveMQQueue(long seckillId) {
        // 秒杀开始
        redisUtil.cacheValue(seckillId + "", null);
        seckillService.deleteSeckill(seckillId);
        final long killId = seckillId;
        int skillNum = 1000;
        Long startTime = System.currentTimeMillis();
        LOGGER.info("开始ActiveMQ分布式队列秒杀五");
        for (int i = 0; i < skillNum; i++) {
            final long userId = i;
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    if (redisUtil.getValue(killId + "") == null) {
                        Destination destination = new ActiveMQQueue("seckill.queue");
                        // 思考如何返回给用户信息ws
                        activeMQSender.sendChannelMess(destination, killId + ";" + userId);
                        LOGGER.info("用户:{}{}", userId, " ActiveMQ分布式队列发布订阅消息成功");
                    } else {
                        // 秒杀结束
                        LOGGER.info("用户:{}{}", userId, " ActiveMQ分布式队列发布订阅消息失败");
                    }
                }
            };
            executor.execute(task);
        }
        try {
            Thread.sleep(10000);
            redisUtil.cacheValue(killId + "", null);
            Long seckillCount = seckillService.getSeckillCount(seckillId);
            Long endTime = System.currentTimeMillis();
            LOGGER.info("ActiveMQ分布式队列秒杀五结束:一共秒杀出{}件商品,{}", seckillCount, "耗时：" + (endTime - startTime) + " ms");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }
}
