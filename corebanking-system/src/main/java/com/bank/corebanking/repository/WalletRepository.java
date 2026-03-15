package com.bank.corebanking.repository;

import com.bank.corebanking.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Wallet findByWalletNumber(String walletNumber);
}