package com.imooc.example.ticket.dao;

import com.imooc.example.ticket.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findOneByOwner(Long owner);

    Ticket findOneByTicketNum(Long ticketNum);

    @Modifying
    @Query("UPDATE ticket set lock_user = ?1 WHERE lock_user is null and ticket_num = ?2")
    int lockTicket(Long customerId, Long ticketNum);

    @Modifying
    @Query("UPDATE ticket set lock_user = null WHERE lock_user = ?1 and ticket_num = ?2")
    int unlockTicket(Long customerId, Long ticketNum);

    @Modifying
    @Query("UPDATE ticket set owner = ?1, lock_user = null WHERE lock_user = ?1 and ticket_num = ?2")
    int moveTicket(Long customerId, Long ticketNum);

    @Modifying
    @Query("UPDATE ticket set owner = null, lock_user = null WHERE owner = ?1 and ticket_num = ?2")
    int unmoveTicket(Long customerId, Long ticketNum);
}
