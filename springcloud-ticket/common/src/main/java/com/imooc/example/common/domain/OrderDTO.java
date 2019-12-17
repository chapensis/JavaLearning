package com.imooc.example.common.domain;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * order是关键字（order by），数据库不能创建order表
 */
@Data
@ToString
public class OrderDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 订单的唯一标识
     */
    private String uuid;

    /**
     * 哪个用户创建的订单
     */
    private Long customerId;

    private String title;

    /**
     * 该订单定的是哪张票
     */
    private Long ticketNum;

    private Integer amount;

    /**
     * 订单状态
     */
    private String status;

    /**
     * 订单出错的原因
     */
    private String reason;

    /**
     * 订单创建时间
     */
    private ZonedDateTime createTime;
}
