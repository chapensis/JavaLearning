package com.imooc.example.order.dao;

import com.imooc.example.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findOneByCustomerId(Long customerId);

    /**
     * 选择早于checkTime并且订单状态是未完成的订单
     * 这些订单都认为是超时订单
     * @param status
     * @param checkTime
     * @return
     */
    List<Order> findAllByStatusAndCreateTimeBefore(String status, ZonedDateTime checkTime);

    Order findOneByUuid(String uuid);
}
