package com.imooc.example.springdtxjpadb.domain;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Order {

    private Long id;

    private Long customerId;

    private String title;

    private Integer amount;
}
