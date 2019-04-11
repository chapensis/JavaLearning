package com.imooc.example.springaxon.account.event;

import lombok.Data;

@Data
public class AccountWithdrawedEvent {
    private String accountId;

    private Double amount;

    public AccountWithdrawedEvent(String accountId, Double amount) {
        this.accountId = accountId;
        this.amount = amount;
    }
}
