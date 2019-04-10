package com.itstyle.seckill.queue.activemq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import com.itstyle.seckill.common.entity.Result;
import com.itstyle.seckill.common.enums.SeckillStatEnum;
import com.itstyle.seckill.common.redis.RedisUtil;
import com.itstyle.seckill.common.webSocket.WebSocketServer;
import com.itstyle.seckill.service.ISeckillService;

@Service
public class ActiveMQConsumer {

    @Autowired
    private ISeckillService seckillService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 使用JmsListener配置消费者监听的队列，其中text是接收到的消息
     * 如果receiveQueue方法报错，listner的事务会回滚，会重新从队列里面拿数据，重试七次
     * 如果添加了@Transactional注解，那就只会尝试一次
     * @param message
     */
    @JmsListener(destination = "seckill.queue")
    public void receiveQueue(String message) {
        // 收到通道的消息之后执行秒杀操作(超卖)
        String[] array = message.split(";");
        Result result = seckillService.startSeckilDBPCC_TWO(Long.parseLong(array[0]), Long.parseLong(array[1]));
        if (result.equals(Result.ok(SeckillStatEnum.SUCCESS))) {
            // 推送给前台
            WebSocketServer.sendInfo("ActiveMQConsumer秒杀成功", array[0]);
        } else {
            // 推送给前台
            WebSocketServer.sendInfo("ActiveMQConsumer秒杀失败", array[0]);
            // 秒杀结束
            redisUtil.cacheValue(array[0], "ok");
        }
    }
}
