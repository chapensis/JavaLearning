package com.imooc.example.user.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity(name = "pay_info")
public class PayInfo {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "order_id")
    private Long orderId;

    private String status;

    private int amount;
}
