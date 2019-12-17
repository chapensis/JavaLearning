package com.imooc.example.ticket.domain;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 模拟一张票被多个人抢的场景
 */
@Data
@ToString
@Entity(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * 票的名字
     */
    private String name;

    /**
     * 票的所有者
     */
    private Long owner;

    /**
     * 锁票的用户
     */
    private Long lockUser;

    /**
     * 票的序列号
     */
    private Long ticketNum;
}
