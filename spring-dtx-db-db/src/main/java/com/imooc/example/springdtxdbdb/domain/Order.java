package com.imooc.example.springdtxdbdb.domain;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Order {

    private Long id;

    private String customerId;

    private String title;

    private String amount;
}
