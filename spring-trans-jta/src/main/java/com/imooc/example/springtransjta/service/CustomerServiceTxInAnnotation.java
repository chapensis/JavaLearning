package com.imooc.example.springtransjta.service;

import com.imooc.example.springtransjta.dao.CustomerRepository;
import com.imooc.example.springtransjta.domain.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CustomerServiceTxInAnnotation {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private JmsTemplate jmsTemplate;

    /**
     * 使用Transactional注解的方式提交事务
     *
     * @param customer
     * @return
     */
    @Transactional
    public Customer create(Customer customer) {
        log.info("CustomerService Tx In Annotation create customer:" + customer);
        if (customer.getId() != null) {
            throw new RuntimeException("用户已经存在");
        }
        customer.setUsername("Annotation:" + customer.getUsername());
        customerRepository.save(customer);
        jmsTemplate.convertAndSend("customer:msg:reply", customer.toString());
        return customer;
    }

    /**
     * activemq 监听队列
     *
     * @param name
     */
    @JmsListener(destination = "customer:annotation:new")
    public void create(String name) {
        log.info("CustomerService Tx In Annotation by linstener create customer:" + name);
        Customer customer = new Customer();
        customer.setUsername("Annotation:" + name);
        customer.setPassword("123456");
        customer.setRole("user");
        customerRepository.save(customer);

        jmsTemplate.convertAndSend("customer:msg:reply", customer.toString());
    }

    @JmsListener(destination = "customer:msg:reply")
    public void reply(String name) {
        log.info("CustomerService Tx In Annotation by linstener reply customer:" + name);
    }
}
