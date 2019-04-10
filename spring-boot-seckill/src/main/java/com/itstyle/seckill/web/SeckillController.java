package com.itstyle.seckill.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itstyle.seckill.common.entity.Result;
import com.itstyle.seckill.common.entity.SuccessKilled;
import com.itstyle.seckill.queue.disruptor.DisruptorUtil;
import com.itstyle.seckill.queue.disruptor.SeckillEvent;
import com.itstyle.seckill.queue.jvm.SeckillQueue;
import com.itstyle.seckill.service.ISeckillService;

/**
 * @author yangchang
 */
@Api(tags = "秒杀")
@RestController
@RequestMapping("/seckill")
public class SeckillController {
    private final static Logger LOGGER = LoggerFactory.getLogger(SeckillController.class);

    /**
     * 返回Java处理器的数量
     */
    private static int corePoolSize = Runtime.getRuntime().availableProcessors();

    /**
     * 创建线程池  调整队列数 拒绝服务
     */
    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, corePoolSize + 10, 10L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(1000));

    /**
     * 思考：为什么不用synchronized
     * service 默认是单例的，并发下lock只有一个实例
     * 互斥锁 参数默认false，不公平锁
     */
    private Lock lock = new ReentrantLock(true);

    @Autowired
    private ISeckillService seckillService;

    /**
     * 普通秒杀，查询数据库资源够不够，够就秒杀（会出现多线程资源竞争问题）
     *
     * @param seckillId
     * @return
     */
    @ApiOperation(value = "秒杀一(最low实现)", nickname = "科帮网")
    @PostMapping("/start")
    public Result start(long seckillId) {
        // 为了每次体验都是新的，先删除以前秒杀的记录
        seckillService.deleteSeckill(seckillId);
        // N个购买者
        int skillNum = 100;
        final CountDownLatch latch = new CountDownLatch(skillNum);
        final long killId = seckillId;
        Long startTime = System.currentTimeMillis();
        LOGGER.info("开始秒杀一(竞争太激烈，商品少会出现超卖，商品多很多人由于限流策略访问不到数据库)");
        for (int i = 0; i < skillNum; i++) {
            final long userId = i;
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    // 会被service的限流策略限制，会返回空
                    Result result = seckillService.startSeckil(killId, userId);
                    if (result != null) {
                        LOGGER.info("用户:{}{}", userId, result.get("msg"));
                    } else {
                        LOGGER.info("用户:{}{}", userId, "哎呦喂，人也太多了，请稍后！");
                    }
                    latch.countDown();
                }
            };
            executor.execute(task);
        }
        try {
            // 等待所有人任务结束
            latch.await();
            Long seckillCount = seckillService.getSeckillCount(seckillId);
            Long endTime = System.currentTimeMillis();
            LOGGER.info("秒杀一结束:一共秒杀出{}件商品,{}", seckillCount, "耗时：" + (endTime - startTime) + " ms");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    /**
     * 查数据库和修改数量之前加锁lock.lock()
     *
     * @param seckillId
     * @return
     */
    @ApiOperation(value = "秒杀二(程序锁)", nickname = "科帮网")
    @PostMapping("/startLock")
    public Result startLock(long seckillId) {
        // 为了每次体验都是新的，先删除以前秒杀的记录
        seckillService.deleteSeckill(seckillId);
        int skillNum = 1000;
        // N个购买者
        final CountDownLatch latch = new CountDownLatch(skillNum);
        final long killId = seckillId;
        Long startTime = System.currentTimeMillis();
        LOGGER.info("开始秒杀二(不太正常)");
        for (int i = 0; i < 1000; i++) {
            final long userId = i;
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    Result result = seckillService.startSeckilLock(killId, userId);
                    if (result != null) {
                        LOGGER.info("用户:{}{}", userId, result.get("msg"));
                    } else {
                        LOGGER.info("用户:{}{}", userId, "哎呦喂，人也太多了，请稍后！");
                    }
                    latch.countDown();
                }
            };
            executor.execute(task);
        }
        try {
            // 等待所有人任务结束
            latch.await();
            Long seckillCount = seckillService.getSeckillCount(seckillId);
            Long endTime = System.currentTimeMillis();
            LOGGER.info("秒杀二结束:一共秒杀出{}件商品,{}", seckillCount, "耗时：" + (endTime - startTime) + " ms");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    /**
     * 通过切面来进行加锁（在进入方法前加锁，执行完释放），这样就不存在锁已释放，但是事务还没提交的BUG
     *
     * @param seckillId
     * @return
     */
    @ApiOperation(value = "秒杀三(AOP程序锁)", nickname = "科帮网")
    @PostMapping("/startAopLock")
    public Result startAopLock(long seckillId) {
        seckillService.deleteSeckill(seckillId);
        // N个购买者
        int skillNum = 1000;
        final CountDownLatch latch = new CountDownLatch(skillNum);
        final long killId = seckillId;
        Long startTime = System.currentTimeMillis();
        LOGGER.info("开始秒杀三(正常)");
        for (int i = 0; i < 1000; i++) {
            final long userId = i;
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    Result result = seckillService.startSeckilAopLock(killId, userId);
                    if (result != null) {
                        LOGGER.info("用户:{}{}", userId, result.get("msg"));
                    } else {
                        LOGGER.info("用户:{}{}", userId, "哎呦喂，人也太多了，请稍后！");
                    }
                    latch.countDown();
                }
            };
            executor.execute(task);
        }
        try {
            // 等待所有人任务结束
            latch.await();
            Long seckillCount = seckillService.getSeckillCount(seckillId);
            Long endTime = System.currentTimeMillis();
            LOGGER.info("秒杀三结束:一共秒杀出{}件商品,{}", seckillCount, "耗时：" + (endTime - startTime) + " ms");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    /**
     * 通过在查询数量时添加FOR UPDATE关键字，让别人无法修改这条数据
     *
     * @param seckillId
     * @return
     */
    @ApiOperation(value = "秒杀四(数据库悲观锁)", nickname = "科帮网")
    @PostMapping("/startDBPCC_ONE")
    public Result startDBPCC_ONE(long seckillId) {
        seckillService.deleteSeckill(seckillId);
        // N个购买者
        int skillNum = 1000;
        final CountDownLatch latch = new CountDownLatch(skillNum);
        final long killId = seckillId;
        Long startTime = System.currentTimeMillis();
        LOGGER.info("开始秒杀四(正常)");
        for (int i = 0; i < 1000; i++) {
            final long userId = i;
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    Result result = seckillService.startSeckilDBPCC_ONE(killId, userId);
                    if (result != null) {
                        LOGGER.info("用户:{}{}", userId, result.get("msg"));
                    } else {
                        LOGGER.info("用户:{}{}", userId, "哎呦喂，人也太多了，请稍后！");
                    }
                    latch.countDown();
                }
            };
            executor.execute(task);
        }
        try {
            // 等待所有人任务结束
            latch.await();
            Long seckillCount = seckillService.getSeckillCount(seckillId);
            Long endTime = System.currentTimeMillis();
            LOGGER.info("秒杀四结束:一共秒杀出{}件商品,{}", seckillCount, "耗时：" + (endTime - startTime) + " ms");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    /**
     * 数据库修改库存时限制number>0
     * 最大缺点：尽管秒杀已经结束了，但是所有人的请求还是会打到数据库，这就没必要了，所以需要发布-订阅
     * @param seckillId
     * @return
     */
    @ApiOperation(value = "秒杀五(数据库悲观锁)", nickname = "科帮网")
    @PostMapping("/startDPCC_TWO")
    public Result startDPCC_TWO(long seckillId) {
        seckillService.deleteSeckill(seckillId);
        // N个购买者
        int skillNum = 1000;
        final CountDownLatch latch = new CountDownLatch(skillNum);
        final long killId = seckillId;
        Long startTime = System.currentTimeMillis();
        LOGGER.info("开始秒杀五(正常、数据库锁最优实现)");
        for (int i = 0; i < 1000; i++) {
            final long userId = i;
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    Result result = seckillService.startSeckilDBPCC_TWO(killId, userId);
                    if (result != null) {
                        LOGGER.info("用户:{}{}", userId, result.get("msg"));
                    } else {
                        LOGGER.info("用户:{}{}", userId, "哎呦喂，人也太多了，请稍后！");
                    }
                    latch.countDown();
                }
            };
            executor.execute(task);
        }
        try {
            // 等待所有人任务结束
            latch.await();
            Long seckillCount = seckillService.getSeckillCount(seckillId);
            Long endTime = System.currentTimeMillis();
            LOGGER.info("秒杀五结束:一共秒杀出{}件商品,{}", seckillCount, "耗时：" + (endTime - startTime) + " ms");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    /**
     * 通过添加version字段来实现乐观锁
     * @param seckillId
     * @return
     */
    @ApiOperation(value = "秒杀六(数据库乐观锁)", nickname = "科帮网")
    @PostMapping("/startDBOCC")
    public Result startDBOCC(long seckillId) {
        seckillService.deleteSeckill(seckillId);
        // N个购买者
        int skillNum = 1000;
        final CountDownLatch latch = new CountDownLatch(skillNum);
        final long killId = seckillId;
        Long startTime = System.currentTimeMillis();
        LOGGER.info("开始秒杀六(正常、数据库锁最优实现)");
        for (int i = 0; i < 1000; i++) {
            final long userId = i;
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    // 这里使用的乐观锁、可以自定义抢购数量、如果配置的抢购人数比较少、比如120:100(人数:商品) 会出现少买的情况
                    // 用户同时进入会出现更新失败的情况
                    Result result = seckillService.startSeckilDBOCC(killId, userId, 1);
                    if (result != null) {
                        LOGGER.info("用户:{}{}", userId, result.get("msg"));
                    } else {
                        LOGGER.info("用户:{}{}", userId, "哎呦喂，人也太多了，请稍后！");
                    }
                    latch.countDown();
                }
            };
            executor.execute(task);
        }
        try {
            // 等待所有人任务结束
            latch.await();
            Long seckillCount = seckillService.getSeckillCount(seckillId);
            Long endTime = System.currentTimeMillis();
            LOGGER.info("秒杀六结束:一共秒杀出{}件商品,{}", seckillCount, "耗时：" + (endTime - startTime) + " ms");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    /**
     * 通过实现一个秒杀队列，来一个请求就往队列放一个请求，类似于分布式的发布-订阅
     * @param seckillId
     * @return
     */
    @ApiOperation(value = "秒杀柒(进程内队列)", nickname = "科帮网")
    @PostMapping("/startQueue")
    public Result startQueue(long seckillId) {
        seckillService.deleteSeckill(seckillId);
        // N个购买者
        int skillNum = 1000;
        final long killId = seckillId;
        Long startTime = System.currentTimeMillis();
        LOGGER.info("开始秒杀柒(正常)");
        for (int i = 0; i < 1000; i++) {
            final long userId = i;
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    SuccessKilled kill = new SuccessKilled();
                    kill.setSeckillId(killId);
                    kill.setUserId(userId);
                    try {
                        Boolean flag = SeckillQueue.getMailQueue().produce(kill);
                        if (flag) {
                            LOGGER.info("用户:{}{}", kill.getUserId(), " 进程内队列，入队成功，等待秒杀");
                        } else {
                            LOGGER.info("用户:{}{}", userId, " 进程内队列，入队失败，秒杀失败");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        LOGGER.info("用户:{}{}", userId, "秒杀失败");
                    }
                }
            };
            executor.execute(task);
        }
        try {
            Thread.sleep(10000);
            Long seckillCount = seckillService.getSeckillCount(seckillId);
            Long endTime = System.currentTimeMillis();
            LOGGER.info("秒杀七结束:一共秒杀出{}件商品,{}", seckillCount, "耗时：" + (endTime - startTime) + " ms");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    /**
     * 待研究
     * @param seckillId
     * @return
     */
    @ApiOperation(value = "秒杀八(Disruptor队列)", nickname = "科帮网")
    @PostMapping("/startDisruptorQueue")
    public Result startDisruptorQueue(long seckillId) {
        seckillService.deleteSeckill(seckillId);
        // N个购买者
        int skillNum = 1000;
        final CountDownLatch latch = new CountDownLatch(skillNum);
        final long killId = seckillId;
        Long startTime = System.currentTimeMillis();
        LOGGER.info("开始秒杀八(正常)");
        for (int i = 0; i < 1000; i++) {
            final long userId = i;
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    SeckillEvent kill = new SeckillEvent();
                    kill.setSeckillId(killId);
                    kill.setUserId(userId);
                    DisruptorUtil.producer(kill);
                    latch.countDown();
                }
            };
            executor.execute(task);
        }
        try {
            // 等待所有人任务结束
            latch.await();
            Long seckillCount = seckillService.getSeckillCount(seckillId);
            Long endTime = System.currentTimeMillis();
            LOGGER.info("秒杀八结束:一共秒杀出{}件商品,{}", seckillCount, "耗时：" + (endTime - startTime) + " ms");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }
}
