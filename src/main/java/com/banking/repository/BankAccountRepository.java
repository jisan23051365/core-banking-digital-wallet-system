package com.banking.repository;

import com.banking.entity.BankAccount;
import com.banking.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    Optional<BankAccount> findByAccountNumber(String accountNumber);

    List<BankAccount> findByCustomerId(Long customerId);

    List<BankAccount> findByCustomerIdAndStatus(Long customerId, AccountStatus status);

    boolean existsByAccountNumber(String accountNumber);
}
