package com.imooc.example.springtransjta.dao;

import com.imooc.example.springtransjta.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findOneByUsername(String username);
}
