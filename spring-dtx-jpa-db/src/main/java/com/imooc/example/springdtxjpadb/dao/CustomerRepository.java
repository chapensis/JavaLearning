package com.imooc.example.springdtxjpadb.dao;

import com.imooc.example.springdtxjpadb.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author yangchang
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
