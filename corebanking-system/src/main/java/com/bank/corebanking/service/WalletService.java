package com.bank.corebanking.service;

import com.bank.corebanking.entity.Wallet;
import com.bank.corebanking.repository.WalletRepository;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    private final WalletRepository repository;

    public WalletService(WalletRepository repository) {
        this.repository = repository;
    }

    public Wallet createWallet(Wallet wallet){
        return repository.save(wallet);
    }

    public Wallet getWallet(String walletNumber){
        return repository.findByWalletNumber(walletNumber);
    }
}