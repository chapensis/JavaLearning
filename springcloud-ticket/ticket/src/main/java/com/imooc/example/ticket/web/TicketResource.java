package com.imooc.example.ticket.web;

import com.imooc.example.common.domain.OrderDTO;
import com.imooc.example.ticket.dao.TicketRepository;
import com.imooc.example.ticket.domain.Ticket;
import com.imooc.example.ticket.service.TicketService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@RequestMapping("api/ticket")
@RestController
public class TicketResource {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketService ticketService;

    @PostConstruct
    public void init() {
        if (ticketRepository.count() > 0) {
            return;
        }
        Ticket ticket = new Ticket();
        ticket.setName("火车票-深圳-长沙");
        ticket.setTicketNum(10086L);
        ticketRepository.save(ticket);
    }

    /**
     * 锁票操作
     *
     * @param orderDTO
     */
    @PostMapping("lock")
    public Ticket lock(@RequestBody OrderDTO orderDTO) {
        return ticketService.ticketLock(orderDTO);
    }

    @PostMapping("lock2")
    public int lock2(@RequestBody OrderDTO orderDTO) {
        return ticketService.ticketLock2(orderDTO);
    }

    @PostMapping("")
    public Ticket create(@RequestBody Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @HystrixCommand
    @GetMapping("")
    public List<Ticket> getAll() {
        return ticketRepository.findAll();
    }
}
