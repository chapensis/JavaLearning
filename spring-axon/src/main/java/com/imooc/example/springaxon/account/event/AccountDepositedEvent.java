package com.imooc.example.springaxon.account.event;

import lombok.Data;

@Data
public class AccountDepositedEvent {
    private String accountId;

    private Double amount;

    public AccountDepositedEvent(String accountId, Double amount) {
        this.accountId = accountId;
        this.amount = amount;
    }
}
