package com.bank.corebanking.controller;

import com.bank.corebanking.dto.DepositWithdrawRequest;
import com.bank.corebanking.entity.Account;
import com.bank.corebanking.service.AccountService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @PostMapping
    public Account createAccount(@RequestBody Account account){
        return service.createAccount(account);
    }

    @PostMapping("/deposit")
    public String deposit(@RequestBody DepositWithdrawRequest req){

        service.deposit(req.getAccountNumber(),req.getAmount());
        return "Deposit Successful";
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestBody DepositWithdrawRequest req){

        service.withdraw(req.getAccountNumber(),req.getAmount());
        return "Withdraw Successful";
    }
}