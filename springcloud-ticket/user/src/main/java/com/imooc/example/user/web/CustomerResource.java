package com.imooc.example.user.web;

import com.imooc.example.user.dao.CustomerRepository;
import com.imooc.example.user.domain.Customer;
import com.imooc.example.user.feign.OrderClient;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("api/customer")
@RestController
public class CustomerResource {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderClient orderClient;

    @PostMapping("")
    public Customer create(@RequestBody Customer customer) {
        return customerRepository.save(customer);
    }

    @HystrixCommand
    @GetMapping("")
    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    @HystrixCommand
    @GetMapping("order")
    public String getMyOrder() {
        return orderClient.getMyOrders();
    }
}
