package com.imooc.example.springdtxjpadb.service;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class DBConfiguration {

    /**
     * 专门用于配置数据库的连接属性
     *
     * @return
     */
    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.user")
    public DataSourceProperties userDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource userDataSource() {
        return userDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    /**
     * 这个方法的名字还必须叫做entityManagerFactory
     * @return
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(false);

        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setJpaVendorAdapter(vendorAdapter);
        factoryBean.setDataSource(userDataSource());
        factoryBean.setPackagesToScan("com.imooc.example");

        return factoryBean;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager userTM = new JpaTransactionManager();
        userTM.setEntityManagerFactory(entityManagerFactory().getObject());

        DataSourceTransactionManager orderTM = new DataSourceTransactionManager(orderDataSource());
        // 放在前面的DataSourceTransactionManager后提交
        ChainedTransactionManager tm = new ChainedTransactionManager(userTM, orderTM);
        return tm;
    }

//    /**
//     * 链式事务管理器，这样子也能管理JPA的事务
//     * @return
//     */
//    @Bean
//    public PlatformTransactionManager transactionManager() {
//        DataSourceTransactionManager userTM = new DataSourceTransactionManager(userDataSource());
//        DataSourceTransactionManager orderTM = new DataSourceTransactionManager(orderDataSource());
//        // 放在前面的DataSourceTransactionManager后提交
//        ChainedTransactionManager tm = new ChainedTransactionManager(userTM, orderTM);
//        return tm;
//    }

    @Bean
    @ConfigurationProperties(prefix = "spring.order")
    public DataSourceProperties orderDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource orderDataSource() {
        return orderDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean
    public JdbcTemplate orderJdbcTemplate(@Qualifier("orderDataSource") DataSource orderDataSource) {
        return new JdbcTemplate(orderDataSource);
    }
}
