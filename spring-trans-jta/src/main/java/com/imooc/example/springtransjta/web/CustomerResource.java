package com.imooc.example.springtransjta.web;

import com.imooc.example.springtransjta.dao.CustomerRepository;
import com.imooc.example.springtransjta.domain.Customer;
import com.imooc.example.springtransjta.service.CustomerServiceTxInAnnotation;
import com.imooc.example.springtransjta.service.CustomerServiceTxInCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("api")
@RestController
public class CustomerResource {

    @Autowired
    private CustomerServiceTxInAnnotation customerServiceTxInAnnotation;

    @Autowired
    private CustomerServiceTxInCode customerServiceTxInCode;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private JmsTemplate jmsTemplate;

    @PostMapping("annotation")
    public Customer createInAnnotation(@RequestBody Customer customer) {
        log.info("CustomerResource create in annotation create customer" + customer);
        return customerServiceTxInAnnotation.create(customer);
    }

    @PostMapping("code")
    public Customer createInCode(@RequestBody Customer customer) {
        log.info("CustomerResource create in code create customer" + customer);
        return customerServiceTxInCode.create(customer);
    }

    @Transactional
    @PostMapping("message/annotation")
    public String createInAnnotationByListener(@RequestBody Customer customer) {
        log.info("CustomerResource create in annotation create customer" + customer);
        jmsTemplate.convertAndSend("customer:annotation:new", customer.getUsername());
        return "success";
    }

    @Transactional
    @PostMapping("message/code")
    public String createInCodeByListener(@RequestBody Customer customer) {
        log.info("CustomerResource create in code create customer" + customer);
        jmsTemplate.convertAndSend("customer:code:new", customer.getUsername());
        return "success";
    }

    @GetMapping("")
    public List<Customer> getAll() {
        return customerRepository.findAll();
    }
}
