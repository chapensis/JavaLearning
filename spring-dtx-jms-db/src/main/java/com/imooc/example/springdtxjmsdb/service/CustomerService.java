package com.imooc.example.springdtxjmsdb.service;

import com.imooc.example.springdtxjmsdb.dao.CustomerRepository;
import com.imooc.example.springdtxjmsdb.domain.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * 监听放到customer:msg:new的消息
     * 因此这里是一个消费者
     * @param msg
     */
    @JmsListener(destination = "customer:msg:new")
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void handle(String msg) {
        log.info("CustomerService Active mq get msg:" + msg);
        Customer customer = new Customer();
        customer.setUsername(msg);
        customer.setDeposit(100);
        customerRepository.save(customer);
        if (msg.contains("error1")) {
            throw new RuntimeException("Handle Error1");
        }
        jmsTemplate.convertAndSend("customer:msg:reply", msg);
        // 如果这里抛错，数据库插入会报错，但是消息队列还是会有消息
        if (msg.contains("error2")) {
            throw new RuntimeException("Handle Error2");
        }
    }

    @Autowired
    private JmsTemplate jmsTemplate;

    /**
     * 因为默认事务只在第一个数据库访问中生效
     *
     * @param customer customer
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public Customer createCustomer(Customer customer) {
        Customer result = customerRepository.save(customer);
        if (customer.getUsername().contains("error1")) {
            throw new RuntimeException("Error1");
        }
        jmsTemplate.convertAndSend("customer:msg:reply", customer.getUsername());
        // 虽然它会抛异常，但是customerRepository提交的事务不会回滚
        if (customer.getUsername().contains("error2")) {
            throw new RuntimeException("Error2");
        }

        return result;
    }
}
