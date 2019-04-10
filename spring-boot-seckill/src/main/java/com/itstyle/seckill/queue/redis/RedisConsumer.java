package com.itstyle.seckill.queue.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itstyle.seckill.common.entity.Result;
import com.itstyle.seckill.common.enums.SeckillStatEnum;
import com.itstyle.seckill.common.redis.RedisUtil;
import com.itstyle.seckill.common.webSocket.WebSocketServer;
import com.itstyle.seckill.service.ISeckillService;

/**
 * 消费者
 *
 * @author 科帮网 By https://blog.52itstyle.com
 */
@Service
public class RedisConsumer {

    @Autowired
    private ISeckillService seckillService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 通过listenerAdapter配置收到发布的消息之后要执行的方法
     * @param message
     */
    public void receiveMessage(String message) {
        // 收到通道的消息之后执行秒杀操作(超卖)
        String[] array = message.split(";");
        // controller层已经判断了，其实这里不需要再判断了，虽然有很大一部分人能发布消息，但是并不代表每个消息都会秒杀成功
        if (redisUtil.getValue(array[0]) == null) {
            Result result = seckillService.startSeckilDBPCC_TWO(Long.parseLong(array[0]), Long.parseLong(array[1]));
            if (result.equals(Result.ok(SeckillStatEnum.SUCCESS))) {
                // 推送给前台
                WebSocketServer.sendInfo("RedisConsumer秒杀成功", array[0]);
            } else {
                // 推送给前台
                WebSocketServer.sendInfo("RedisConsumer秒杀失败", array[0]);
                // 真正执行的数据库操作，有人失败就说明秒杀结束，将秒杀的Id放入缓存，下次就不会再进来执行数据库了
                redisUtil.cacheValue(array[0], "ok");
            }
        } else {
            // 推送给前台
            WebSocketServer.sendInfo("RedisConsumer秒杀失败 " + redisUtil.getValue(array[0]), array[0]);
        }
    }
}