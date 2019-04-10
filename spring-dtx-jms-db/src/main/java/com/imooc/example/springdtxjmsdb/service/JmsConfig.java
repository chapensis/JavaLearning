package com.imooc.example.springdtxjmsdb.service;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.TransactionAwareConnectionFactoryProxy;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;

@Configuration
public class JmsConfig {

    /**
     * 相当于在activemq的Transaction上面做了一层封装
     * 配置好了以后，就会尽最大努力一次提交
     * @return
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        ConnectionFactory cf = new ActiveMQConnectionFactory("tcp://192.168.159.130:61616");
        TransactionAwareConnectionFactoryProxy proxy = new TransactionAwareConnectionFactoryProxy();
        proxy.setTargetConnectionFactory(cf);
        proxy.setSynchedLocalTransactionAllowed(true);
        return proxy;
    }

    /**
     * 要求jmsTemplate发送消息是在事务中发送消息
     * 发送完消息不要关闭session
     * 方便后续回滚
     * @param connectionFactory
     * @return
     */
    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setSessionTransacted(true);
        return jmsTemplate;
    }
}

