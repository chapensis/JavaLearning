package com.imooc.example.ticket.service;

import com.imooc.example.common.domain.OrderDTO;
import com.imooc.example.ticket.dao.TicketRepository;
import com.imooc.example.ticket.domain.Ticket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private JmsTemplate jmsTemplate;

    /**
     * 处理创建订单的请求，先锁票
     *
     * @param orderDTO
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    @JmsListener(destination = "order:new", containerFactory = "msgFactory")
    public void handleTicketLock(OrderDTO orderDTO) {
        log.info("TicketService got new order for order lock:" + orderDTO);
        int count = ticketRepository.lockTicket(orderDTO.getCustomerId(), orderDTO.getTicketNum());
        if (count == 1) {
            orderDTO.setStatus("TICKET_LOCKED");
            jmsTemplate.convertAndSend("order:locked", orderDTO);
        } else {
            orderDTO.setStatus("TICKET_LOCKED_FAIL");
            jmsTemplate.convertAndSend("order:fail", orderDTO);
        }
    }

    /**
     * 处理票失败的情况，将票解锁同时票回滚
     *
     * @param orderDTO
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    @JmsListener(destination = "order:ticket_error", containerFactory = "msgFactory")
    public void handleTicketError(OrderDTO orderDTO) {
        log.info("TicketService got ticket order to unlock:" + orderDTO);
        int count = ticketRepository.unlockTicket(orderDTO.getCustomerId(), orderDTO.getTicketNum());
        if (count == 0) {
            log.warn("Ticket already unlocked:" + orderDTO);
        }
        int unmoveCount = ticketRepository.unmoveTicket(orderDTO.getCustomerId(), orderDTO.getTicketNum());
        if (unmoveCount == 0) {
            log.warn("Ticket already unmoved or not moved:" + orderDTO);
        }
        jmsTemplate.convertAndSend("order:fail", orderDTO);
    }

    /**
     * 交票操作
     *
     * @param orderDTO
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    @JmsListener(destination = "order:ticket_move", containerFactory = "msgFactory")
    public void handleTicketMove(OrderDTO orderDTO) {
        log.info("TicketService got new order for ticket move:" + orderDTO);
        int count = ticketRepository.moveTicket(orderDTO.getCustomerId(), orderDTO.getTicketNum());
        if (count == 0) {
            log.warn("ticket already move:" + orderDTO);
        }
        orderDTO.setStatus("TICKET_MOVE");
        jmsTemplate.convertAndSend("order:finish", orderDTO);
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Ticket ticketLock(OrderDTO orderDTO) {
        Ticket ticket = ticketRepository.findOneByTicketNum(orderDTO.getTicketNum());
        ticket.setLockUser(orderDTO.getCustomerId());
        ticket = ticketRepository.save(ticket);

        try {
            Thread.sleep(10 * 1000);
        } catch (Exception e) {
            log.error("sleep error:", e);
        }

        return ticket;
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public int ticketLock2(OrderDTO orderDTO) {
        int count = ticketRepository.lockTicket(orderDTO.getCustomerId(), orderDTO.getTicketNum());
        log.info("lock ticker count " + count);

        return count;
    }
}
