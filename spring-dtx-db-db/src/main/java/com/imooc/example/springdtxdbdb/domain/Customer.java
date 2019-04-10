package com.imooc.example.springdtxdbdb.domain;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class Customer {
    private Long id;

    private String username;

    private Integer deposit;
}
