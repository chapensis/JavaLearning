package com.imooc.example.springdtxjpadb.domain;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@ToString
@Data
@Entity(name = "customer")
public class Customer {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String username;

    @Column
    private Integer deposit;
}
