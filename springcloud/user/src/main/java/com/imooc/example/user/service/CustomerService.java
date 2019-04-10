package com.imooc.example.user.service;

import com.imooc.example.common.domain.OrderDTO;
import com.imooc.example.user.dao.CustomerRepository;
import com.imooc.example.user.dao.PayInfoRepository;
import com.imooc.example.user.domain.Customer;
import com.imooc.example.user.domain.PayInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PayInfoRepository payInfoRepository;


    @Autowired
    private JmsTemplate jmsTemplate;

    /**
     * 处理创建订单时，订单已锁住的请求
     * 订单插入数据库并通知缴费
     *
     * @param orderDTO
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    @JmsListener(destination = "order:pay", containerFactory = "msgFactory")
    public void handleOrderPay(OrderDTO orderDTO) {
        log.info("CustomerService got new order for pay:" + orderDTO);

        PayInfo payInfo = payInfoRepository.findOneByOrderId(orderDTO.getId());
        if (payInfo != null) {
            log.warn("order already paid, duplicated message:" + orderDTO);
        } else {
            Optional<Customer> customerOptional = customerRepository.findById(orderDTO.getCustomerId());
            if (customerOptional.isPresent()) {
                Customer customer = customerOptional.get();
                // 检查余额
                if (customer.getDeposit() < orderDTO.getAmount()) {
                    // 余额不足
                    log.warn("Not enough deposit");
                    orderDTO.setStatus("NOT_ENOUGH_DEPOSIT");
                    jmsTemplate.convertAndSend("order:ticket_error", orderDTO);
                    return;
                }
                // customer.setDeposit(customer.getDeposit() - order.getAmount());
                customerRepository.charge(customer.getId(), orderDTO.getAmount());

                PayInfo newPayInfo = new PayInfo();
                newPayInfo.setOrderId(orderDTO.getId());
                newPayInfo.setAmount(orderDTO.getAmount());
                newPayInfo.setStatus("PAID");
                payInfoRepository.save(newPayInfo);
            }
        }
        orderDTO.setStatus("PAID");
        jmsTemplate.convertAndSend("order:ticket_move", orderDTO);
    }
}
