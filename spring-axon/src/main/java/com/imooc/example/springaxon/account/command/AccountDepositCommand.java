package com.imooc.example.springaxon.account.command;

import lombok.Data;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

@Data
public class AccountDepositCommand {

    @TargetAggregateIdentifier
    private String accountId;

    private Double amount;

    public AccountDepositCommand(String accountId, Double amount) {
        this.accountId = accountId;
        this.amount = amount;
    }
}
