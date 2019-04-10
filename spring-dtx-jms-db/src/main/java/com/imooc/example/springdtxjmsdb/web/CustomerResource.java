package com.imooc.example.springdtxjmsdb.web;

import com.imooc.example.springdtxjmsdb.dao.CustomerRepository;
import com.imooc.example.springdtxjmsdb.domain.Customer;
import com.imooc.example.springdtxjmsdb.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/customer")
public class CustomerResource {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @PostMapping("")
    public void create(@RequestBody Customer customer) {
        log.info("CustomerResource create:" + customer);
        customerService.createCustomer(customer);
    }

    @PostMapping("/msg")
    public void create(@RequestParam String msg) {
        log.info("CustomerResource create:" + msg);
        jmsTemplate.convertAndSend("customer:msg:new", msg);
    }

    @GetMapping("")
    public List<Customer> getAll(@PathVariable Long id) {
        return customerRepository.findAll();
    }

    @GetMapping("msg")
    public String getMsg() {
        jmsTemplate.setReceiveTimeout(3000);
        // 主动消费消息,没有消息会自己返回null
        Object reply = jmsTemplate.receiveAndConvert("customer:msg:reply");
        return String.valueOf(reply);
    }
}
