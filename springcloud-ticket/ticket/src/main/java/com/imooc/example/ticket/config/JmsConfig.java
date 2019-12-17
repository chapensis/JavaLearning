package com.imooc.example.ticket.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.TransactionAwareConnectionFactoryProxy;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.transaction.PlatformTransactionManager;

import javax.jms.ConnectionFactory;

@EnableJms
@Configuration
public class JmsConfig {

    /**
     * 相当于在activemq的Transaction上面做了一层封装
     * 配置好了以后，就会尽最大努力一次提交
     *
     * @return
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("tcp://192.168.159.130:61616");
        cf.setTrustAllPackages(true);
        // 可以同步mq的事务和数据库的事务
        TransactionAwareConnectionFactoryProxy proxy = new TransactionAwareConnectionFactoryProxy();
        proxy.setTargetConnectionFactory(cf);
        proxy.setSynchedLocalTransactionAllowed(true);
        return proxy;
    }

    /**
     * 要求jmsTemplate发送消息是在事务中发送消息
     * 发送完消息不要关闭session
     * 方便后续回滚
     *
     * @param connectionFactory
     * @return
     */
    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setSessionTransacted(true);
        return jmsTemplate;
    }

    @Bean
    public JmsListenerContainerFactory<?> msgFactory(ConnectionFactory cf,
                                                     PlatformTransactionManager transactionManager,
                                                     DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, cf);
        factory.setReceiveTimeout(1000L);
        factory.setTransactionManager(transactionManager);
        // 设置并发数
        factory.setConcurrency("10");
        return factory;
    }

    /**
     * jms收发消息的时候会自动把java对象转换成json数据
     * @return
     */
    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }
}

