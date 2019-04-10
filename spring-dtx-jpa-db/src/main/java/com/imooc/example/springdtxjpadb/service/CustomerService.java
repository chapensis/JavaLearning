package com.imooc.example.springdtxjpadb.service;

import com.imooc.example.springdtxjpadb.dao.CustomerRepository;
import com.imooc.example.springdtxjpadb.domain.Customer;
import com.imooc.example.springdtxjpadb.domain.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    @Qualifier("orderJdbcTemplate")
    private JdbcTemplate orderJdbcTemplate;

    /**
     * 创建订单
     */
    private static final String SQL_CREATE_ORDER = "INSERT INTO customer_order (customer_id, title, amount) values (?, ?, ?)";

    /**
     * 因为默认事务只在第一个数据库访问中生效
     * @param order
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void createOrder(Order order) {
        Customer customer = customerRepository.findById(order.getCustomerId()).get();
        customer.setDeposit(customer.getDeposit() - order.getAmount());
        customerRepository.save(customer);
        if (order.getTitle().contains("error1")) {
            throw new RuntimeException("Error1");
        }
        orderJdbcTemplate.update(SQL_CREATE_ORDER, order.getCustomerId(), order.getTitle(), order.getAmount());
        // 虽然它会抛异常，但是orderJdbcTemplate提交的事务不会回滚
        if (order.getTitle().contains("error2")) {
            throw new RuntimeException("Error2");
        }
    }

    public Map userInfo(Long customerId) {
        Customer customer = customerRepository.findById(customerId).get();
        List orders = orderJdbcTemplate.queryForList("select * from customer_order where customer_id = " + customerId);
        Map result = new HashMap();
        result.put("customer", customer);
        result.put("orders", orders);
        return result;
    }
}
