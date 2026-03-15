package com.banking.repository;

import com.banking.entity.Transaction;
import com.banking.enums.TransactionStatus;
import com.banking.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByReferenceNumber(String referenceNumber);

    List<Transaction> findBySourceAccountId(Long accountId);

    List<Transaction> findByTargetAccountId(Long accountId);

    @Query("SELECT t FROM Transaction t WHERE t.sourceAccount.id = :accountId OR t.targetAccount.id = :accountId ORDER BY t.createdAt DESC")
    List<Transaction> findAllByAccountId(@Param("accountId") Long accountId);

    List<Transaction> findByWalletId(Long walletId);

    List<Transaction> findByType(TransactionType type);

    List<Transaction> findByStatus(TransactionStatus status);

    @Query("SELECT t FROM Transaction t WHERE t.sourceAccount.customer.id = :customerId OR t.targetAccount.customer.id = :customerId ORDER BY t.createdAt DESC")
    List<Transaction> findAllByCustomerId(@Param("customerId") Long customerId);
}
