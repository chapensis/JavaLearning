package com.imooc.example.order.web;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.imooc.example.common.domain.OrderDTO;
import com.imooc.example.order.dao.OrderRepository;
import com.imooc.example.order.domain.Order;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("api/order")
@RestController
public class OrderResource {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private JmsTemplate jmsTemplate;

    /**
     * 基于时间的UUID生成器
     */
    private TimeBasedGenerator uuidGenerator = Generators.timeBasedGenerator();

    /**
     * 订单服务直接把创建订单的请求发送到MQ
     * @param orderDTO orderDTO
     */
    @PostMapping("")
    public void create(@RequestBody OrderDTO orderDTO) {
        log.info("OrderResource create order:" + orderDTO);
        orderDTO.setUuid(uuidGenerator.generate().toString());
        jmsTemplate.convertAndSend("order:new", orderDTO);
    }

    @HystrixCommand
    @GetMapping("")
    public List<Order> getAll() {
        return orderRepository.findAll();
    }
}
