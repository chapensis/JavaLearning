package com.imooc.example.springdtxjmsdb.dao;

import com.imooc.example.springdtxjmsdb.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author yangchang
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
