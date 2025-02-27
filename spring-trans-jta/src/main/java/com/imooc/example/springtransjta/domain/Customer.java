package com.imooc.example.springtransjta.domain;

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

    @Column(name = "user_name", unique = true)
    private String username;

    private String password;

    private String role;
}
