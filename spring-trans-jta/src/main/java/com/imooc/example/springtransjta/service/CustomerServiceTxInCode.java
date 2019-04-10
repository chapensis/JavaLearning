package com.imooc.example.springtransjta.service;

import com.imooc.example.springtransjta.dao.CustomerRepository;
import com.imooc.example.springtransjta.domain.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Slf4j
@Service
public class CustomerServiceTxInCode {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private JmsTemplate jmsTemplate;

    /**
     * 用代码的方式自定义事务处理
     *
     * @param customer
     * @return
     */
    public Customer create(Customer customer) {
        log.info("CustomerService Tx In Code create customer:" + customer);
        if (customer.getId() != null) {
            throw new RuntimeException("用户已经存在");
        }
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        // def.setIsolationLevel(TransactionDefinition.ISOLATION_DEFAULT);
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setTimeout(15);
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            customer.setUsername("Code:" + customer.getUsername());
            customerRepository.save(customer);
            jmsTemplate.convertAndSend("customer:msg:reply", customer.toString());
            transactionManager.commit(status);
            return customer;
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw e;
        }
    }

    /**
     * 不能有return数据
     *
     * @param name
     */
    @JmsListener(destination = "customer:code:new")
    public void create(String name) {
        log.info("CustomerService Tx In Code by linstener create customer:" + name);
        Customer customer = new Customer();
        customer.setUsername("Code:" + name);
        customer.setPassword("123456");
        customer.setRole("admin");

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        // def.setIsolationLevel(TransactionDefinition.ISOLATION_DEFAULT);
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setTimeout(15);
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            customerRepository.save(customer);
            String username = customer.getUsername();
            jmsTemplate.convertAndSend("customer:code:reply", username);
            // SomeErrorHappened(); 这句话会导致jms消息发送失败，但是如果先执行数据库事务，会使数据库插入成功，待解决
            transactionManager.commit(status);
        } catch (Exception e) {
            log.error("CustomerService Tx In Code by linstener create error", e);
            transactionManager.rollback(status);
            throw e;
        }
    }

    @JmsListener(destination = "customer:code:reply")
    public void reply(String name) {
        log.info("CustomerService Tx In Code by linstener reply custmoer:" + name);
    }

    private void SomeErrorHappened() {
        throw new RuntimeException("我就是要找事情");
    }
}
