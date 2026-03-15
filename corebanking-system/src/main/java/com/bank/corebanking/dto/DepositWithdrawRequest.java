package com.bank.corebanking.dto;

import lombok.Data;

@Data
public class DepositWithdrawRequest {

    private String accountNumber;

    private double amount;
}