package com.imooc.example.springaxon.account.command;

import lombok.Data;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

@Data
public class AccountWithdrawCommand {

    @TargetAggregateIdentifier
    private String accountId;

    private Double amount;

    public AccountWithdrawCommand(String accountId, Double amount) {
        this.accountId = accountId;
        this.amount = amount;
    }
}
