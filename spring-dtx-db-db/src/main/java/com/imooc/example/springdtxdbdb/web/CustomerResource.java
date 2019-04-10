package com.imooc.example.springdtxdbdb.web;

import com.imooc.example.springdtxdbdb.domain.Order;
import com.imooc.example.springdtxdbdb.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/customer")
public class CustomerResource {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/order")
    public void create(@RequestBody Order order) {
        log.info("CustomerResource create:" + order);
        customerService.createOrder(order);
    }

    @GetMapping("/{id}")
    public Map create(@PathVariable Long id) {
        return customerService.userInfo(id);
    }
}
