package com.banking.repository;

import com.banking.entity.DigitalWallet;
import com.banking.enums.WalletStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DigitalWalletRepository extends JpaRepository<DigitalWallet, Long> {

    Optional<DigitalWallet> findByWalletNumber(String walletNumber);

    List<DigitalWallet> findByCustomerId(Long customerId);

    List<DigitalWallet> findByCustomerIdAndStatus(Long customerId, WalletStatus status);

    boolean existsByWalletNumber(String walletNumber);
}
