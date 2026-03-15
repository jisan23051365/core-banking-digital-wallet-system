package com.bank.corebanking.service;

import com.bank.corebanking.entity.Account;
import com.bank.corebanking.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    public Account createAccount(Account account) {
        return repository.save(account);
    }

    public Account getAccount(String accountNumber){
        return repository.findByAccountNumber(accountNumber);
    }

    @Transactional
    public void deposit(String accountNumber,double amount){

        Account account = repository.findByAccountNumber(accountNumber);

        account.setBalance(account.getBalance()+amount);

        repository.save(account);
    }

    @Transactional
    public void withdraw(String accountNumber,double amount){

        Account account = repository.findByAccountNumber(accountNumber);

        if(account.getBalance() < amount)
            throw new RuntimeException("Insufficient Balance");

        account.setBalance(account.getBalance()-amount);

        repository.save(account);
    }
}