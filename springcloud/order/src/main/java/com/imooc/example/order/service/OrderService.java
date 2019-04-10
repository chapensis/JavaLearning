package com.imooc.example.order.service;

import com.imooc.example.common.domain.OrderDTO;
import com.imooc.example.order.dao.OrderRepository;
import com.imooc.example.order.domain.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private JmsTemplate jmsTemplate;

    /**
     * 处理创建订单时，订单已锁住的请求
     * 订单插入数据库并通知缴费
     *
     * @param orderDTO
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    @JmsListener(destination = "order:locked", containerFactory = "msgFactory")
    public void handleOrderLocked(OrderDTO orderDTO) {
        log.info("OrderService got new order to create:" + orderDTO);
        // 如果消息已经处理过则不处理
        if (orderRepository.findOneByUuid(orderDTO.getUuid()) != null) {
            log.info("Msg already processed:", orderDTO);
        } else {
            Order order = createOrder(orderDTO);
            // 先保存才会有Id
            orderRepository.save(order);
            orderDTO.setId(order.getId());
        }
        orderDTO.setStatus("NEW");
        jmsTemplate.convertAndSend("order:pay", orderDTO);
    }

    /**
     * 订单锁票失败，情况
     * 1、一开始锁票失败
     * 2、扣费失败后，解锁票，然后触发
     * 3、定时任务检测到订单超时
     *
     * @param orderDTO
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    @JmsListener(destination = "order:fail", containerFactory = "msgFactory")
    public void handleOrderFailed(OrderDTO orderDTO) {
        log.info("OrderService got order fail:" + orderDTO);
        Order order = null;
        // 如果订单没有被处理过，则不需要处理订单，同时表示是因为锁票失败
        if (orderDTO.getId() == null) {
            order = createOrder(orderDTO);
            order.setReason("TICKET_LOCKED_FAIL");
        } else {
            Optional<Order> optionalOrder = orderRepository.findById(orderDTO.getId());
            if (!optionalOrder.isPresent()) {
                return;
            }
            order = optionalOrder.get();
            // 如果是因为余额不足
            if ("NOT_ENOUGH_DEPOSIT".equals(orderDTO.getStatus())) {
                order.setReason("NOT_ENOUGH_DEPOSIT");
            }
            // 如果是因为订单超时
            if ("TIMEOUT".equals(orderDTO.getStatus())) {
                order.setReason("TIMEOUT");
            }
        }
        // 最后订单状态都是失败状态
        order.setStatus("FAIL");
        orderRepository.save(order);
    }

    /**
     * 订单完成
     *
     * @param orderDTO
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    @JmsListener(destination = "order:finish", containerFactory = "msgFactory")
    public void handleOrderFinish(OrderDTO orderDTO) {
        log.info("OrderService got new order to finish:" + orderDTO);
        // 如果消息已经处理过则不处理
        Optional<Order> optionalOrder = orderRepository.findById(orderDTO.getId());
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setStatus("FINISH");
            orderRepository.save(order);
        }
    }

    /**
     * 根据传输对象创建Order对象
     *
     * @param orderDTO
     * @return
     */
    private Order createOrder(OrderDTO orderDTO) {
        Order order = new Order();
        BeanUtils.copyProperties(orderDTO, order);
        order.setStatus("NEW");
        order.setCreateTime(ZonedDateTime.now());
        return order;
    }

    /**
     * 每次执行完，每隔10秒再检查一次
     */
    @Scheduled(fixedDelay = 10000L)
    public void checkTimeoutOrders() {
        log.info("正在检查超时订单...");
        ZonedDateTime checkTime = ZonedDateTime.now().minusMinutes(1L);
        List<Order> orders = orderRepository.findAllByStatusAndCreateTimeBefore("NEW", checkTime);
        orders.forEach(order -> {
            log.error("Order timeout:" + order);
            OrderDTO orderDTO = new OrderDTO();
            BeanUtils.copyProperties(order, orderDTO);
            orderDTO.setStatus("TIMEOUT");
            jmsTemplate.convertAndSend("order:fail", orderDTO);
        });
    }
}
