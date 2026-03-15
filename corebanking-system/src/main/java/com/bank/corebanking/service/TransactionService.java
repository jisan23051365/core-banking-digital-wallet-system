package com.bank.corebanking.service;

import com.bank.corebanking.entity.Account;
import com.bank.corebanking.entity.Transaction;
import com.bank.corebanking.repository.AccountRepository;
import com.bank.corebanking.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(AccountRepository accountRepository,
                              TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Transaction transfer(String from,String to,double amount){

        Account sender = accountRepository.findByAccountNumber(from);
        Account receiver = accountRepository.findByAccountNumber(to);

        if(sender.getBalance() < amount)
            throw new RuntimeException("Insufficient Balance");

        sender.setBalance(sender.getBalance()-amount);
        receiver.setBalance(receiver.getBalance()+amount);

        accountRepository.save(sender);
        accountRepository.save(receiver);

        Transaction tx = new Transaction();

        tx.setType("TRANSFER");
        tx.setSource(from);
        tx.setDestination(to);
        tx.setAmount(amount);
        tx.setTimestamp(LocalDateTime.now());

        return transactionRepository.save(tx);
    }
}