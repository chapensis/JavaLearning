package com.imooc.example.springaxon.account;

import com.imooc.example.springaxon.account.command.AccountCreateCommand;
import com.imooc.example.springaxon.account.command.AccountDepositCommand;
import com.imooc.example.springaxon.account.command.AccountWithdrawCommand;
import com.imooc.example.springaxon.account.event.AccountCreatedEvent;
import com.imooc.example.springaxon.account.event.AccountDepositedEvent;
import com.imooc.example.springaxon.account.event.AccountWithdrawedEvent;
import lombok.Data;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;

import javax.persistence.Entity;
import javax.persistence.Id;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@Data
@Aggregate
@Entity(name = "tb_account")
public class Account {

    @Id
    private String accountId;

    private Double deposit;

    public Account() {

    }

    @CommandHandler
    public Account(AccountCreateCommand command) {
        apply(new AccountCreatedEvent(command.getAccountId()));
    }

    /**
     * 存款操作
     *
     * @param command
     */
    @CommandHandler
    public void handle(AccountDepositCommand command) {
        apply(new AccountDepositedEvent(command.getAccountId(), command.getAmount()));
    }

    /**
     * 取款操作
     *
     * @param command
     */
    @CommandHandler
    public void handle(AccountWithdrawCommand command) {
        if (this.deposit >= command.getAmount()) {
            apply(new AccountDepositedEvent(command.getAccountId(), command.getAmount()));
        } else {
            throw new IllegalArgumentException("余额不足");
        }
    }

    /**
     * 创建
     *
     * @param event
     */
    @EventSourcingHandler
    public void on(AccountCreatedEvent event) {
        this.accountId = event.getAccountId();
        this.deposit = 0d;
    }

    /**
     * 余额加
     *
     * @param event
     */
    @EventSourcingHandler
    public void on(AccountDepositedEvent event) {
        this.deposit += event.getAmount();
    }

    /**
     * 余额减
     *
     * @param event
     */
    @EventSourcingHandler
    public void on(AccountWithdrawedEvent event) {
        this.deposit -= event.getAmount();
    }
}
