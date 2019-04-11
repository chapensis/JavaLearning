package com.imooc.example.springaxon.account.command;

import lombok.Data;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

@Data
public class AccountCreateCommand {

    @TargetAggregateIdentifier
    private String accountId;

    public AccountCreateCommand(String accountId) {
        this.accountId = accountId;
    }
}
